# 결제 시스템 메커니즘 상세 가이드

## 목차
1. [결제 시스템 개요](#1-결제-시스템-개요)
2. [결제 프로세스 흐름](#2-결제-프로세스-흐름)
3. [장바구니에서 결제로의 전환](#3-장바구니에서-결제로의-전환)
4. [결제 정보 입력 및 처리](#4-결제-정보-입력-및-처리)
5. [포인트 시스템 통합](#5-포인트-시스템-통합)
6. [주문 완료 및 확인](#6-주문-완료-및-확인)
7. [프론트엔드 구현 상세](#7-프론트엔드-구현-상세)
8. [백엔드 구현 상세](#8-백엔드-구현-상세)
9. [데이터베이스 스키마 및 관계](#9-데이터베이스-스키마-및-관계)
10. [다른 시스템과의 통합](#10-다른-시스템과의-통합)
11. [성능 최적화 및 확장성](#11-성능-최적화-및-확장성)
12. [보안 고려사항](#12-보안-고려사항)

## 1. 결제 시스템 개요

결제 시스템은 사용자가 장바구니에 담은 레시피 재료를 구매하기 위한 핵심 기능을 제공합니다. 이 시스템은 장바구니, 결제 처리, 주문 관리, 포인트 시스템 등 여러 하위 시스템과 통합되어 있습니다.

### 1.1 시스템 목적

결제 시스템의 주요 목적은 다음과 같습니다:

- 사용자가 선택한 레시피 재료에 대한 원활한 결제 프로세스 제공
- 다양한 결제 수단 지원 (신용카드, 계좌이체, 휴대폰 결제, 카카오페이 등)
- 포인트 적립 및 사용 기능 통합
- 주문 정보의 안전한 처리 및 저장
- 사용자 친화적인 결제 경험 제공

### 1.2 시스템 구성 요소

결제 시스템은 다음과 같은 주요 구성 요소로 이루어져 있습니다:

1. **CartController**: 장바구니 관리 및 결제 페이지로의 전환을 담당
2. **PaymentController**: 결제 처리 및 주문 완료 페이지 표시를 담당
3. **OrderService**: 주문 정보 저장 및 관리를 담당
4. **PaymentEntity**: 결제 정보를 저장하는 데이터 모델
5. **OrderDTO**: 주문 정보를 전달하는 데이터 전송 객체
6. **checkout.html**: 결제 정보 입력 페이지
7. **complete.html**: 결제 완료 페이지

### 1.3 시스템 아키텍처

결제 시스템은 Spring Boot 기반의 MVC 아키텍처를 따르고 있으며, 다음과 같은 계층 구조로 구성되어 있습니다:

- **프레젠테이션 계층**: CartController와 PaymentController가 HTTP 요청을 처리하고 뷰를 반환
- **비즈니스 계층**: OrderService가 비즈니스 로직을 처리
- **데이터 액세스 계층**: 레포지토리가 데이터베이스 액세스를 담당
- **데이터 모델**: PaymentEntity가 데이터 구조를 정의

## 2. 결제 프로세스 흐름

결제 프로세스는 사용자가 장바구니에서 결제를 시작하는 순간부터 주문 완료까지의 전체 흐름을 포함합니다.

### 2.1 전체 프로세스 개요

결제 프로세스의 전체 흐름은 다음과 같습니다:

1. 사용자가 장바구니에서 결제하기 버튼 클릭
2. 시스템이 장바구니 정보를 기반으로 결제 페이지 준비
3. 사용자가 배송 정보 및 결제 정보 입력
4. 사용자가 결제 버튼 클릭
5. 시스템이 결제 정보 검증 및 처리
6. 포인트 차감 및 적립 처리
7. 주문 정보 생성 및 저장
8. 장바구니에서 구매 항목 제거
9. 결제 완료 페이지 표시

### 2.2 주요 액터

결제 프로세스에 관련된 주요 액터는 다음과 같습니다:

1. **사용자**: 결제를 진행하는 주체
2. **CartController**: 장바구니 정보를 관리하고 결제 페이지로 전환
3. **PaymentController**: 결제 처리 및 주문 완료 페이지 표시
4. **OrderService**: 주문 정보 저장 및 관리
5. **UserService**: 사용자 정보 및 포인트 관리

### 2.3 상태 전이

결제 프로세스 중 주문의 상태 전이는 다음과 같습니다:

1. **장바구니 상태**: 사용자가 선택한 상품이 장바구니에 저장된 상태
2. **결제 진행 상태**: 사용자가 결제 정보를 입력하는 상태
3. **결제 완료 상태**: 결제가 성공적으로 처리된 상태
4. **주문 확정 상태**: 주문 정보가 저장되고 장바구니에서 항목이 제거된 상태

## 3. 장바구니에서 결제로의 전환

장바구니에서 결제 페이지로의 전환은 CartController의 checkout 메소드에 의해 처리됩니다.

### 3.1 결제 페이지 접근

1. 사용자가 장바구니 페이지에서 특정 레시피의 "결제하기" 버튼을 클릭
2. 시스템이 CartController의 checkout 메소드를 호출
3. 메소드는 사용자 인증 정보를 확인
4. 인증되지 않은 사용자는 로그인 페이지로 리다이렉트

```java
@GetMapping("/checkout")
public String checkout(@RequestParam Long recipeId,
                       @RequestParam Long cartId,
                       Principal principal,
                       Model model) {
    if (principal == null) {
        return "redirect:/login";
    }
    
    // 이후 로직...
}
```

### 3.2 결제 정보 준비

CartController의 checkout 메소드는 결제에 필요한 정보를 준비합니다:

1. 사용자의 장바구니에서 선택한 레시피와 카트 ID에 해당하는 항목 찾기
2. 해당 레시피의 재료 목록 추출
3. 총 가격 계산 (재료 가격 * 수량의 합)
4. 배송비 설정 (고정값 3,000원)
5. 사용자 정보 조회 (포인트 정보 포함)

```java
// 1. 해당 유저의 장바구니 중 recipeId, cartId가 일치하는 CartDTO만 추출
List<CartDTO> cartDTOList = cartService.getAllCartsByUsername(username);

Optional<CartDTO> optionalCart = cartDTOList.stream()
        .filter(c -> c.getId() != null && c.getId().equals(cartId))
        .filter(c -> c.getRecipeIngredients() != null &&
                c.getRecipeIngredients().stream().anyMatch(i -> i.getRecipeId().equals(recipeId)))
        .findFirst();

// 2. 해당 CartDTO에서 recipeId에 해당하는 재료만 추출
List<RecipeIngredientsDTO> recipeIngredientDTOList = optionalCart.get()
        .getRecipeIngredients()
        .stream()
        .filter(i -> i.getRecipeId().equals(recipeId))
        .toList();

// 3. 총 가격 계산
long totalPrice = recipeIngredientDTOList.stream()
        .mapToLong(i -> i.getIngredient().getPrice() * i.getQuantity())
        .sum();

long shippingFee = 3000;

// 4. 사용자 정보 가져오기
UserDTO userDTO = userService.findByUsername(username);
```

### 3.3 결제 페이지 표시

준비된 정보를 모델에 추가하고 결제 페이지(checkout.html)를 반환합니다:

```java
// 5. 모델에 담기
model.addAttribute("cartId", cartId);
model.addAttribute("recipeId", recipeId);
model.addAttribute("cartItems", recipeIngredientDTOList);
model.addAttribute("totalPrice", totalPrice);
model.addAttribute("shippingFee", shippingFee);
model.addAttribute("user", userDTO);

return "cart/checkout";
```

## 4. 결제 정보 입력 및 처리

사용자가 결제 페이지에서 정보를 입력하고 결제를 진행하는 과정입니다.

### 4.1 결제 정보 입력 폼

결제 페이지(checkout.html)는 다음과 같은 정보를 입력받습니다:

1. **배송 정보**:
   - 받는 사람 이름
   - 연락처
   - 배송 주소 (기본 주소 및 상세 주소)
   - 배송 요청사항 (선택 옵션 또는 직접 입력)

2. **결제 정보**:
   - 결제 수단 (신용카드, 계좌이체, 휴대폰 결제, 카카오페이)
   - 사용할 포인트 (사용자의 보유 포인트 내에서 선택)
   - 주문 내용 확인 및 결제 진행 동의

```html
<form th:action="@{/payment/process}" method="post" id="checkoutForm">
    <!-- 히든 필드들 -->
    <input type="hidden" name="cartId" th:value="${cartId}">
    <input type="hidden" name="recipeId" th:value="${recipeId}">
    <input type="hidden" name="totalAmount" th:value="${totalPrice + shippingFee}">
    <input type="hidden" name="shippingFee" th:value="${shippingFee}">
    <input type="hidden" id="usedPoints" name="usedPoints" value="0">
    
    <!-- 배송 정보 입력 필드들 -->
    <div class="mb-3">
        <label for="name" class="form-label">받는 사람</label>
        <input type="text" class="form-control" id="name" name="name" required>
    </div>
    <!-- 기타 배송 정보 필드들... -->
    
    <!-- 결제 정보 입력 필드들 -->
    <div class="mb-3">
        <label for="paymentMethod" class="form-label">결제 수단</label>
        <select class="form-select" id="paymentMethod" name="paymentMethod" required>
            <option value="">결제 수단을 선택해주세요</option>
            <option value="card">신용카드</option>
            <option value="bank">계좌이체</option>
            <option value="phone">휴대폰 결제</option>
            <option value="kakao">카카오페이</option>
        </select>
    </div>
    <!-- 기타 결제 정보 필드들... -->
    
    <button type="submit" class="btn btn-primary">결제하기</button>
</form>
```

### 4.2 클라이언트 측 유효성 검사

결제 폼은 다음과 같은 클라이언트 측 유효성 검사를 수행합니다:

1. 필수 필드 검사 (HTML5의 required 속성 사용)
2. 포인트 사용량 제한 (사용자의 보유 포인트를 초과할 수 없음)
3. 결제 동의 체크박스 확인
4. 결제 수단 선택 확인
5. 직접 입력 옵션 선택 시 내용 확인

```javascript
// 폼 제출 전 유효성 검사
$('#checkoutForm').submit(function(e) {
    if (!$('#agreeTerms').is(':checked')) {
        e.preventDefault();
        alert('주문 내용 확인 및 결제 진행 동의가 필요합니다.');
        return false;
    }

    if ($('#paymentMethod').val() === '') {
        e.preventDefault();
        alert('결제 수단을 선택해주세요.');
        return false;
    }

    // 직접 입력 옵션 선택 시 내용 확인
    if ($('#deliveryRequest').val() === 'direct' && $('#deliveryRequestDirect').val().trim() === '') {
        e.preventDefault();
        alert('배송 요청사항을 입력해주세요.');
        return false;
    }
});
```

### 4.3 결제 처리

사용자가 결제 폼을 제출하면 PaymentController의 processPayment 메소드가 호출됩니다:

```java
@PostMapping("/process")
public String processPayment(
        @RequestParam Long cartId,
        @RequestParam Long recipeId,
        @RequestParam int totalAmount,  // 상품 + 배송비
        @RequestParam int shippingFee,
        @RequestParam int usedPoints,
        @RequestParam String name,
        @RequestParam String phone,
        @RequestParam String shippingAddress,
        @RequestParam String shippingAddressDetail,
        @RequestParam String deliveryRequest,
        @RequestParam(required = false) String deliveryRequestDirect,
        @RequestParam String paymentMethod,
        Principal principal,
        RedirectAttributes redirectAttributes
) {
    // 결제 처리 로직...
}
```

이 메소드는 다음과 같은 작업을 수행합니다:

1. 로그인 사용자 정보 가져오기
2. 실제 결제 금액 계산 (포인트 차감)
3. 주문 번호 생성
4. 주문 객체 생성 및 데이터 설정
5. 사용자 포인트 차감 및 적립
6. 장바구니에서 구매 항목 제거
7. 주문 정보를 리다이렉트 속성에 추가
8. 결제 완료 페이지로 리다이렉트

## 5. 포인트 시스템 통합

결제 시스템은 포인트 시스템과 통합되어 있어 사용자가 포인트를 사용하고 적립할 수 있습니다.

### 5.1 포인트 사용

사용자는 결제 시 보유한 포인트를 사용할 수 있습니다:

1. 결제 페이지에서 사용할 포인트 입력
2. 입력된 포인트는 총 결제 금액에서 차감
3. 사용된 포인트는 사용자의 포인트 잔액에서 차감

```java
// 실제 결제 금액 계산 (포인트 차감)
int finalAmount = totalAmount - usedPoints;

// 사용자 포인트 차감 및 적립
user.setPoints(user.getPoints() - usedPoints + order.getEarnedPoints());
userService.updateUser(username, user);
```

### 5.2 포인트 적립

결제 완료 시 결제 금액의 일정 비율(1%)이 포인트로 적립됩니다:

```java
// 포인트 적립 (결제 금액의 1%)
order.setEarnedPoints((int)(finalAmount * 0.01));
```

### 5.3 포인트 정보 표시

결제 완료 페이지에서는 다음과 같은 포인트 정보가 표시됩니다:

1. 사용한 포인트
2. 적립된 포인트
3. 현재 보유 포인트

```html
<div class="card">
    <div class="card-header">
        <h3>포인트 정보</h3>
    </div>
    <div class="card-body">
        <div class="row mb-3">
            <div class="col-md-4">사용 포인트:</div>
            <div class="col-md-8" th:text="${order.usedPoints} + ' 포인트'">1,000 포인트</div>
        </div>
        <div class="row mb-3">
            <div class="col-md-4">적립 포인트:</div>
            <div class="col-md-8" th:text="${order.earnedPoints} + ' 포인트'">470 포인트</div>
        </div>
        <div class="row mb-3">
            <div class="col-md-4">현재 보유 포인트:</div>
            <div class="col-md-8" th:text="${user.points} + ' 포인트'">2,470 포인트</div>
        </div>
    </div>
</div>
```

## 6. 주문 완료 및 확인

결제가 완료되면 사용자는 주문 완료 페이지로 리다이렉트됩니다.

### 6.1 결제 완료 페이지

결제 완료 페이지(complete.html)는 다음과 같은 정보를 표시합니다:

1. **주문 정보**:
   - 주문 번호
   - 주문 일시
   - 결제 금액
   - 결제 방법
   - 배송지
   - 배송 요청사항

2. **주문 상품 목록**:
   - 상품명
   - 수량
   - 가격
   - 합계

3. **가격 정보**:
   - 상품 금액
   - 배송비
   - 포인트 사용
   - 총 결제 금액

4. **포인트 정보**:
   - 사용 포인트
   - 적립 포인트
   - 현재 보유 포인트

```java
@GetMapping("/complete")
public String showCompletePage() {
    return "payment/complete"; // complete.html
}
```

### 6.2 주문 정보 전달

PaymentController의 processPayment 메소드는 RedirectAttributes를 통해 주문 정보를 결제 완료 페이지로 전달합니다:

```java
// 주문 정보 전달
redirectAttributes.addFlashAttribute("order", order);
redirectAttributes.addFlashAttribute("user", user);

return "redirect:/payment/complete";
```

### 6.3 주문 확인 알림

결제 완료 페이지에서는 JavaScript를 통해 결제 완료 알림을 표시합니다:

```html
<script layout:fragment="script" th:if="${order != null}">
    alert("총 결제 금액은 [[${order.totalAmount}]]원입니다.\n정상 결제되었습니다.");
</script>
```

## 7. 프론트엔드 구현 상세

결제 시스템의 프론트엔드 구현에 대한 상세 내용입니다.

### 7.1 결제 페이지 UI

결제 페이지(checkout.html)는 다음과 같은 UI 요소로 구성됩니다:

1. **주문 상품 정보 섹션**:
   - 상품 목록 테이블 (재료명, 단위, 수량, 가격, 합계)
   - 가격 요약 (상품 금액, 배송비, 총 결제 금액)

2. **배송 정보 섹션**:
   - 받는 사람 입력 필드
   - 연락처 입력 필드
   - 배송 주소 입력 필드 (기본 주소 및 상세 주소)
   - 배송 요청사항 선택 드롭다운 및 직접 입력 필드

3. **결제 정보 섹션**:
   - 결제 수단 선택 드롭다운
   - 포인트 사용 입력 필드
   - 주문 내용 확인 및 결제 진행 동의 체크박스

4. **버튼 섹션**:
   - 결제하기 버튼
   - 장바구니로 돌아가기 버튼

### 7.2 결제 완료 페이지 UI

결제 완료 페이지(complete.html)는 다음과 같은 UI 요소로 구성됩니다:

1. **주문 정보 카드**:
   - 주문 번호, 주문 일시, 결제 금액, 결제 방법 등 표시
   - 배송지 및 배송 요청사항 표시

2. **주문 상품 카드**:
   - 상품 목록 테이블 (상품명, 수량, 가격, 합계)
   - 가격 요약 (상품 금액, 배송비, 포인트 사용, 총 결제 금액)

3. **포인트 정보 카드**:
   - 사용 포인트, 적립 포인트, 현재 보유 포인트 표시

4. **버튼 섹션**:
   - 주문 내역 확인 버튼
   - 레시피 검색 버튼

### 7.3 반응형 디자인

결제 시스템의 UI는 Bootstrap 프레임워크를 사용하여 반응형으로 구현되어 있습니다:

1. **그리드 시스템**:
   - 큰 화면에서는 2열 레이아웃 (주문 정보 | 배송 및 결제 정보)
   - 작은 화면에서는 1열 레이아웃 (주문 정보 → 배송 및 결제 정보)

2. **반응형 테이블**:
   - 테이블 내용이 화면 크기에 맞게 조정
   - 작은 화면에서는 가로 스크롤 제공

3. **폼 요소**:
   - 터치 친화적인 큰 입력 필드 및 버튼
   - 모바일 환경에 최적화된 드롭다운 및 체크박스

### 7.4 사용자 경험 향상

결제 시스템은 다음과 같은 방법으로 사용자 경험을 향상시킵니다:

1. **실시간 피드백**:
   - 포인트 사용량 변경 시 실시간으로 반영
   - 폼 유효성 검사 오류 시 즉각적인 피드백

2. **직관적인 UI**:
   - 명확한 섹션 구분 및 레이블
   - 시각적 계층 구조를 통한 정보 우선순위 표시

3. **상태 표시**:
   - 결제 완료 시 알림 메시지
   - 주문 정보 요약을 통한 확인 기능

## 8. 백엔드 구현 상세

결제 시스템의 백엔드 구현에 대한 상세 내용입니다.

### 8.1 컨트롤러 구현

#### 8.1.1 CartController

CartController는 장바구니 관리 및 결제 페이지로의 전환을 담당합니다:

```java
@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
@Log4j2
public class CartController {
    private final CartService cartService;
    private final RecipeService recipeService;
    private final RecipeRepository recipeRepository;
    private final UserService userService;

    // 장바구니 목록 표시
    @GetMapping("/list")
    public String cartList(Model model, Principal principal) {
        // 구현 생략...
    }

    // 결제 페이지로 전환
    @GetMapping("/checkout")
    public String checkout(@RequestParam Long recipeId,
                           @RequestParam Long cartId,
                           Principal principal,
                           Model model) {
        // 구현 생략...
    }

    // 장바구니 항목 삭제
    @PostMapping("/delete/item/{id}")
    public String deleteCartItem(@PathVariable Long id, Principal principal) {
        // 구현 생략...
    }

    // 장바구니 전체 삭제
    @PostMapping("/delete/{cartId}")
    public String deleteCart(@PathVariable Long cartId, Principal principal) {
        // 구현 생략...
    }
}
```

#### 8.1.2 PaymentController

PaymentController는 결제 처리 및 주문 완료 페이지 표시를 담당합니다:

```java
@Controller
@RequestMapping("/payment")
public class PaymentController {
    private final CartService cartService;
    private final OrderService orderService;
    private final UserService userService;
    private final RecipeIngredientsServiceImpl recipeIngredientsServiceImpl;

    // 생성자 주입
    public PaymentController(CartService cartService, OrderService orderService, UserService userService,
                            RecipeIngredientsServiceImpl recipeIngredientsServiceImpl) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.userService = userService;
        this.recipeIngredientsServiceImpl = recipeIngredientsServiceImpl;
    }

    // 결제 처리
    @PostMapping("/process")
    public String processPayment(
            @Request