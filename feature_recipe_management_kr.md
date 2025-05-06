# 레시피 관리 기능 - PowerPoint 슬라이드

---
## 슬라이드 1: 레시피 관리 개요
- v 종합적인 레시피 생성 및 관리 시스템
- v 가정 요리사와 전문가를 위한 직관적인 사용자 인터페이스
- v 고급 검색 및 발견 기능
- v 카테고리화 및 태그 시스템
- v 재료 및 쇼핑 기능과의 통합

---
## 슬라이드 2: 레시피 생성 메커니즘
- v 단계별 레시피 빌더 인터페이스
  - v RecipeController의 recipeRegister 메소드로 구현
  - v MultipartHttpServletRequest로 이미지 파일 처리
  - v 사용자 인증 정보는 PrincipalDetail에서 획득
- v 재료 수량 및 단위 관리
  - v RecipeIngredientsEntity로 레시피-재료 관계 매핑
  - v 재료별 수량 정보 저장 및 관리
- v 조리 단계 관리
  - v RecipeStepEntity로 순서대로 단계 저장
  - v 각 단계별 이미지 및 설명 관리
- v 다중 이미지 업로드 기능
  - v FileService를 통한 이미지 저장
  - v UUID 기반 고유 파일명 생성으로 충돌 방지
- v 포인트 보상 시스템
  - v 레시피 등록 시 사용자에게 100포인트 자동 지급
  - v UserEntity의 addPoints 메소드로 포인트 증가

---
## 슬라이드 3: 레시피 조회 메커니즘
- v 깔끔하고 집중할 수 있는 레시피 표시
  - v RecipeController의 recipeRead 메소드로 구현
  - v 레시피, 단계, 재료 정보 통합 조회
- v 모든 기기에 대응하는 반응형 디자인
  - v 부트스트랩 기반 반응형 레이아웃
  - v 화면 크기에 따른 콘텐츠 재배치
- 인쇄 친화적 포맷팅
  - 인쇄용 CSS 스타일 적용
  - 불필요한 요소 자동 숨김
- v 조리 모드와 단계 분리
  - v 단계별 이미지와 설명 분리 표시
  - v 현재 단계 강조 표시
- v 리뷰 및 평점 시스템 통합
  - v ReviewService로 레시피별 평균 평점 계산
  - v 페이징 처리된 리뷰 목록 표시

---
## 슬라이드 4: 검색 및 발견 메커니즘
- v 고급 필터링 옵션
  - v RecipeController의 recipeList 메소드로 구현
  - v 카테고리, 검색어, 검색 유형 파라미터 처리
- v 키워드 및 자연어 검색
  - v RecipeServiceImpl의 searchRecipes 메소드로 구현
  - v 제목 또는 작성자 기준 검색 지원
- v 카테고리 기반 브라우징
  - v RecipeRepository의 findByCategory 메소드 활용
  - v 카테고리별 레시피 목록 조회
- v 인기 및 트렌드 레시피 쇼케이스
  - v RecipeServiceImpl의 getRecentRecipes 메소드로 구현
  - v 최근 등록된 레시피 우선 표시
- v 검색 결과 정렬 및 페이징
  - v Spring Data JPA의 Pageable 인터페이스 활용
  - v 페이지 크기 및 정렬 기준 설정 가능

---
## 슬라이드 5: 레시피 관리 메커니즘
- v 레시피 수정 기능
  - v RecipeController의 recipeUpdate 메소드로 구현
  - v 기존 레시피 정보 로드 후 수정 사항 적용
  - v 트랜잭션 처리로 데이터 일관성 유지
- v 레시피 삭제 기능
  - v RecipeController의 recipeDelete 메소드로 구현
  - v 관련 이미지 파일 자동 삭제
  - v 연관 데이터(단계, 재료) 함께 삭제
- v 버전 관리
  - v 수정 시간 기록으로 변경 이력 추적
  - v 이전 버전 이미지 자동 삭제 및 교체
- v 개인 노트 및 수정
  - v 사용자별 레시피 관리
  - v 자신의 레시피만 수정/삭제 권한 부여
- v 공유 기능
  - v 레시피 URL 공유 가능
  - 소셜 미디어 공유 링크 제공

---
## 슬라이드 6: 재료 관리 메커니즘
- v 재료 데이터베이스 통합
  - v IngredientRepository로 재료 정보 관리
  - v 재료명 기준 조회 기능 제공
- v 레시피-재료 연결 시스템
  - v RecipeIngredientsEntity로 다대다 관계 구현
  - v 레시피별 필요 재료 및 수량 관리
- v 장바구니 연동
  - v 레시피 등록 시 CartEntity 자동 생성
  - v RecipeIngredientsEntity와 CartEntity 연결
  - v 재료 총 가격 자동 계산
- v 재료 수량 관리
  - v 레시피 등록/수정 시 재료별 수량 설정
  - v 장바구니에 추가 시 수량 반영
- v 재료 검색 및 선택
  - v 레시피 등록/수정 시 전체 재료 목록 제공
  - v 재료명 기준 검색 및 선택 기능

---
## 슬라이드 7: 레시피 카테고리화 메커니즘
- v 카테고리 시스템
  - v RecipeEntity의 category 필드로 관리
  - v 기본 카테고리: 밥, 국, 메인반찬, 밑반찬, 면
- v 카테고리 기반 검색
  - v RecipeRepository의 findByCategory 메소드 활용
  - v 카테고리별 레시피 목록 조회
- v 카테고리와 키워드 조합 검색
  - v RecipeServiceImpl의 searchByCategoryAndKeyword 메소드
  - v 카테고리 내에서 제목 또는 작성자 검색
- v 카테고리별 레시피 표시
  - v 카테고리별 레시피 목록 페이지 제공
  - v 카테고리 필터링 UI 제공
- v 카테고리 관리
  - v 레시피 등록/수정 시 카테고리 선택 필수
  - v 관리자 설정 카테고리 목록 사용

---
## 슬라이드 8: 소셜 기능 메커니즘
- v 사용자 리뷰 시스템
  - v ReviewEntity로 레시피 리뷰 관리
  - v 별점 및 텍스트 리뷰 지원
- v 평점 시스템
  - v 1-5점 별점 부여 가능
  - v RecipeServiceImpl에서 평균 평점 계산
  - v 레시피 목록에 평균 평점 표시
- v 리뷰 정렬 및 필터링
  - v 최신순/평점순 정렬 지원
  - v 페이징 처리로 대량 리뷰 효율적 표시
- v 리뷰 페이징 처리
  - v Spring Data JPA의 Pageable 인터페이스 활용
  - v 페이지 크기 및 정렬 기준 설정 가능
- v 레시피-리뷰 연결
  - v RecipeEntity와 ReviewEntity 간 일대다 관계
  - v 레시피 삭제 시 연관 리뷰 자동 삭제(cascade)

---
## 슬라이드 9: 프론트엔드 구현 메커니즘
- v 레시피 등록 폼
  - v Thymeleaf 템플릿 엔진으로 동적 폼 생성
  - v JavaScript로 재료/단계 동적 추가/삭제
  - v 이미지 미리보기 기능
- v 레시피 조회 화면
  - v 반응형 레이아웃으로 다양한 기기 지원
  - v 단계별 이미지와 설명 구조화 표시
  - v 재료 목록 및 수량 정보 표시
- v 레시피 수정 화면
  - v 기존 데이터 자동 로드
  - v 이미지 교체 또는 유지 옵션
  - v 재료/단계 추가/삭제/수정 기능
- v 레시피 목록 화면
  - v 그리드 레이아웃으로 여러 레시피 표시
  - v 썸네일, 제목, 작성자, 평점 정보 제공
  - v 검색 및 필터링 UI 제공
- v 모바일 최적화
  - v 미디어 쿼리로 화면 크기별 레이아웃 조정
  - v 터치 친화적 UI 요소 설계

---
## 슬라이드 10: 백엔드 구현 메커니즘
- v 레시피 엔티티 구조
  - v RecipeEntity 클래스로 DB 테이블 매핑
  - v 제목, 카테고리, 이미지, 작성자 등 필드 포함
  - v 단계 및 리뷰와 일대다 관계 설정
- v 레시피 서비스 계층
  - v RecipeService 인터페이스 정의
  - v RecipeServiceImpl에서 비즈니스 로직 구현
  - v 트랜잭션 관리로 데이터 일관성 유지
- v 레시피 컨트롤러
  - v RecipeController에서 HTTP 요청 처리
  - v 사용자 인증 정보 확인 및 권한 검증
  - v 서비스 계층 호출 및 결과 처리
- v 파일 업로드 처리
  - v FileService로 이미지 파일 저장 및 관리
  - v UUID 기반 고유 파일명 생성
  - v 파일 시스템에 이미지 저장
- v 데이터 검증 및 정제
  - v 입력값 유효성 검사
  - v XSS 방지 필터링
  - v 트랜잭션 처리로 데이터 일관성 유지

---
## 슬라이드 11: 데이터베이스 스키마 메커니즘
- v 레시피 테이블 구조
  - v id(PK), title, category, mainImage 등 필드
  - v user_id 외래키로 작성자 참조
  - v createdAt 필드로 생성 시간 기록
- v 레시피 단계 테이블
  - v id(PK), recipe_id(FK), stepNumber, content, imageName 필드
  - v recipe_id로 RecipeEntity 참조
  - v stepNumber로 단계 순서 관리
- v 레시피 재료 테이블
  - v id(PK), recipeId(FK), ingredientId(FK), cartId(FK), quantity 필드
  - v 레시피, 재료, 장바구니 간 관계 매핑
  - v 재료별 수량 정보 저장
- v 이미지 저장 방식
  - v 파일 시스템에 이미지 저장
  - v DB에는 이미지 경로만 저장
  - v UUID 기반 고유 파일명으로 충돌 방지
- v 사용자 및 리뷰 엔티티와의 관계
  - v 레시피-사용자: 다대일 관계(ManyToOne)
  - v 레시피-리뷰: 일대다 관계(OneToMany)
  - v 레시피-단계: 일대다 관계(OneToMany)
  - v 레시피-재료: 일대다 관계(OneToMany)

---
## 슬라이드 12: 통합 지점 메커니즘
- v 사용자 인증 및 권한
  - v Spring Security로 사용자 인증
  - v 레시피 작성자만 수정/삭제 권한 부여
  - v PrincipalDetail에서 현재 사용자 정보 획득
- v 장바구니 연동
  - v 레시피 등록 시 재료 자동 장바구니 추가
  - v CartEntity와 RecipeIngredientsEntity 연결
  - v 재료 총 가격 자동 계산
- v 재료 관리 연동
  - v IngredientRepository로 재료 정보 조회
  - v 레시피별 필요 재료 및 수량 관리
- v 리뷰 및 평점 시스템
  - v ReviewService로 레시피별 리뷰 관리
  - v 평균 평점 계산 및 표시
  - v 리뷰 정렬 및 페이징 처리
- v 포인트 시스템 연동
  - v 레시피 등록 시 사용자에게 포인트 지급
  - v UserEntity의 addPoints 메소드로 포인트 증가
  - 포인트로 쇼핑 시 할인 혜택 제공
