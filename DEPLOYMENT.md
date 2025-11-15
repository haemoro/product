# 배포 가이드

## 환경변수 설정

배포 시 다음 환경변수를 설정해야 합니다:

### 필수 환경변수
```bash
MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/database?options
```

### 선택 환경변수
```bash
PORT=8080                    # 서버 포트 (기본값: 8080)
SPRING_PROFILES_ACTIVE=production  # 프로파일
```

## 로컬 개발 환경

1. `.env` 파일 생성:
```bash
MONGODB_URI=mongodb+srv://username:password@cluster.mongodb.net/database?options
```

2. 애플리케이션 실행:
```bash
./gradlew bootRun
```

## 배포 환경별 설정

### Docker
```dockerfile
ENV MONGODB_URI=mongodb+srv://...
```

### Heroku
```bash
heroku config:set MONGODB_URI="mongodb+srv://..."
```

### Koyeb
```bash
# 1. GitHub 연결 후 자동 배포
# 2. 환경변수 설정
MONGODB_URI="mongodb+srv://..."
SPRING_PROFILES_ACTIVE=production

# 3. 빌드 설정: Docker
# 4. Health Check: /actuator/health
```

### AWS/GCP/Azure
환경변수 또는 Secret Manager 사용

## 보안 주의사항
- `.env` 파일은 절대 Git에 커밋하지 마세요
- 프로덕션에서는 Secret Manager 사용을 권장합니다
- 정기적으로 DB 패스워드를 변경하세요