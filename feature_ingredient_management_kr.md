# 재료 관리 기능 - PowerPoint 슬라이드

---
## 슬라이드 1: 재료 관리 개요
- v 종합적인 재료 데이터베이스 시스템
- 카테고리화 및 조직 프레임워크
- v 레시피 및 쇼핑 기능과의 통합
- 영양 정보 추적
- v 재고 관리 기능

---
## 슬라이드 2: 재료 데이터베이스 메커니즘
- v 방대한 재료 카탈로그
  - v IngredientEntity로 재료 정보 저장
  - v id, ingredientName, price, detail, imageUrl 필드 포함
  - v 관리자 전용 재료 관리 인터페이스
- v 재료 정보 관리
  - v AdminController의 ingredientlist 메소드로 전체 재료 조회
  - v ingredientinsert 메소드로 새 재료 등록
  - v updateIngredient 메소드로 재료 정보 수정
  - v deleteIngredient 메소드로 재료 삭제
- v 재료 가격 관리
  - v 각 재료별 가격 정보 저장
  - v prePersist 메소드로 기본값 0 설정
  - v 장바구니 총액 계산에 활용
- v 재료 이미지 관리
  - v imageUrl 필드에 이미지 경로 저장
  - v TEXT 타입 컬럼으로 긴 URL 저장 가능
- v 재료 상세 정보
  - v detail 필드에 재료 설명 저장
  - v 사용법, 보관법 등 정보 제공

---
## 슬라이드 3: 재료 카테고리 메커니즘
- v 재료 분류 시스템
  - v 현재는 단일 테이블로 관리
  - 향후 카테고리 테이블 확장 가능성
- v 재료 검색 및 필터링
  - v IngredientRepository의 findAll 메소드로 전체 재료 조회
  - v findById 메소드로 특정 재료 조회
  - v findByIngredientName 메소드로 이름 기준 검색
- v 관리자 재료 관리
  - v AdminController에서 재료 CRUD 작업 처리
  - v 관리자 권한 검증 후 접근 허용
  - v 재료 목록, 등록, 수정, 삭제 기능 제공
- v 재료 정보 변환
  - v IngredientService의 dtoToEntity, entityToDto 메소드
  - v 데이터 계층과 표현 계층 간 변환 처리
- v 재료 데이터 유효성 검사
  - v 필수 필드(ingredientName, price) 검증
  - v 중복 재료명 확인
  - v 가격 정보 유효성 검사

---
## 슬라이드 4: 재료 검색 메커니즘
- v 레시피 등록/수정 시 재료 검색
  - v RecipeController에서 전체 재료 목록 제공
  - v 재료명 기준 검색 및 선택 기능
- v 재료 조회 기능
  - v IngredientService의 findAllIngredient 메소드
  - v IngredientRepository를 통한 데이터 접근
  - v Stream API로 엔티티-DTO 변환 처리
- v 재료별 레시피 검색
  - v 특정 재료를 사용하는 레시피 검색 가능
  - v RecipeIngredientsRepository 활용
- 재료 정보 캐싱
  - 자주 사용되는 재료 정보 메모리에 캐싱 가능
  - 검색 성능 최적화
- 재료 데이터 페이징
  - 대량의 재료 데이터 효율적 처리
  - 페이지 단위로 데이터 로딩

---
## 슬라이드 5: 재료 상세 정보 메커니즘
- v 재료 상세 정보 표시
  - v IngredientDTO로 재료 정보 전달
  - v 이름, 가격, 설명, 이미지 정보 포함
- v 재료 정보 조회
  - v IngredientService의 findIngredientById 메소드
  - v 존재하지 않는 재료 요청 시 EntityNotFoundException 발생
- v 재료 이미지 처리
  - v 이미지 URL 저장 및 표시
  - v 이미지 없을 경우 기본 이미지 표시
- v 재료 가격 정보
  - v Long 타입으로 가격 저장
  - v 화면에 표시 시 통화 형식으로 변환
- v 재료 설명 정보
  - v HTML 태그 지원으로 서식 있는 설명 가능
  - v 줄바꿈, 강조 등 텍스트 서식 지원

---
## 슬라이드 6: 레시피 연동 메커니즘
- v 레시피-재료 연결 시스템
  - v RecipeIngredientsEntity로 다대다 관계 구현
  - v 레시피별 필요 재료 및 수량 관리
- v 레시피 등록 시 재료 연결
  - v RecipeController의 recipeRegister 메소드에서 처리
  - v 재료명과 수량 정보 수집 및 저장
- v 레시피 조회 시 재료 정보 표시
  - v RecipeController의 recipeRead 메소드에서 처리
  - v RecipeIngredientsService로 레시피별 재료 조회
- v 레시피 수정 시 재료 관리
  - v RecipeController의 recipeUpdate 메소드에서 처리
  - v 기존 재료 연결 삭제 후 새로 생성
- v 레시피 삭제 시 연관 데이터 처리
  - v RecipeServiceImpl의 deleteRecipe 메소드에서 처리
  - v 연관된 재료 연결 정보 함께 삭제

---
## 슬라이드 7: 장바구니 연동 메커니즘
- v 레시피 재료 장바구니 추가
  - v RecipeServiceImpl의 registerRecipe 메소드에서 처리
  - v 레시피 등록 시 CartEntity 자동 생성
  - v RecipeIngredientsEntity와 CartEntity 연결
- v 재료 수량 관리
  - v RecipeIngredientsEntity의 quantity 필드로 관리
  - v 장바구니에 추가 시 수량 반영
- v 장바구니 가격 계산
  - v 재료별 가격과 수량을 곱하여 총액 계산
  - v CartEntity의 totalPrice 필드에 저장
- v 장바구니-재료 연결
  - v RecipeIngredientsEntity에 cartEntity 필드로 연결
  - v 다대일 관계(ManyToOne)로 구현
- v 장바구니 데이터 관리
  - v CartRepository를 통한 장바구니 데이터 접근
  - v 사용자별 장바구니 관리

---
## 슬라이드 8: 재고 관리 메커니즘
- v 재료 관리 시스템
  - v 관리자 페이지에서 재료 CRUD 작업
  - v 재료 정보 실시간 업데이트
- v 재료 정보 수정
  - v IngredientServiceImpl의 updateIngredient 메소드
  - v 기존 재료 조회 후 정보 업데이트
  - v 변경사항 즉시 DB 반영
- v 재료 삭제 처리
  - v IngredientServiceImpl의 deleteIngredient 메소드
  - v 연관된 레시피 재료 정보 처리 필요
- v 재료 등록 프로세스
  - v IngredientServiceImpl의 saveIngredient 메소드
  - v Builder 패턴으로 엔티티 생성 및 저장
- v 재료 조회 기능
  - v IngredientServiceImpl의 findAllIngredient 메소드
  - v Stream API로 엔티티 목록을 DTO 목록으로 변환

---
## 슬라이드 9: 프론트엔드 구현 메커니즘
- v 관리자 재료 목록 화면
  - v Thymeleaf 템플릿으로 재료 목록 표시
  - v 테이블 형태로 재료 정보 정리
  - v 등록, 수정, 삭제 버튼 제공
- v 재료 등록 폼
  - v 이름, 가격, 설명, 이미지 URL 입력 필드
  - v 폼 제출 시 AdminController로 데이터 전송
- v 재료 수정 화면
  - v 기존 재료 정보 자동 로드
  - v 수정 후 저장 시 DB 업데이트
- v 레시피에서 재료 선택 UI
  - v 드롭다운 또는 자동완성 입력 필드
  - v 선택한 재료에 수량 입력 기능
- v 재료 정보 표시
  - v 이미지, 이름, 가격, 설명 정보 표시
  - v 레시피 상세 페이지에 재료 목록 표시

---
## 슬라이드 10: 백엔드 구현 메커니즘
- v 재료 엔티티 구조
  - v IngredientEntity 클래스로 DB 테이블 매핑
  - v id, ingredientName, price, detail, imageUrl 필드
  - v prePersist 메소드로 기본값 설정
- v 재료 서비스 계층
  - v IngredientService 인터페이스 정의
  - v IngredientServiceImpl에서 비즈니스 로직 구현
  - v DTO-Entity 변환 로직
- v 재료 컨트롤러
  - v AdminController에서 재료 관리 기능 제공
  - v 관리자 권한 검증 후 접근 허용
  - v 서비스 계층 호출 및 결과 처리
- v 재료-레시피 연결 서비스
  - v RecipeIngredientsService 인터페이스 정의
  - v RecipeIngredientsServiceImpl에서 비즈니스 로직 구현
  - v 레시피별 재료 조회 및 관리
- v 데이터 접근 계층
  - v IngredientRepository로 재료 데이터 접근
  - v RecipeIngredientsRepository로 레시피-재료 연결 데이터 접근
  - v Spring Data JPA 활용

---
## 슬라이드 11: 데이터베이스 스키마 메커니즘
- v 재료 테이블 구조
  - v id(PK), ingredientName, price, detail, imageUrl 필드
  - v ingredientName에 unique 제약조건 적용 가능
  - v price는 NOT NULL 제약조건
- v 레시피-재료 연결 테이블
  - v id(PK), recipeId(FK), ingredientId(FK), cartId(FK), quantity 필드
  - v 레시피, 재료, 장바구니 간 관계 매핑
  - v 재료별 수량 정보 저장
- v 테이블 관계 설정
  - v 재료-레시피: 일대다 관계(OneToMany)
  - v 재료-장바구니: 일대다 관계(OneToMany)
  - v 다중 테이블 조인 쿼리 최적화
- v 인덱스 설정
  - v 재료명 검색 최적화를 위한 인덱스
  - v 외래 키 필드에 인덱스 자동 생성
- v 데이터 무결성
  - v 외래 키 제약조건으로 참조 무결성 유지
  - v 필수 필드 NOT NULL 제약조건 적용

---
## 슬라이드 12: 통합 지점 메커니즘
- v 레시피 생성 워크플로우
  - v RecipeController에서 재료 목록 제공
  - v 레시피 등록 시 선택한 재료 연결
  - v RecipeIngredientsEntity로 관계 저장
- v 장바구니 연동
  - v 레시피 등록 시 재료 자동 장바구니 추가
  - v CartEntity와 RecipeIngredientsEntity 연결
  - v 재료 총 가격 자동 계산
- v 재료 관리 시스템
  - v AdminController에서 재료 CRUD 작업
  - v 관리자 권한 검증 후 접근 허용
  - v 재료 정보 실시간 업데이트
- v 레시피 검색 연동
  - v 재료 기반 레시피 검색 가능
  - v RecipeIngredientsRepository 활용
- v 데이터 일관성 유지
  - v 트랜잭션 처리로 데이터 일관성 보장
  - v 연관 데이터 함께 처리
