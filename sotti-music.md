너는 시니어 백엔드 엔지니어다. 아래 요구사항으로 “소띠뮤직” 백엔드 설계/구현 문서를 만들어라.
중요: 기존 소띠퀴즈(기존 도메인/엔드포인트/DB)는 절대 수정하거나 깨뜨리지 말고, 같은 프로젝트 내에 신규 모듈/패키지로만 추가하라.

[기술/전제]
- Spring Boot + Kotlin
- DB는 기존 프로젝트와 동일(문서에 Mongo/JPA 둘 다 가능하면 선택지로 적되, 최종안은 하나로 고정해라)
- Admin 인증은 기존 프로젝트 방식 그대로 재사용(새 인증 추가 금지)
- Public API는 무인증 가능
- 세션 저장 없음. 정답 검증은 signed questionToken으로 처리
- Access log 정도는 서버 로그로 남길 수 있게 권장(필수는 아님)

[MVP 기능]
1) 트랙 관리(관리자)
- music_track 스키마:
    - trackId(PK), youtubeVideoId(unique), startSeconds(int), thumbnailUrl, status(ACTIVE/INACTIVE), title/category/difficulty(optional), createdAt/updatedAt
- API:
    - POST /api/v1/music-quiz/admin/tracks
    - GET /api/v1/music-quiz/admin/tracks
    - PATCH /api/v1/music-quiz/admin/tracks/{trackId} (startSeconds/status 부분수정)

2) 문제 생성(Stateless)
- GET /api/v1/music-quiz/question
- Response:
    - questionToken(서명), previewSeconds=5
    - youtube: videoId, startSeconds
    - choices: 4개(trackId + thumbnailUrl)
- 동작:
    - ACTIVE 트랙 중 정답 1 + 오답 3 랜덤(정답 제외)
    - 옵션으로 category/difficulty 필터, excludeTrackIds 처리
    - 트랙 수 부족 시 에러코드 정의

3) 정답 체크(Stateless)
- POST /api/v1/music-quiz/answer
- Request: questionToken + selectedTrackId
- Response: isCorrect + correctTrackId
- questionToken:
    - payload: correctTrackId, choiceTrackIds[4], issuedAt, expiresAt(10분)
    - 서명(HMAC 또는 JWT)
    - 이거 꼭 필요? 필요없다면 스킵해도됨 서명같은건 MVP니깐

[선택 기능]
- X-API-ID? 기존껄 사용 헤더를 받을 수 있게 해두고, 필요하면 client allowlist로 특정 카테고리 추가 노출 가능하게 확장 여지 설계(단 MVP 구현은 최소)

[산출물]
- API 명세(요청/응답 JSON)
- DTO/Service/Repository 네이밍(Java 컨벤션: 클래스는 명사, 메서드는 동사, boolean은 is/has)
- 패키지 구조 제안(기존과 충돌 없이)
- 에러 코드/예외 정책
- 랜덤 추출 구현 가이드(Mongo면 $sample, RDB면 전략)
- questionToken signer/verifier 설계와 만료 처리
- 인덱스/마이그레이션/운영 체크리스트