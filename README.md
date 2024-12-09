## Blog 프로젝트

**프론트엔드**
- SvelteKit를 사용하여 빠르고 반응성 좋은 사용자 인터페이스 구현

**백엔드**
- Spring Boot 기반으로 REST API 제공, MySQL을 주 데이터베이스로 사용하며 Redis를 통해 Refresh Token 관리 

**이미지 서버**
- 별도 분리되어 이미지 저장 및 관리. API Key 인증 방식 적용, ROLE_ADMIN 계정만 이미지 업로드 가능

**상태 확인**
- Spring Boot Scheduler를 활용하여 서버 상태 확인 및 Discord 알림

## 구조
**Docker 기반 구현**
- Docker Compose로 환경 구성 간편하게 관리
- 백엔드, 프론트엔드, 이미지 서버 각각 컨테이너화하여 독립적인 실행 및 배포 가능
         
**CI/CD Pipeline (Jenkins)** 
- Checkout:  GitHub에서 소스 코드 가져오기
- Add Env:  환경 변수 파일 복사 (프론트엔드, 백엔드 설정)
- Build:  Docker Compose를 사용하여 이미지 빌드 및 실행
- Post:  성공/실패 여부, 실행 시간 등을 Discord webhook으로 전송
         
## 기술 선택 이유 
- Docker:  환경 재현성 확보 및 배포 간소화
- Jenkins: 테스트 및 배포 간소화
- Spring Boot:  안정적인 API 구축 및 빠른 개발 속도 제공
- Redis:  Refresh Token 관리
- MySQL:  데이터 저장 및 관리

## 링크
- https://github.com/fdoom/blog : Blog 프로젝트 GitHub
- https://github.com/fdoom/blog-image-db : 이미지 서버 프로젝트 GitHub
- https://blog.noteit.cyou/post/1: 프로젝트 추가 설명
- https://blog.noteit.cyou/post/89: Raspberry pi 5 8GB 모델 서버 성능
- https://blog.noteit.cyou/post/91: SSL Server Test(SSL Labs) 결과
