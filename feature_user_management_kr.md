# 사용자 관리 기능 - PowerPoint 슬라이드

---
## 슬라이드 1: 사용자 관리 개요
- v 종합적인 사용자 계정 시스템
- v 안전한 인증 및 권한 부여
- v 프로필 맞춤 설정 기능
- v 모든 플랫폼 기능과의 통합
- 크로스 플랫폼 일관성

---
## 슬라이드 2: 등록 프로세스
- v 사용자 친화적인 가입 양식
- 이메일 인증 시스템
- 비밀번호 강도 요구사항
- 서비스 약관 동의
- 계정 활성화 워크플로우

---
## 슬라이드 3: 인증 시스템 메커니즘
- v Spring Security 기반 로그인 프로세스
  - v UserController가 로그인 페이지 제공
  - v Spring Security가 인증 처리
  - v PrincipalDetailService가 사용자 정보 로드
  - v PrincipalDetail이 UserDetails 구현
- v BCrypt 알고리즘으로 비밀번호 암호화
  - v 단방향 해시 함수로 원본 복구 불가능
  - v 솔트(salt) 자동 적용으로 레인보우 테이블 공격 방지
- v 세션 기반 사용자 상태 관리
  - v SecurityContextHolder에 인증 정보 저장
  - v 최대 1개 세션 제한으로 동시 로그인 방지
- 자동 로그인(Remember Me) 기능
- 다중 인증 옵션 지원

---
## 슬라이드 4: 사용자 프로필 관리 메커니즘
- v 개인 정보 관리 프로세스
  - v UserController의 userInfo 메소드로 정보 조회
  - v SecurityContextHolder에서 현재 사용자 정보 획득
  - v UserService를 통한 데이터 처리
- 프로필 사진 업로드 및 크롭 기능
- v 환경 설정 관리 시스템
  - v UserDTO와 UserEntity 간 변환 로직
  - v 설정 변경 시 즉시 DB 반영
- 개인정보 보호 제어
- 계정 연결 옵션

---
## 슬라이드 5: 권한 부여 프레임워크 메커니즘
- v 역할 기반 접근 제어(RBAC)
  - v UserEntity의 role 필드로 권한 관리
  - v "ROLE_USER", "ROLE_ADMIN" 등 역할 구분
  - v CustomSecurityConfig에서 URL별 접근 권한 설정
- v 권한 관리 시스템
  - v GrantedAuthority 인터페이스 구현으로 권한 표현
  - v PrincipalDetail의 getAuthorities() 메소드로 권한 제공
- v 사용자 권한 수준
  - v SecurityFilterChain에서 URL 패턴별 접근 제어
  - v hasRole() 메소드로 특정 역할 요구
- v 콘텐츠 접근 제한
- 보안 정책 시행

---
## 슬라이드 6: 비밀번호 관리 메커니즘
- 안전한 비밀번호 재설정 워크플로우
  - 이메일 인증 후 토큰 기반 재설정
  - 제한 시간 설정으로 보안 강화
- 비밀번호 변경 기능
  - 현재 비밀번호 확인 후 변경 가능
  - v BCryptPasswordEncoder로 새 비밀번호 암호화
- 비밀번호 이력 추적
  - 이전 비밀번호 재사용 방지
  - 주기적 변경 유도
- v 암호화 표준
  - v BCrypt 알고리즘 사용
  - v 보안 강도 조정 가능
- 보안 질문 복구

---
## 슬라이드 7: 사용자 대시보드 메커니즘
- v 개인화된 사용자 홈페이지
  - v 로그인 사용자 정보 기반 콘텐츠 필터링
  - v SecurityContextHolder에서 사용자 정보 획득
- 활동 요약
  - 사용자 활동 로그 DB 저장 및 표시
  - 최근 활동 우선 정렬
- 최근 상호작용
  - 레시피 조회, 리뷰 등 활동 추적
  - JPA 관계 매핑으로 연관 데이터 조회
- 저장된 레시피
  - 사용자-레시피 간 다대다 관계 매핑
  - 즐겨찾기 기능 구현
- 주문 내역 접근
  - 사용자 ID 기반 주문 내역 조회
  - 페이징 처리로 대량 데이터 효율적 표시

---
## 슬라이드 8: 계정 설정 메커니즘
- 알림 환경설정
  - 사용자별 알림 설정 DB 저장
  - 이벤트 발생 시 설정 기반 알림 전송
- 언어 및 지역 설정
  - 로케일 정보 세션 또는 DB 저장
  - 국제화(i18n) 지원
- 접근성 옵션
  - 사용자별 UI 설정 저장
  - 접근성 향상 기능 제공
- 연결된 계정 관리
  - OAuth 연동 지원
  - 소셜 로그인 통합
- v 개인 정보 및 데이터 설정
  - 개인정보 다운로드 및 삭제 기능

---
## 슬라이드 9: 프론트엔드 구현 메커니즘
- v 반응형 등록 양식
  - v HTML, CSS, JavaScript 활용
  - v Bootstrap 프레임워크 적용
  - v 다양한 화면 크기 지원
- v 직관적인 프로필 편집 인터페이스
  - v Thymeleaf 템플릿 엔진으로 동적 콘텐츠 생성
  - AJAX 요청으로 비동기 데이터 처리
- 실시간 유효성 검사 피드백
  - JavaScript 기반 클라이언트 측 검증
  - v 서버 측 검증 결과 즉시 표시
- v 사용자 친화적 오류 메시지
  - v 상황별 맞춤 오류 안내
  - 문제 해결 가이드 제공
- v 사용자 기능 간 원활한 탐색
  - v 논리적 UI 구조
  - v 직관적 네비게이션 설계

---
## 슬라이드 10: 백엔드 구현 메커니즘
- v 사용자 엔티티 구조
  - v JPA Entity 클래스로 DB 테이블 매핑
  - v BaseEntity 상속으로 감사 필드(생성일, 수정일) 자동화
  - v 관계 매핑으로 다른 엔티티와 연결
- v 인증 서비스
  - v Spring Security 프레임워크 활용
  - v UserDetailsService 구현으로 사용자 정보 로드
  - v AuthenticationManager로 인증 처리
- v 프로필 서비스
  - v UserService 인터페이스 정의
  - v UserServiceImpl에서 비즈니스 로직 구현
  - v DTO-Entity 변환 로직
- v 보안 구성
  - v CustomSecurityConfig 클래스에서 보안 규칙 정의
  - v SecurityFilterChain 구성
  - v 정적 리소스 보안 예외 처리
- v 데이터 유효성 검사 및 정제
  - v Bean Validation으로 입력 검증
  - XSS 방지 필터링
  - SQL 인젝션 방지

---
## 슬라이드 11: 데이터베이스 스키마 메커니즘
- v 사용자 테이블 구조
  - v id(PK), username, password, email 등 필수 필드
  - v role 필드로 권한 관리
  - v point 필드로 포인트 시스템 구현
- v 프로필 정보 저장
  - v 사용자 테이블에 통합 저장
  - v 확장 가능한 구조 설계
- 인증 기록
  - 로그인 이력 별도 테이블 관리
  - IP, 기기 정보 등 보안 데이터 저장
- v 세션 관리
  - v Spring Session 활용 가능
  - DB 기반 세션 저장소 구현
- v 다른 엔티티와의 관계
  - v 주문, 리뷰, 레시피 등과 @OneToMany 관계
  - v 외래 키 제약조건으로 데이터 무결성 유지

---
## 슬라이드 12: 통합 지점 메커니즘
- v 레시피 생성 귀속
  - v 레시피 엔티티에 사용자 참조(ManyToOne)
  - v 작성자 정보 자동 기록
- v 장바구니 연결
  - v 사용자-장바구니 일대일 관계
  - v 세션 기반 임시 장바구니의 사용자 계정 연결
- v 리뷰 및 평가 시스템
  - v 리뷰 엔티티에 사용자 참조
  - v 사용자별 리뷰 이력 관리
- v 주문 내역 추적
  - v 주문 엔티티에 사용자 참조
  - v 사용자별 주문 내역 조회 기능
- v 고객 서비스 문의
  - v 문의 엔티티에 사용자 참조
  - v 사용자별 문의 내역 관리
