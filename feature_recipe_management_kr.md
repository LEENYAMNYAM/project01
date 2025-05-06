# 레시피 관리 기능 - PowerPoint 슬라이드

---
## 슬라이드 1: 레시피 관리 개요
- 종합적인 레시피 생성 및 관리 시스템
- 가정 요리사와 전문가를 위한 직관적인 사용자 인터페이스
- 고급 검색 및 발견 기능
- 카테고리화 및 태그 시스템
- 재료 및 쇼핑 기능과의 통합

---
## 슬라이드 2: 레시피 생성 메커니즘
- 단계별 레시피 빌더 인터페이스
  - RecipeController의 recipeRegister 메소드로 구현
  - MultipartHttpServletRequest로 이미지 파일 처리
  - 사용자 인증 정보는 PrincipalDetail에서 획득
- 재료 수량 및 단위 관리
  - RecipeIngredientsEntity로 레시피-재료 관계 매핑
  - 재료별 수량 정보 저장 및 관리
- 조리 단계 관리
  - RecipeStepEntity로 순서대로 단계 저장
  - 각 단계별 이미지 및 설명 관리
- 다중 이미지 업로드 기능
  - FileService를 통한 이미지 저장
  - UUID 기반 고유 파일명 생성으로 충돌 방지
- 포인트 보상 시스템
  - 레시피 등록 시 사용자에게 100포인트 자동 지급
  - UserEntity의 addPoints 메소드로 포인트 증가

---
## 슬라이드 3: 레시피 조회 메커니즘
- 깔끔하고 집중할 수 있는 레시피 표시
  - RecipeController의 recipeRead 메소드로 구현
  - 레시피, 단계, 재료 정보 통합 조회
- 모든 기기에 대응하는 반응형 디자인
  - 부트스트랩 기반 반응형 레이아웃
  - 화면 크기에 따른 콘텐츠 재배치
- 인쇄 친화적 포맷팅
  - 인쇄용 CSS 스타일 적용
  - 불필요한 요소 자동 숨김
- 조리 모드와 단계 분리
  - 단계별 이미지와 설명 분리 표시
  - 현재 단계 강조 표시
- 리뷰 및 평점 시스템 통합
  - ReviewService로 레시피별 평균 평점 계산
  - 페이징 처리된 리뷰 목록 표시

---
## 슬라이드 4: 검색 및 발견 메커니즘
- 고급 필터링 옵션
  - RecipeController의 recipeList 메소드로 구현
  - 카테고리, 검색어, 검색 유형 파라미터 처리
- 키워드 및 자연어 검색
  - RecipeServiceImpl의 searchRecipes 메소드로 구현
  - 제목 또는 작성자 기준 검색 지원
- 카테고리 기반 브라우징
  - RecipeRepository의 findByCategory 메소드 활용
  - 카테고리별 레시피 목록 조회
- 인기 및 트렌드 레시피 쇼케이스
  - RecipeServiceImpl의 getRecentRecipes 메소드로 구현
  - 최근 등록된 레시피 우선 표시
- 검색 결과 정렬 및 페이징
  - Spring Data JPA의 Pageable 인터페이스 활용
  - 페이지 크기 및 정렬 기준 설정 가능

---
## 슬라이드 5: 레시피 관리 메커니즘
- 레시피 수정 기능
  - RecipeController의 recipeUpdate 메소드로 구현
  - 기존 레시피 정보 로드 후 수정 사항 적용
  - 트랜잭션 처리로 데이터 일관성 유지
- 레시피 삭제 기능
  - RecipeController의 recipeDelete 메소드로 구현
  - 관련 이미지 파일 자동 삭제
  - 연관 데이터(단계, 재료) 함께 삭제
- 버전 관리
  - 수정 시간 기록으로 변경 이력 추적
  - 이전 버전 이미지 자동 삭제 및 교체
- 개인 노트 및 수정
  - 사용자별 레시피 관리
  - 자신의 레시피만 수정/삭제 권한 부여
- 공유 기능
  - 레시피 URL 공유 가능
  - 소셜 미디어 공유 링크 제공

---
## 슬라이드 6: 재료 관리 메커니즘
- 재료 데이터베이스 통합
  - IngredientRepository로 재료 정보 관리
  - 재료명 기준 조회 기능 제공
- 레시피-재료 연결 시스템
  - RecipeIngredientsEntity로 다대다 관계 구현
  - 레시피별 필요 재료 및 수량 관리
- 장바구니 연동
  - 레시피 등록 시 CartEntity 자동 생성
  - RecipeIngredientsEntity와 CartEntity 연결
  - 재료 총 가격 자동 계산
- 재료 수량 관리
  - 레시피 등록/수정 시 재료별 수량 설정
  - 장바구니에 추가 시 수량 반영
- 재료 검색 및 선택
  - 레시피 등록/수정 시 전체 재료 목록 제공
  - 재료명 기준 검색 및 선택 기능

---
## 슬라이드 7: 레시피 카테고리화 메커니즘
- 카테고리 시스템
  - RecipeEntity의 category 필드로 관리
  - 기본 카테고리: 밥, 국, 메인반찬, 밑반찬, 면
- 카테고리 기반 검색
  - RecipeRepository의 findByCategory 메소드 활용
  - 카테고리별 레시피 목록 조회
- 카테고리와 키워드 조합 검색
  - RecipeServiceImpl의 searchByCategoryAndKeyword 메소드
  - 카테고리 내에서 제목 또는 작성자 검색
- 카테고리별 레시피 표시
  - 카테고리별 레시피 목록 페이지 제공
  - 카테고리 필터링 UI 제공
- 카테고리 관리
  - 레시피 등록/수정 시 카테고리 선택 필수
  - 관리자 설정 카테고리 목록 사용

---
## 슬라이드 8: 소셜 기능 메커니즘
- 사용자 리뷰 시스템
  - ReviewEntity로 레시피 리뷰 관리
  - 별점 및 텍스트 리뷰 지원
- 평점 시스템
  - 1-5점 별점 부여 가능
  - RecipeServiceImpl에서 평균 평점 계산
  - 레시피 목록에 평균 평점 표시
- 리뷰 정렬 및 필터링
  - 최신순/평점순 정렬 지원
  - 페이징 처리로 대량 리뷰 효율적 표시
- 리뷰 페이징 처리
  - Spring Data JPA의 Pageable 인터페이스 활용
  - 페이지 크기 및 정렬 기준 설정 가능
- 레시피-리뷰 연결
  - RecipeEntity와 ReviewEntity 간 일대다 관계
  - 레시피 삭제 시 연관 리뷰 자동 삭제(cascade)

---
## 슬라이드 9: 프론트엔드 구현 메커니즘
- 레시피 등록 폼
  - Thymeleaf 템플릿 엔진으로 동적 폼 생성
  - JavaScript로 재료/단계 동적 추가/삭제
  - 이미지 미리보기 기능
- 레시피 조회 화면
  - 반응형 레이아웃으로 다양한 기기 지원
  - 단계별 이미지와 설명 구조화 표시
  - 재료 목록 및 수량 정보 표시
- 레시피 수정 화면
  - 기존 데이터 자동 로드
  - 이미지 교체 또는 유지 옵션
  - 재료/단계 추가/삭제/수정 기능
- 레시피 목록 화면
  - 그리드 레이아웃으로 여러 레시피 표시
  - 썸네일, 제목, 작성자, 평점 정보 제공
  - 검색 및 필터링 UI 제공
- 모바일 최적화
  - 미디어 쿼리로 화면 크기별 레이아웃 조정
  - 터치 친화적 UI 요소 설계

---
## 슬라이드 10: 백엔드 구현 메커니즘
- 레시피 엔티티 구조
  - RecipeEntity 클래스로 DB 테이블 매핑
  - 제목, 카테고리, 이미지, 작성자 등 필드 포함
  - 단계 및 리뷰와 일대다 관계 설정
- 레시피 서비스 계층
  - RecipeService 인터페이스 정의
  - RecipeServiceImpl에서 비즈니스 로직 구현
  - 트랜잭션 관리로 데이터 일관성 유지
- 레시피 컨트롤러
  - RecipeController에서 HTTP 요청 처리
  - 사용자 인증 정보 확인 및 권한 검증
  - 서비스 계층 호출 및 결과 처리
- 파일 업로드 처리
  - FileService로 이미지 파일 저장 및 관리
  - UUID 기반 고유 파일명 생성
  - 파일 시스템에 이미지 저장
- 데이터 검증 및 정제
  - 입력값 유효성 검사
  - XSS 방지 필터링
  - 트랜잭션 처리로 데이터 일관성 유지

---
## 슬라이드 11: 데이터베이스 스키마 메커니즘
- 레시피 테이블 구조
  - id(PK), title, category, mainImage 등 필드
  - user_id 외래키로 작성자 참조
  - createdAt 필드로 생성 시간 기록
- 레시피 단계 테이블
  - id(PK), recipe_id(FK), stepNumber, content, imageName 필드
  - recipe_id로 RecipeEntity 참조
  - stepNumber로 단계 순서 관리
- 레시피 재료 테이블
  - id(PK), recipeId(FK), ingredientId(FK), cartId(FK), quantity 필드
  - 레시피, 재료, 장바구니 간 관계 매핑
  - 재료별 수량 정보 저장
- 이미지 저장 방식
  - 파일 시스템에 이미지 저장
  - DB에는 이미지 경로만 저장
  - UUID 기반 고유 파일명으로 충돌 방지
- 사용자 및 리뷰 엔티티와의 관계
  - 레시피-사용자: 다대일 관계(ManyToOne)
  - 레시피-리뷰: 일대다 관계(OneToMany)
  - 레시피-단계: 일대다 관계(OneToMany)
  - 레시피-재료: 일대다 관계(OneToMany)

---
## 슬라이드 12: 통합 지점 메커니즘
- 사용자 인증 및 권한
  - Spring Security로 사용자 인증
  - 레시피 작성자만 수정/삭제 권한 부여
  - PrincipalDetail에서 현재 사용자 정보 획득
- 장바구니 연동
  - 레시피 등록 시 재료 자동 장바구니 추가
  - CartEntity와 RecipeIngredientsEntity 연결
  - 재료 총 가격 자동 계산
- 재료 관리 연동
  - IngredientRepository로 재료 정보 조회
  - 레시피별 필요 재료 및 수량 관리
- 리뷰 및 평점 시스템
  - ReviewService로 레시피별 리뷰 관리
  - 평균 평점 계산 및 표시
  - 리뷰 정렬 및 페이징 처리
- 포인트 시스템 연동
  - 레시피 등록 시 사용자에게 포인트 지급
  - UserEntity의 addPoints 메소드로 포인트 증가
  - 포인트로 쇼핑 시 할인 혜택 제공