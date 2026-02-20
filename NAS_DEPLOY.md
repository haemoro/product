# Synology NAS Docker 배포 가이드

## 개요

로컬에서 Docker 이미지를 빌드하고, SSH 파이프를 통해 Synology NAS에 전송하여 배포하는 방식.
Private Registry 없이 `docker save | ssh docker load` 패턴을 사용한다.

## 사전 준비

### 1. Synology NAS 설정

#### SSH 활성화
- DSM > 제어판 > 터미널 및 SNMP > SSH 서비스 활성화
- 기본 포트: 22

#### Docker 설치
- DSM > 패키지 센터 > Container Manager 설치

#### NAS 아키텍처 확인
```bash
ssh admin@nas "uname -m"
# x86_64 → linux/amd64 (기본값)
# aarch64 → linux/arm64 (PLATFORM=linux/arm64 으로 오버라이드)
```

### 2. SSH 키 설정

```bash
# SSH 키 생성 (없는 경우)
ssh-keygen -t ed25519 -C "nas-deploy"

# NAS에 공개키 복사
ssh-copy-id -p 22 admin@nas

# 연결 테스트
ssh admin@nas "echo ok"
```

#### ~/.ssh/config 설정 (권장)
```
Host nas
    HostName 192.168.1.xxx
    User admin
    Port 22
    IdentityFile ~/.ssh/id_ed25519
```

### 3. NAS 환경변수 설정

NAS에 직접 `.env` 파일을 생성한다 (보안을 위해 로컬에서 전송하지 않음):

```bash
ssh admin@nas "mkdir -p /volume1/docker/sotti-product"
ssh admin@nas "vi /volume1/docker/sotti-product/.env"
```

`.env.nas.example`을 참고하여 값을 설정한다.

### 4. 로컬 Docker buildx 확인

```bash
docker buildx version
# 없으면: brew install docker-buildx
```

## 배포

### 기본 배포
```bash
./deploy-nas.sh
```

### 버전 태그 배포
```bash
IMAGE_TAG=v1.0.0 ./deploy-nas.sh
```

### compose 파일 업데이트 포함
```bash
./deploy-nas.sh --update-compose
```

### 빌드 없이 재배포 (이미 이미지가 있는 경우)
```bash
./deploy-nas.sh --skip-build
```

### NAS 주소 오버라이드
```bash
NAS_HOST=192.168.1.100 ./deploy-nas.sh
```

## 운영

### 로그 확인
```bash
ssh admin@nas "cd /volume1/docker/sotti-product && docker compose logs -f"
ssh admin@nas "cd /volume1/docker/sotti-product && docker compose logs --tail 100"
```

### 컨테이너 상태
```bash
ssh admin@nas "docker ps | grep sotti-product"
```

### 컨테이너 재시작
```bash
ssh admin@nas "cd /volume1/docker/sotti-product && docker compose restart"
```

### 컨테이너 중지
```bash
ssh admin@nas "cd /volume1/docker/sotti-product && docker compose down"
```

### 헬스체크
```bash
curl http://nas:8080/actuator/health
```

## 롤백

이전 버전으로 롤백하려면 태그를 지정하여 재배포:

```bash
# 이전에 v1.0.0으로 배포했다면
IMAGE_TAG=v0.9.0 ./deploy-nas.sh --skip-build
```

`--skip-build`는 NAS에 해당 태그 이미지가 이미 로드되어 있을 때 사용.
이미지가 없으면 `--skip-build` 없이 다시 빌드해야 한다.

## 트러블슈팅

### SSH 연결 실패
```bash
# SSH 키 권한 확인
chmod 700 ~/.ssh
chmod 600 ~/.ssh/id_ed25519

# NAS에서 SSH 서비스 활성화 확인
# DSM > 제어판 > 터미널 및 SNMP
```

### docker 명령어 권한 에러
```bash
# NAS에서 admin 유저를 docker 그룹에 추가
ssh admin@nas "sudo synogroup --add docker admin"
# SSH 재접속 필요
```

### 이미지 전송 중 실패
```bash
# 디스크 공간 확인
ssh admin@nas "df -h /volume1"

# 사용하지 않는 이미지 정리
ssh admin@nas "docker image prune -f"
```

### 컨테이너 시작 실패
```bash
# 로그 확인
ssh admin@nas "cd /volume1/docker/sotti-product && docker compose logs"

# .env 파일 확인
ssh admin@nas "cat /volume1/docker/sotti-product/.env"

# 포트 충돌 확인
ssh admin@nas "netstat -tlnp | grep 8080"
```

### 메모리 부족
```bash
# 컨테이너 메모리 사용량 확인
ssh admin@nas "docker stats sotti-product --no-stream"

# docker-compose.nas.yml에서 메모리 제한 조정 후
./deploy-nas.sh --update-compose
```

## 파일 구조

```
project/
├── deploy-nas.sh           # 배포 스크립트 (로컬 실행)
├── docker-compose.nas.yml  # NAS compose 설정 (버전 관리용)
├── .env.nas.example        # 환경변수 템플릿
├── Dockerfile              # Docker 빌드 (기존, Koyeb/NAS 공용)
└── NAS_DEPLOY.md           # 이 문서

NAS /volume1/docker/sotti-product/
├── docker-compose.yml      # deploy-nas.sh가 업로드
└── .env                    # NAS에서 직접 생성 (시크릿)
```
