# 재료 관리 기능 - PowerPoint 슬라이드

---
## 슬라이드 1: 재료 관리 개요
- 종합적인 재료 데이터베이스 시스템
- 카테고리화 및 조직 프레임워크
- 레시피 및 쇼핑 기능과의 통합
- 영양 정보 추적
- 재고 관리 기능

---
## 슬라이드 2: 재료 데이터베이스 메커니즘
- 방대한 재료 카탈로그
  - IngredientEntity로 재료 정보 저장
  - id, ingredientName, price, detail, imageUrl 필드 포함
  - 관리자 전용 재료 관리 인터페이스
- 재료 정보 관리
  - AdminController의 ingredientlist 메소드로 전체 재료 조회
  - ingredientinsert 메소드로 새 재료 등록
  - updateIngredient 메소드로 재료 정보 수정
  - deleteIngredient 메소드로 재료 삭제
- 재료 가격 관리
  - 각 재료별 가격 정보 저장
  - prePersist 메소드로 기본값 0 설정
  - 장바구니 총액 계산에 활용
- 재료 이미지 관리
  - imageUrl 필드에 이미지 경로 저장
  - TEXT 타입 컬럼으로 긴 URL 저장 가능
- 재료 상세 정보
  - detail 필드에 재료 설명 저장
  - 사용법, 보관법 등 정보 제공

---
## 슬라이드 3: 재료 카테고리 메커니즘
- 재료 분류 시스템
  - 현재는 단일 테이블로 관리
  - 향후 카테고리 테이블 확장 가능성
- 재료 검색 및 필터링
  - IngredientRepository의 findAll 메소드로 전체 재료 조회
  - findById 메소드로 특정 재료 조회
  - findByIngredientName 메소드로 이름 기준 검색
- 관리자 재료 관리
  - AdminController에서 재료 CRUD 작업 처리
  - 관리자 권한 검증 후 접근 허용
  - 재료 목록, 등록, 수정, 삭제 기능 제공
- 재료 정보 변환
  - IngredientService의 dtoToEntity, entityToDto 메소드
  - 데이터 계층과 표현 계층 간 변환 처리
- 재료 데이터 유효성 검사
  - 필수 필드(ingredientName, price) 검증
  - 중복 재료명 확인
  - 가격 정보 유효성 검사

---
## 슬라이드 4: 재료 검색 메커니즘
- 레시피 등록/수정 시 재료 검색
  - RecipeController에서 전체 재료 목록 제공
  - 재료명 기준 검색 및 선택 기능
- 재료 조회 기능
  - IngredientService의 findAllIngredient 메소드
  - IngredientRepository를 통한 데이터 접근
  - Stream API로 엔티티-DTO 변환 처리
- 재료별 레시피 검색
  - 특정 재료를 사용하는 레시피 검색 가능
  - RecipeIngredientsRepository 활용
- 재료 정보 캐싱
  - 자주 사용되는 재료 정보 메모리에 캐싱 가능
  - 검색 성능 최적화
- 재료 데이터 페이징
  - 대량의 재료 데이터 효율적 처리
  - 페이지 단위로 데이터 로딩

---
## 슬라이드 5: 재료 상세 정보 메커니즘
- 재료 상세 정보 표시
  - IngredientDTO로 재료 정보 전달
  - 이름, 가격, 설명, 이미지 정보 포함
- 재료 정보 조회
  - IngredientService의 findIngredientById 메소드
  - 존재하지 않는 재료 요청 시 EntityNotFoundException 발생
- 재료 이미지 처리
  - 이미지 URL 저장 및 표시
  - 이미지 없을 경우 기본 이미지 표시
- 재료 가격 정보
  - Long 타입으로 가격 저장
  - 화면에 표시 시 통화 형식으로 변환
- 재료 설명 정보
  - HTML 태그 지원으로 서식 있는 설명 가능
  - 줄바꿈, 강조 등 텍스트 서식 지원

---
## 슬라이드 6: 레시피 연동 메커니즘
- 레시피-재료 연결 시스템
  - RecipeIngredientsEntity로 다대다 관계 구현
  - 레시피별 필요 재료 및 수량 관리
- 레시피 등록 시 재료 연결
  - RecipeController의 recipeRegister 메소드에서 처리
  - 재료명과 수량 정보 수집 및 저장
- 레시피 조회 시 재료 정보 표시
  - RecipeController의 recipeRead 메소드에서 처리
  - RecipeIngredientsService로 레시피별 재료 조회
- 레시피 수정 시 재료 관리
  - RecipeController의 recipeUpdate 메소드에서 처리
  - 기존 재료 연결 삭제 후 새로 생성
- 레시피 삭제 시 연관 데이터 처리
  - RecipeServiceImpl의 deleteRecipe 메소드에서 처리
  - 연관된 재료 연결 정보 함께 삭제

---
## 슬라이드 7: 장바구니 연동 메커니즘
- 레시피 재료 장바구니 추가
  - RecipeServiceImpl의 registerRecipe 메소드에서 처리
  - 레시피 등록 시 CartEntity 자동 생성
  - RecipeIngredientsEntity와 CartEntity 연결
- 재료 수량 관리
  - RecipeIngredientsEntity의 quantity 필드로 관리
  - 장바구니에 추가 시 수량 반영
- 장바구니 가격 계산
  - 재료별 가격과 수량을 곱하여 총액 계산
  - CartEntity의 totalPrice 필드에 저장
- 장바구니-재료 연결
  - RecipeIngredientsEntity에 cartEntity 필드로 연결
  - 다대일 관계(ManyToOne)로 구현
- 장바구니 데이터 관리
  - CartRepository를 통한 장바구니 데이터 접근
  - 사용자별 장바구니 관리

---
## 슬라이드 8: 재고 관리 메커니즘
- 재료 관리 시스템
  - 관리자 페이지에서 재료 CRUD 작업
  - 재료 정보 실시간 업데이트
- 재료 정보 수정
  - IngredientServiceImpl의 updateIngredient 메소드
  - 기존 재료 조회 후 정보 업데이트
  - 변경사항 즉시 DB 반영
- 재료 삭제 처리
  - IngredientServiceImpl의 deleteIngredient 메소드
  - 연관된 레시피 재료 정보 처리 필요
- 재료 등록 프로세스
  - IngredientServiceImpl의 saveIngredient 메소드
  - Builder 패턴으로 엔티티 생성 및 저장
- 재료 조회 기능
  - IngredientServiceImpl의 findAllIngredient 메소드
  - Stream API로 엔티티 목록을 DTO 목록으로 변환

---
## 슬라이드 9: 프론트엔드 구현 메커니즘
- 관리자 재료 목록 화면
  - Thymeleaf 템플릿으로 재료 목록 표시
  - 테이블 형태로 재료 정보 정리
  - 등록, 수정, 삭제 버튼 제공
- 재료 등록 폼
  - 이름, 가격, 설명, 이미지 URL 입력 필드
  - 폼 제출 시 AdminController로 데이터 전송
- 재료 수정 화면
  - 기존 재료 정보 자동 로드
  - 수정 후 저장 시 DB 업데이트
- 레시피에서 재료 선택 UI
  - 드롭다운 또는 자동완성 입력 필드
  - 선택한 재료에 수량 입력 기능
- 재료 정보 표시
  - 이미지, 이름, 가격, 설명 정보 표시
  - 레시피 상세 페이지에 재료 목록 표시

---
## 슬라이드 10: 백엔드 구현 메커니즘
- 재료 엔티티 구조
  - IngredientEntity 클래스로 DB 테이블 매핑
  - id, ingredientName, price, detail, imageUrl 필드
  - prePersist 메소드로 기본값 설정
- 재료 서비스 계층
  - IngredientService 인터페이스 정의
  - IngredientServiceImpl에서 비즈니스 로직 구현
  - DTO-Entity 변환 로직
- 재료 컨트롤러
  - AdminController에서 재료 관리 기능 제공
  - 관리자 권한 검증 후 접근 허용
  - 서비스 계층 호출 및 결과 처리
- 재료-레시피 연결 서비스
  - RecipeIngredientsService 인터페이스 정의
  - RecipeIngredientsServiceImpl에서 비즈니스 로직 구현
  - 레시피별 재료 조회 및 관리
- 데이터 접근 계층
  - IngredientRepository로 재료 데이터 접근
  - RecipeIngredientsRepository로 레시피-재료 연결 데이터 접근
  - Spring Data JPA 활용

---
## 슬라이드 11: 데이터베이스 스키마 메커니즘
- 재료 테이블 구조
  - id(PK), ingredientName, price, detail, imageUrl 필드
  - ingredientName에 unique 제약조건 적용 가능
  - price는 NOT NULL 제약조건
- 레시피-재료 연결 테이블
  - id(PK), recipeId(FK), ingredientId(FK), cartId(FK), quantity 필드
  - 레시피, 재료, 장바구니 간 관계 매핑
  - 재료별 수량 정보 저장
- 테이블 관계 설정
  - 재료-레시피: 일대다 관계(OneToMany)
  - 재료-장바구니: 일대다 관계(OneToMany)
  - 다중 테이블 조인 쿼리 최적화
- 인덱스 설정
  - 재료명 검색 최적화를 위한 인덱스
  - 외래 키 필드에 인덱스 자동 생성
- 데이터 무결성
  - 외래 키 제약조건으로 참조 무결성 유지
  - 필수 필드 NOT NULL 제약조건 적용

---
## 슬라이드 12: 통합 지점 메커니즘
- 레시피 생성 워크플로우
  - RecipeController에서 재료 목록 제공
  - 레시피 등록 시 선택한 재료 연결
  - RecipeIngredientsEntity로 관계 저장
- 장바구니 연동
  - 레시피 등록 시 재료 자동 장바구니 추가
  - CartEntity와 RecipeIngredientsEntity 연결
  - 재료 총 가격 자동 계산
- 재료 관리 시스템
  - AdminController에서 재료 CRUD 작업
  - 관리자 권한 검증 후 접근 허용
  - 재료 정보 실시간 업데이트
- 레시피 검색 연동
  - 재료 기반 레시피 검색 가능
  - RecipeIngredientsRepository 활용
- 데이터 일관성 유지
  - 트랜잭션 처리로 데이터 일관성 보장
  - 연관 데이터 함께 처리