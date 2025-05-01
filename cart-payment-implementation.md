# 장바구니 및 결제 시스템 구현 설명서

## 1. 개요

이 문서는 레시피 및 식재료 쇼핑 웹사이트의 장바구니와 결제 시스템 구현에 대한 상세한 설명을 제공합니다. 장바구니 시스템은 사용자가 선택한 레시피와 식재료를 임시로 저장하고, 결제 후에는 해당 데이터가 삭제되는 방식으로 구현되었습니다.

## 2. 시스템 아키텍처

장바구니 및 결제 시스템은 다음과 같은 계층 구조로 설계되었습니다:

1. **엔티티 계층 (Entity Layer)**
   - CartEntity: 사용자의 장바구니 정보
   - CartItemEntity: 장바구니에 담긴 개별 상품 정보
   - PaymentEntity: 결제 정보

2. **저장소 계층 (Repository Layer)**
   - CartRepository: 장바구니 데이터 액세스
   - CartItemRepository: 장바구니 아이템 데이터 액세스
   - PaymentRepository: 결제 데이터 액세스

3. **서비스 계층 (Service Layer)**
   - CartService: 장바구니 관련 비즈니스 로직
   - CartServiceImpl: CartService 구현체

4. **컨트롤러 계층 (Controller Layer)**
   - CartController: 장바구니 관련 요청 처리
   - PaymentController: 결제 관련 요청 처리

5. **뷰 계층 (View Layer)**
   - cart/list.html: 장바구니 목록 화면
   - cart/checkout.html: 결제 화면

## 3. 엔티티 관계

### 3.1 CartEntity

```java
@Entity
@Getter @Setter
@NoArgsConstructor
public class CartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username")
    private UserEntity user;

    private Long totalPrice;

    @CreationTimestamp
    @Column(name="createdAt")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
```

### 3.2 CartItemEntity

```java
@Entity
@Getter @Setter
@NoArgsConstructor
public class CartItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredientId")
    private IngredientEntity ingredientEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cartId")
    private CartEntity cartEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipeId")
    private RecipeEntity recipeEntity;

    private Long quantity;
}
```

### 3.3 PaymentEntity

```java
@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cartId")
    private CartEntity cartEntity;

    private Long totalPrice;

    private String payment_url;

    private String status;

    @CreationTimestamp
    @Column(name="createdAt")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
```

## 4. 저장소 계층 (Repository Layer)

### 4.1 CartRepository

```java
@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {
    Optional<CartEntity> findByUser(UserEntity user);
}
```

### 4.2 CartItemRepository

```java
@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {
    List<CartItemEntity> findByCartEntity(CartEntity cartEntity);
    
    Optional<CartItemEntity> findByCartEntityAndIngredientEntity(CartEntity cartEntity, IngredientEntity ingredientEntity);
    
    Optional<CartItemEntity> findByCartEntityAndIngredientEntityAndRecipeEntity(
            CartEntity cartEntity, 
            IngredientEntity ingredientEntity,
            RecipeEntity recipeEntity);
    
    void deleteByCartEntity(CartEntity cartEntity);
}
```

### 4.3 PaymentRepository

```java
@Repository
public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    List<PaymentEntity> findByCartEntity_User(UserEntity user);
}
```

## 5. 서비스 계층 (Service Layer)

### 5.1 CartService 인터페이스

```java
public interface CartService {
    // 사용자의 장바구니 조회 또는 생성
    CartEntity getOrCreateCart(UserEntity user);
    
    // 장바구니에 상품 추가
    CartItemDTO addToCart(Long ingredientId, Long recipeId, Long quantity, UserEntity user);
    
    // 장바구니 상품 수량 업데이트
    CartItemDTO updateCartItem(Long cartItemId, Long change, UserEntity user);
    
    // 장바구니에서 상품 제거
    void removeFromCart(Long cartItemId, UserEntity user);
    
    // 사용자의 장바구니 상품 목록 조회
    List<CartItemDTO> getCartItems(UserEntity user);
    
    // 장바구니 총 가격 계산
    Long calculateTotalPrice(UserEntity user);
    
    // 결제 후 장바구니 비우기
    void clearCart(UserEntity user);
    
    // CartEntity를 CartDTO로 변환
    CartDTO entityToDto(CartEntity cartEntity);
    
    // CartDTO를 CartEntity로 변환
    CartEntity dtoToEntity(CartDTO cartDTO);
}
```

### 5.2 CartServiceImpl 구현

CartServiceImpl 클래스는 CartService 인터페이스를 구현하며, 다음과 같은 주요 기능을 제공합니다:

1. **getOrCreateCart**: 사용자의 장바구니를 조회하거나 없으면 새로 생성
2. **addToCart**: 장바구니에 상품 추가 (이미 있으면 수량 증가)
3. **updateCartItem**: 장바구니 상품 수량 변경
4. **removeFromCart**: 장바구니에서 상품 제거
5. **getCartItems**: 장바구니 상품 목록 조회
6. **calculateTotalPrice**: 장바구니 총 가격 계산
7. **clearCart**: 결제 후 장바구니 비우기

주요 구현 내용:

```java
@Override
@Transactional
public CartItemDTO addToCart(Long ingredientId, Long recipeId, Long quantity, UserEntity user) {
    CartEntity cart = getOrCreateCart(user);
    IngredientEntity ingredient = ingredientRepository.findById(ingredientId)
            .orElseThrow(() -> new RuntimeException("Ingredient not found"));
    
    RecipeEntity recipe = null;
    if (recipeId != null) {
        recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));
    }
    
    // 이미 장바구니에 있는 상품인지 확인
    Optional<CartItemEntity> existingItem;
    if (recipe != null) {
        existingItem = cartItemRepository.findByCartEntityAndIngredientEntityAndRecipeEntity(cart, ingredient, recipe);
    } else {
        existingItem = cartItemRepository.findByCartEntityAndIngredientEntity(cart, ingredient);
    }
    
    CartItemEntity cartItem;
    if (existingItem.isPresent()) {
        // 이미 있으면 수량 업데이트
        cartItem = existingItem.get();
        cartItem.setQuantity(cartItem.getQuantity() + quantity);
    } else {
        // 새 상품 추가
        cartItem = new CartItemEntity();
        cartItem.setCartEntity(cart);
        cartItem.setIngredientEntity(ingredient);
        cartItem.setRecipeEntity(recipe);
        cartItem.setQuantity(quantity);
    }
    
    cartItem = cartItemRepository.save(cartItem);
    
    // 장바구니 총 가격 업데이트
    updateCartTotalPrice(cart);
    
    return convertToCartItemDTO(cartItem);
}
```

## 6. 컨트롤러 계층 (Controller Layer)

### 6.1 CartController

CartController는 장바구니 관련 요청을 처리하며, 다음과 같은 주요 엔드포인트를 제공합니다:

1. **GET /cart/list**: 장바구니 목록 화면
2. **GET /cart/checkout**: 결제 화면
3. **POST /cart/add**: 장바구니에 상품 추가 (AJAX)
4. **POST /cart/update/{id}**: 장바구니 상품 수량 변경 (AJAX)
5. **DELETE /cart/remove/{id}**: 장바구니에서 상품 제거 (AJAX)
6. **POST /cart/clear**: 장바구니 비우기 (AJAX)

주요 구현 내용:

```java
@GetMapping("/list")
public String list(@AuthenticationPrincipal PrincipalDetail principalDetail, Model model) {
    if (principalDetail == null) {
        return "redirect:/userinfo/login";
    }

    UserEntity user = principalDetail.getUser();
    List<CartItemDTO> cartItems = cartService.getCartItems(user);
    Long totalPrice = cartService.calculateTotalPrice(user);

    model.addAttribute("cartItems", cartItems);
    model.addAttribute("totalPrice", totalPrice);

    return "cart/list";
}

@PostMapping("/add")
@ResponseBody
public ResponseEntity<Map<String, Object>> addToCart(
        @RequestParam Long ingredientId,
        @RequestParam(required = false) Long recipeId,
        @RequestParam(defaultValue = "1") Long quantity,
        @AuthenticationPrincipal PrincipalDetail principalDetail) {

    Map<String, Object> response = new HashMap<>();

    try {
        if (principalDetail == null) {
            response.put("success", false);
            response.put("message", "로그인이 필요합니다.");
            return ResponseEntity.ok(response);
        }

        UserEntity user = principalDetail.getUser();
        CartItemDTO cartItem = cartService.addToCart(ingredientId, recipeId, quantity, user);

        response.put("success", true);
        response.put("message", "장바구니에 추가되었습니다.");
        response.put("cartItem", cartItem);

    } catch (Exception e) {
        log.error("장바구니 추가 중 오류 발생: " + e.getMessage());
        response.put("success", false);
        response.put("message", "오류가 발생했습니다: " + e.getMessage());
    }

    return ResponseEntity.ok(response);
}
```

### 6.2 PaymentController

PaymentController는 결제 관련 요청을 처리하며, 다음과 같은 주요 엔드포인트를 제공합니다:

1. **POST /payment/process**: 결제 처리
2. **GET /payment/complete**: 결제 완료 화면

주요 구현 내용:

```java
@PostMapping("/process")
public String processPayment(
        @RequestParam String name,
        @RequestParam String phone,
        @RequestParam String address,
        @RequestParam(required = false) String addressDetail,
        @RequestParam(required = false) String deliveryRequest,
        @RequestParam(required = false) String deliveryRequestDirect,
        @RequestParam String paymentMethod,
        @RequestParam(defaultValue = "0") Long usePoints,
        @AuthenticationPrincipal PrincipalDetail principalDetail,
        RedirectAttributes redirectAttributes,
        Model model) {

    if (principalDetail == null) {
        return "redirect:/userinfo/login";
    }

    try {
        UserEntity user = principalDetail.getUser();
        
        // 장바구니 상품 및 총 가격 조회
        List<CartItemDTO> cartItems = cartService.getCartItems(user);
        if (cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "장바구니가 비어있습니다.");
            return "redirect:/cart/list";
        }
        
        Long totalPrice = cartService.calculateTotalPrice(user);
        Long shippingFee = 3000L; // 기본 배송비
        Long finalPrice = totalPrice + shippingFee - usePoints;
        
        if (finalPrice < 0) {
            finalPrice = 0L;
        }
        
        // 사용자의 장바구니 조회
        CartEntity cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("장바구니를 찾을 수 없습니다."));
        
        // 결제 정보 생성
        PaymentEntity payment = new PaymentEntity();
        payment.setCartEntity(cart);
        payment.setTotalPrice(finalPrice);
        payment.setStatus("COMPLETED");
        payment.setCreatedAt(LocalDateTime.now());
        
        // 결제 정보 저장
        paymentRepository.save(payment);
        
        // 장바구니 비우기 (장바구니 엔티티는 결제 기록을 위해 유지)
        cartService.clearCart(user);
        
        // 성공 메시지 추가
        redirectAttributes.addFlashAttribute("success", "결제가 완료되었습니다.");
        redirectAttributes.addFlashAttribute("orderId", payment.getId());
        
        return "redirect:/payment/complete";
        
    } catch (Exception e) {
        log.error("결제 처리 중 오류 발생: " + e.getMessage());
        redirectAttributes.addFlashAttribute("error", "결제 처리 중 오류가 발생했습니다: " + e.getMessage());
        return "redirect:/cart/checkout";
    }
}
```

## 7. 뷰 계층 (View Layer)

### 7.1 장바구니 목록 화면 (cart/list.html)

장바구니 목록 화면은 사용자가 장바구니에 담은 상품 목록을 보여주고, 수량 변경 및 삭제 기능을 제공합니다.

주요 기능:
- 장바구니 상품 목록 표시
- 상품 수량 증가/감소
- 상품 삭제
- 총 가격 표시
- 결제 페이지로 이동

### 7.2 결제 화면 (cart/checkout.html)

결제 화면은 장바구니 상품 목록과 함께 배송 정보 및 결제 정보 입력 폼을 제공합니다.

주요 기능:
- 장바구니 상품 목록 표시
- 배송 정보 입력 (이름, 연락처, 주소, 배송 요청사항)
- 결제 수단 선택
- 포인트 사용
- 결제 처리

## 8. 데이터 흐름

1. **장바구니 추가**
   - 사용자가 레시피 또는 식재료 페이지에서 상품을 장바구니에 추가
   - CartController.addToCart() 메서드 호출
   - CartService.addToCart() 메서드에서 장바구니 상품 추가 처리
   - 장바구니 상품 정보 저장 및 응답 반환

2. **장바구니 조회**
   - 사용자가 장바구니 페이지 접속
   - CartController.list() 메서드 호출
   - CartService.getCartItems() 및 calculateTotalPrice() 메서드로 장바구니 정보 조회
   - 장바구니 목록 화면 표시

3. **결제 처리**
   - 사용자가 결제 정보 입력 후 결제 버튼 클릭
   - PaymentController.processPayment() 메서드 호출
   - 결제 정보 생성 및 저장
   - CartService.clearCart() 메서드로 장바구니 비우기
   - 결제 완료 화면으로 리다이렉트

## 9. 결론

장바구니 및 결제 시스템은 사용자가 레시피와 식재료를 쉽게 선택하고 구매할 수 있도록 설계되었습니다. 장바구니는 필요한 데이터만 저장하고, 결제 후에는 장바구니 내용이 삭제되는 방식으로 구현되었습니다. 이를 통해 사용자는 원하는 레시피에 필요한 식재료를 쉽게 구매할 수 있습니다.