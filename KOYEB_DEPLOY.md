# Koyeb 배포 가이드

## 🚀 Koyeb 배포 단계별 가이드

### 1. GitHub Repository 준비
```bash
# 코드를 GitHub에 push
git add .
git commit -m "Add Koyeb deployment configuration"
git push origin main
```

### 2. Koyeb 계정 생성 및 연결
1. [Koyeb.com](https://www.koyeb.com) 계정 생성
2. GitHub 연결

### 3. 새 서비스 생성
1. **Create Service** 클릭
2. **Deploy from GitHub** 선택
3. Repository: `your-username/sotti-music-api` 선택
4. Branch: `main` 선택

### 4. 빌드 설정
```yaml
Build Method: Docker
Dockerfile path: ./Dockerfile
Build context: Root directory
```

### 5. 환경변수 설정
**Environment Variables** 섹션에서:
```bash
MONGODB_URI=mongodb+srv://sotti:sotti!!@cluster0.vrrqxcf.mongodb.net/sotti-product?appName=Cluster0
SPRING_PROFILES_ACTIVE=production
```

### 6. 서비스 설정
```yaml
Service name: sotti-music-api
Port: 8080
Health check: /actuator/health
Min instances: 1
Max instances: 3
```

### 7. 배포 실행
1. **Deploy** 클릭
2. 빌드 로그 확인
3. 배포 완료 후 URL 확인

## 📋 배포 후 확인 사항

### API 테스트
```bash
# Health Check
curl https://your-app.koyeb.app/actuator/health

# API 테스트
curl https://your-app.koyeb.app/api/v1/music-quiz

# Swagger UI 접속
https://your-app.koyeb.app/swagger-ui/index.html
```

### 로그 모니터링
```bash
# Koyeb 대시보드에서 실시간 로그 확인
# - Logs 탭에서 애플리케이션 로그 확인
# - Metrics 탭에서 성능 지표 모니터링
```

## ⚙️ 자동 배포 설정

### GitHub Actions (선택사항)
```yaml
# .github/workflows/deploy.yml
name: Deploy to Koyeb
on:
  push:
    branches: [main]
jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Deploy to Koyeb
        # Koyeb CLI를 사용한 배포 자동화
```

## 🔧 트러블슈팅

### 일반적인 문제들

1. **빌드 실패**
   - Dockerfile 경로 확인
   - 빌드 컨텍스트 설정 확인

2. **환경변수 문제**
   - MongoDB URI 형식 검증
   - 환경변수 이름 확인

3. **Health Check 실패**
   - `/actuator/health` 엔드포인트 확인
   - MongoDB 연결 상태 확인

4. **메모리 부족**
   - Java 힙 메모리 설정 조정
   - 인스턴스 크기 업그레이드

### 로그 확인 방법
```bash
# Koyeb CLI 설치 후
koyeb logs <service-name>
```

## 💰 비용 최적화

### 무료 티어 활용
- 1개 서비스 무료
- 512MB RAM, 0.1 CPU
- 100GB 트래픽/월

### 스케일링 설정
```yaml
Min instances: 1  # 최소 인스턴스
Max instances: 3  # 트래픽에 따라 자동 스케일링
```

## 🔐 보안 설정

### 환경변수 보안
- 민감한 정보는 Koyeb Secret으로 관리
- 정기적인 DB 패스워드 변경
- CORS 설정 추가 (필요시)

### 도메인 설정
```bash
# 커스텀 도메인 연결
# Koyeb 대시보드 > Domains에서 설정
```