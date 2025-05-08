# 리뷰 시스템 메커니즘 상세 가이드

## 목차
1. [리뷰 시스템 개요](#1-리뷰-시스템-개요)
2. [리뷰 생성 프로세스](#2-리뷰-생성-프로세스)
3. [리뷰 표시 및 정렬 메커니즘](#3-리뷰-표시-및-정렬-메커니즘)
4. [평점 계산 및 분석 시스템](#4-평점-계산-및-분석-시스템)
5. [소셜 상호작용 기능](#5-소셜-상호작용-기능)
6. [리뷰 모더레이션 시스템](#6-리뷰-모더레이션-시스템)
7. [리뷰어 평판 시스템](#7-리뷰어-평판-시스템)
8. [리뷰 관리 기능](#8-리뷰-관리-기능)
9. [프론트엔드 구현 상세](#9-프론트엔드-구현-상세)
10. [백엔드 구현 상세](#10-백엔드-구현-상세)
11. [데이터베이스 스키마 및 관계](#11-데이터베이스-스키마-및-관계)
12. [다른 시스템과의 통합](#12-다른-시스템과의-통합)

## 1. 리뷰 시스템 개요

리뷰 시스템은 레시피 공유 플랫폼의 핵심 기능으로, 사용자들이 레시피에 대한 평가와 의견을 공유할 수 있는 종합적인 플랫폼입니다. 이 시스템은 다음과 같은 주요 목적을 가지고 있습니다:

- **품질 평가**: 사용자들이 레시피의 품질을 평가하고 피드백을 제공
- **신뢰성 향상**: 검증된 요리사 배지와 같은 기능을 통해 리뷰의 신뢰성 제고
- **커뮤니티 형성**: 리뷰를 통한 사용자 간 상호작용 촉진
- **콘텐츠 발견**: 평점과 리뷰를 기반으로 한 레시피 추천 및 검색 개선

### 1.1 시스템 구성 요소

리뷰 시스템은 다음과 같은 주요 구성 요소로 이루어져 있습니다:

1. **리뷰 엔티티(ReviewEntity)**: 리뷰 데이터를 저장하는 핵심 데이터 모델
2. **리뷰 컨트롤러(ReviewController)**: 리뷰 관련 HTTP 요청을 처리하는 컨트롤러
3. **리뷰 서비스(ReviewService)**: 리뷰 관련 비즈니스 로직을 처리하는 서비스
4. **리뷰 레포지토리(ReviewRepository)**: 리뷰 데이터에 대한 데이터베이스 액세스를 담당
5. **리뷰 좋아요 엔티티(ReviewLikeEntity)**: 리뷰 좋아요 데이터를 저장하는 모델
6. **리뷰 좋아요 레포지토리(ReviewLikeRepository)**: 리뷰 좋아요 데이터에 대한 데이터베이스 액세스를 담당

### 1.2 시스템 아키텍처

리뷰 시스템은 Spring Boot 기반의 MVC 아키텍처를 따르고 있으며, 다음과 같은 계층 구조로 구성되어 있습니다:

- **프레젠테이션 계층**: ReviewController가 HTTP 요청을 처리하고 뷰를 반환
- **비즈니스 계층**: ReviewService가 비즈니스 로직을 처리
- **데이터 액세스 계층**: ReviewRepository와 ReviewLikeRepository가 데이터베이스 액세스를 담당
- **데이터 모델**: ReviewEntity와 ReviewLikeEntity가 데이터 구조를 정의

## 2. 리뷰 생성 프로세스

리뷰 생성은 사용자가 레시피에 대한 평가와 의견을 제출하는 과정입니다. 이 프로세스는 다음과 같은 단계로 이루어집니다:

### 2.1 리뷰 작성 폼 접근

1. 사용자가 레시피 상세 페이지에서 "리뷰 작성" 버튼을 클릭
2. ReviewController의 `reviewForm` 메소드가 호출됨
3. 메소드는 사용자 인증 정보(PrincipalDetail)를 확인
4. 레시피 ID를 기반으로 해당 레시피 정보를 조회
5. 리뷰 작성 폼 페이지(review-form.html)를 반환

```java
// ReviewController.java
@GetMapping("/recipe/{recipeId}/review/new")
public String reviewForm(@PathVariable Long recipeId, Model model, @AuthenticationPrincipal PrincipalDetail principalDetail) {
    // 사용자 인증 확인
    // 레시피 정보 조회
    // 모델에 데이터 추가
    return "reviews/review-form";
}
```

### 2.2 리뷰 데이터 입력

사용자는 리뷰 작성 폼에서 다음과 같은 정보를 입력합니다:

1. **제목(title)**: 리뷰의 제목
2. **내용(content)**: 리뷰의 상세 내용 (HTML 태그 지원)
3. **평점(rating)**: 1-5점 척도의 별점
4. **이미지(image)**: 선택적으로 요리 사진 업로드
5. **구매 확인(buyer)**: 재료 구매 여부 체크박스
6. **요리 확인(viewer)**: 실제 요리 여부 체크박스

프론트엔드에서는 다음과 같은 유효성 검사를 수행합니다:
- 제목과 내용의 최소/최대 길이 확인
- 평점 선택 여부 확인
- 이미지 크기 및 형식 검증

### 2.3 리뷰 제출 및 처리

1. 사용자가 폼을 제출하면 ReviewController의 `addReview` 메소드가 호출됨
2. 메소드는 다음과 같은 작업을 수행:
   - 사용자 인증 정보 확인
   - 입력 데이터 유효성 검사
   - 이미지 업로드 처리 (FileService 활용)
   - ReviewDTO 객체 생성 및 데이터 설정
   - ReviewService의 `registerReview` 메소드 호출

```java
// ReviewController.java
@PostMapping("/recipe/{recipeId}/review/new")
public String addReview(@ModelAttribute ReviewDTO reviewDTO, @RequestParam("image") MultipartFile image,
                        @AuthenticationPrincipal PrincipalDetail principalDetail, Model model) {
    // 사용자 인증 확인
    // 이미지 처리
    // ReviewDTO 설정
    // 리뷰 등록 서비스 호출
    reviewService.registerReview(reviewDTO);
    return "redirect:/recipe/" + reviewDTO.getRecipeId();
}
```

### 2.4 서비스 계층 처리

ReviewService의 `registerReview` 메소드는 다음과 같은 작업을 수행합니다:

1. ReviewDTO를 ReviewEntity로 변환 (dtoToEntity 메소드 사용)
2. 레시피 엔티티와 연결
3. 사용자 엔티티와 연결
4. 현재 시간을 생성 시간으로 설정
5. ReviewRepository를 통해 데이터베이스에 저장
6. 레시피의 평균 평점 업데이트

```java
// ReviewServiceImpl.java (구현체)
@Override
public void registerReview(ReviewDTO reviewDTO) {
    RecipeEntity recipe = recipeRepository.findById(reviewDTO.getRecipeId())
            .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

    ReviewEntity review = dtoToEntity(reviewDTO, recipe);
    // 추가 설정

    reviewRepository.save(review);
    // 평균 평점 업데이트
}
```

### 2.5 리뷰 생성 완료

리뷰 생성이 완료되면 사용자는 레시피 상세 페이지로 리다이렉트되며, 새로 작성한 리뷰를 포함한 모든 리뷰 목록을 확인할 수 있습니다.

## 3. 리뷰 표시 및 정렬 메커니즘

리뷰 표시는 사용자가 레시피에 대한 다른 사용자들의 평가와 의견을 확인할 수 있는 기능입니다. 이 메커니즘은 다음과 같은 단계로 이루어집니다:

### 3.1 리뷰 목록 조회

1. 사용자가 레시피 상세 페이지에 접근하면 RecipeController의 `recipeRead` 메소드가 호출됨
2. 메소드는 레시피 정보와 함께 해당 레시피의 리뷰 정보를 조회
3. ReviewService의 `getReviewsByRecipePaged` 메소드를 호출하여 페이징 처리된 리뷰 목록을 가져옴
4. 평균 평점은 ReviewService의 `calculateAverageRating` 메소드를 통해 계산

```java
// RecipeController.java
@GetMapping("/recipe/{id}")
public String recipeRead(@PathVariable Long id, Model model) {
    // 레시피 정보 조회
    // 리뷰 목록 조회
    Page<ReviewEntity> reviews = reviewService.getReviewsByRecipePaged(id, "latest", pageable);
    double averageRating = reviewService.calculateAverageRating(id);

    model.addAttribute("reviews", reviews);
    model.addAttribute("averageRating", averageRating);

    return "recipe/view";
}
```

### 3.2 리뷰 정렬 기능

리뷰 목록은 다음과 같은 기준으로 정렬할 수 있습니다:

1. **최신순(latest)**: 가장 최근에 작성된 리뷰부터 표시
2. **평점순(rating)**: 높은 평점의 리뷰부터 표시
3. **유용한 순(helpful)**: 좋아요 수가 많은 리뷰부터 표시

정렬 기능은 다음과 같이 구현됩니다:

```java
// ReviewServiceImpl.java
@Override
public Page<ReviewEntity> getReviewsByRecipePaged(Long recipeId, String sortBy, Pageable pageable) {
    RecipeEntity recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

    switch (sortBy) {
        case "rating":
            return reviewRepository.findByRecipeOrderByRatingDesc(recipe, pageable);
        case "helpful":
            return reviewRepository.findByRecipeOrderByLikesCountDesc(recipe, pageable);
        case "latest":
        default:
            return reviewRepository.findByRecipeOrderByCreatedAtDesc(recipe, pageable);
    }
}
```

### 3.3 리뷰 필터링 기능

리뷰 목록은 다음과 같은 조건으로 필터링할 수 있습니다:

1. **검증된 요리사**: 실제 요리를 한 사용자(viewer=true)의 리뷰만 표시
2. **구매 확인**: 재료를 구매한 사용자(buyer=true)의 리뷰만 표시
3. **평점 범위**: 특정 평점 이상/이하의 리뷰만 표시
4. **키워드 검색**: 제목이나 내용에 특정 키워드가 포함된 리뷰만 표시

```java
// ReviewController.java
@GetMapping("/recipe/{recipeId}/reviews")
public String listReviews(@PathVariable Long recipeId, 
                         @RequestParam(required = false) String searchType,
                         @RequestParam(required = false) String keyword,
                         Model model) {
    // 검색 조건에 따른 리뷰 목록 조회
    List<ReviewEntity> reviews;

    if (searchType != null && keyword != null) {
        if (searchType.equals("writer")) {
            reviews = reviewService.getReviewsByRecipeAndWriter(recipeId, keyword);
        } else if (searchType.equals("content")) {
            reviews = reviewService.getReviewsByRecipeAndContent(recipeId, keyword);
        } else {
            reviews = reviewService.getReviewsByRecipe(recipeId);
        }
    } else {
        reviews = reviewService.getReviewsByRecipe(recipeId);
    }

    model.addAttribute("reviews", reviews);
    return "reviews/list";
}
```

### 3.4 리뷰 요약 통계

레시피 상세 페이지에는 다음과 같은 리뷰 요약 통계가 표시됩니다:

1. **평균 평점**: 모든 리뷰의 평점 평균 (소수점 한 자리까지 표시)
2. **리뷰 수**: 해당 레시피에 작성된 총 리뷰 수
3. **평점 분포**: 각 별점(1-5점)별 리뷰 수 또는 비율을 시각적으로 표시

```java
// ReviewServiceImpl.java
@Override
public double calculateAverageRating(Long recipeId) {
    List<ReviewEntity> reviews = getReviewsByRecipe(recipeId);

    if (reviews.isEmpty()) {
        return 0.0;
    }

    double sum = reviews.stream()
            .mapToDouble(ReviewEntity::getRating)
            .sum();

    return Math.round((sum / reviews.size()) * 10) / 10.0; // 소수점 한 자리까지 반올림
}
```

### 3.5 리뷰 상세 보기

사용자는 리뷰 목록에서 특정 리뷰를 클릭하여 상세 내용을 볼 수 있습니다:

1. 리뷰 클릭 시 ReviewController의 `viewReview` 메소드가 호출됨
2. 메소드는 리뷰 ID를 기반으로 해당 리뷰의 상세 정보를 조회
3. 리뷰 상세 페이지(review-view.html)를 반환

```java
// ReviewController.java
@GetMapping("/review/{id}")
public String viewReview(@PathVariable Long id, Model model) {
    ReviewEntity review = reviewService.readReview(id);
    model.addAttribute("review", review);
    return "reviews/view";
}
```

## 4. 평점 계산 및 분석 시스템

평점 시스템은 레시피의 품질을 정량적으로 평가하고 사용자들에게 유용한 정보를 제공하는 중요한 기능입니다.

### 4.1 평균 평점 계산

평균 평점은 ReviewService의 `calculateAverageRating` 메소드를 통해 계산됩니다:

1. 해당 레시피의 모든 리뷰를 조회
2. 각 리뷰의 평점을 합산
3. 리뷰 수로 나누어 평균 계산
4. 소수점 한 자리까지 반올림하여 반환

이 평균 평점은 다음과 같은 곳에 사용됩니다:
- 레시피 상세 페이지의 평점 표시
- 레시피 목록에서의 평점 표시
- 검색 결과 정렬 기준
- 추천 알고리즘의 입력 데이터

### 4.2 평점 분포 시각화

평점 분포는 각 별점(1-5점)별 리뷰 수를 시각적으로 표현합니다:

1. 각 별점별 리뷰 수 계산
2. 전체 리뷰 수 대비 비율 계산
3. 막대 그래프 또는 퍼센트 바로 시각화

```javascript
// 프론트엔드 JavaScript 코드 (예시)
function renderRatingDistribution(ratings) {
    const total = ratings.reduce((sum, count) => sum + count, 0);

    for (let i = 0; i < 5; i++) {
        const percent = total > 0 ? (ratings[i] / total * 100) : 0;
        document.querySelector(`.rating-bar-${i+1}`).style.width = `${percent}%`;
        document.querySelector(`.rating-percent-${i+1}`).textContent = `${Math.round(percent)}%`;
    }
}
```

### 4.3 시간에 따른 추세 분석

시간에 따른 평점 추세를 분석하여 레시피의 품질 변화를 파악할 수 있습니다:

1. 리뷰 데이터를 기간별로 그룹화 (월별, 분기별 등)
2. 각 기간의 평균 평점 계산
3. 시간 순서대로 평점 변화를 그래프로 시각화

이 기능은 다음과 같은 목적으로 사용됩니다:
- 레시피 개선 효과 측정
- 사용자 피드백에 대한 대응 효과 확인
- 장기적인 품질 추세 파악

### 4.4 비교 평점 지표

레시피의 평점을 다른 레시피와 비교하여 상대적인 품질을 평가할 수 있습니다:

1. 카테고리 평균과 비교: 같은 카테고리 내 다른 레시피들의 평균 평점과 비교
2. 유사 레시피와 비교: 비슷한 재료나 조리법을 가진 레시피들과 평점 비교
3. 상대적 순위 표시: 전체 레시피 중 평점 기준 상위 몇 퍼센트인지 표시

### 4.5 레시피 가시성에 대한 평점 영향

평점은 레시피의 가시성에 직접적인 영향을 미칩니다:

1. 높은 평점 레시피 우선 표시: 메인 페이지나 추천 섹션에 높은 평점의 레시피 우선 배치
2. 검색 결과 순위에 평점 반영: 검색 알고리즘에 평점을 가중치로 적용
3. 추천 알고리즘에 평점 활용: 사용자 맞춤 추천 시 평점이 높은 레시피 우선 추천

```java
// RecipeServiceImpl.java (예시)
@Override
public List<RecipeEntity> getTopRatedRecipes(int limit) {
    return recipeRepository.findAll(Sort.by(Sort.Direction.DESC, "averageRating"))
            .stream()
            .limit(limit)
            .collect(Collectors.toList());
}
```

## 5. 소셜 상호작용 기능

리뷰 시스템은 사용자 간 상호작용을 촉진하는 다양한 소셜 기능을 제공합니다.

### 5.1 리뷰 좋아요/유용함 투표

사용자들은 유용하거나 도움이 되는 리뷰에 좋아요를 표시할 수 있습니다:

1. 리뷰 좋아요 버튼 클릭 시 ReviewController의 `toggleReviewLike` 메소드가 호출됨
2. 메소드는 ReviewService의 `toggleReviewLike` 메소드를 호출하여 좋아요 상태 토글
3. 이미 좋아요를 누른 경우 취소, 아닌 경우 좋아요 추가
4. 리뷰의 좋아요 수 업데이트

```java
// ReviewController.java
@PostMapping("/review/{id}/like")
@ResponseBody
public Map<String, Object> toggleReviewLike(@PathVariable Long id, 
                                          @AuthenticationPrincipal PrincipalDetail principalDetail) {
    boolean liked = reviewService.toggleReviewLike(id, principalDetail.getUsername());
    int likesCount = reviewService.getReviewLikesCount(id);

    Map<String, Object> response = new HashMap<>();
    response.put("liked", liked);
    response.put("likesCount", likesCount);

    return response;
}
```

### 5.2 리뷰 공유 기능

사용자들은 유용한 리뷰를 다른 사람들과 공유할 수 있습니다:

1. 소셜 미디어 공유 링크: 주요 소셜 미디어 플랫폼으로 리뷰 공유
2. 이메일 공유 옵션: 이메일을 통해 리뷰 공유
3. 공유 URL 생성: 리뷰 직접 링크 생성 및 복사 기능

```javascript
// 프론트엔드 JavaScript 코드 (예시)
function shareReview(platform, reviewId) {
    const reviewUrl = `${window.location.origin}/review/${reviewId}`;

    switch (platform) {
        case 'facebook':
            window.open(`https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(reviewUrl)}`);
            break;
        case 'twitter':
            window.open(`https://twitter.com/intent/tweet?url=${encodeURIComponent(reviewUrl)}`);
            break;
        case 'email':
            window.location.href = `mailto:?subject=Check out this recipe review&body=${encodeURIComponent(reviewUrl)}`;
            break;
        case 'copy':
            navigator.clipboard.writeText(reviewUrl)
                .then(() => alert('리뷰 링크가 클립보드에 복사되었습니다.'));
            break;
    }
}
```

### 5.3 리뷰 댓글 스레드

리뷰에 대한 댓글 기능을 통해 사용자들 간의 토론이 가능합니다:

1. 리뷰에 댓글 작성 시 ReviewController의 `addReplyToReview` 메소드가 호출됨
2. 메소드는 ReviewService의 `addReplyToReview` 메소드를 호출하여 댓글 추가
3. 댓글은 리뷰 엔티티의 reply 필드에 저장 (현재는 단일 댓글만 지원)
4. 댓글 작성 시 알림 시스템을 통해 리뷰 작성자에게 알림 발송

```java
// ReviewController.java
@PostMapping("/review/{id}/reply")
public String addReplyToReview(@PathVariable Long id, 
                             @RequestParam String reply,
                             @AuthenticationPrincipal PrincipalDetail principalDetail,
                             Model model) {
    reviewService.addReplyToReview(id, reply, principalDetail.getUsername());
    return "redirect:/review/" + id;
}
```

### 5.4 사용자 프로필 연동

리뷰 시스템은 사용자 프로필과 긴밀하게 연동됩니다:

1. 리뷰와 사용자 프로필 연결: 각 리뷰에는 작성자 정보와 프로필 링크 표시
2. 사용자별 리뷰 이력 표시: 사용자 프로필 페이지에서 작성한 모든 리뷰 확인 가능
3. 프로필에서 작성 리뷰 모아보기: 특정 사용자가 작성한 모든 리뷰를 한 곳에서 확인

```java
// UserController.java (예시)
@GetMapping("/user/{username}/reviews")
public String userReviews(@PathVariable String username, Model model) {
    UserEntity user = userService.findByUsername(username);
    List<ReviewEntity> reviews = reviewRepository.findByUserOrderByCreatedAtDesc(user);

    model.addAttribute("user", user);
    model.addAttribute("reviews", reviews);

    return "user/reviews";
}
```

### 5.5 리뷰어 팔로우 기능

사용자들은 유용한 리뷰를 작성하는 리뷰어를 팔로우할 수 있습니다:

1. 특정 리뷰어 팔로우 시스템: 사용자 프로필에서 팔로우 버튼 제공
2. 팔로우한 리뷰어의 새 리뷰 알림: 팔로우한 사용자가 새 리뷰 작성 시 알림 발송
3. 인기 리뷰어 추천: 많은 좋아요를 받은 리뷰를 작성한 사용자를 인기 리뷰어로 추천

## 6. 리뷰 모더레이션 시스템

리뷰 모더레이션 시스템은 콘텐츠 품질을 유지하고 부적절한 내용을 필터링하는 역할을 합니다.

### 6.1 자동 콘텐츠 필터링

시스템은 다음과 같은 자동 필터링 기능을 제공합니다:

1. **부적절한 언어 필터링**: 욕설, 혐오 발언 등 부적절한 언어 자동 감지 및 필터링
2. **스팸 탐지 알고리즘**: 반복적인 내용, 홍보성 내용 등 스팸 리뷰 탐지
3. **중복 리뷰 방지**: 동일 사용자의 동일 레시피에 대한 중복 리뷰 방지

```java
// 리뷰 등록 전 필터링 예시 (서비스 계층)
private boolean containsInappropriateContent(String content) {
    List<String> bannedWords = Arrays.asList("욕설1", "욕설2", "욕설3");
    return bannedWords.stream().anyMatch(content.toLowerCase()::contains);
}
```

### 6.2 부적절한 리뷰 신고 메커니즘

사용자들은 부적절한 리뷰를 신고할 수 있습니다:

1. 리뷰 옆에 신고 버튼 제공
2. 신고 사유 선택 (스팸, 부적절한 내용, 혐오 발언 등)
3. 신고된 리뷰는 관리자 검토 대기열에 추가
4. 일정 횟수 이상 신고된 리뷰는 자동으로 숨김 처리

```java
// ReviewController.java (예시)
@PostMapping("/review/{id}/report")
public String reportReview(@PathVariable Long id, 
                         @RequestParam String reason,
                         @AuthenticationPrincipal PrincipalDetail principalDetail) {
    reviewService.reportReview(id, reason, principalDetail.getUsername());
    return "redirect:/recipe/" + reviewService.readReview(id).getRecipe().getId();
}
```

### 6.3 관리자 리뷰 대기열

관리자는 신고된 리뷰를 검토하고 적절한 조치를 취할 수 있습니다:

1. 관리자 대시보드에 신고된 리뷰 목록 표시
2. 리뷰 내용, 신고 사유, 신고 횟수 등 정보 제공
3. 승인, 거부, 수정 요청 등의 조치 옵션 제공
4. 처리된 리뷰는 대기열에서 제거

```java
// AdminController.java (예시)
@GetMapping("/admin/reviews/reported")
public String reportedReviews(Model model) {
    List<ReviewEntity> reportedReviews = reviewService.getReportedReviews();
    model.addAttribute("reportedReviews", reportedReviews);
    return "admin/reported-reviews";
}
```

### 6.4 모더레이션 작업 추적

모든 모더레이션 작업은 추적되어 시스템 개선에 활용됩니다:

1. 모더레이션 이력 기록: 어떤 관리자가 어떤 리뷰에 어떤 조치를 취했는지 기록
2. 관리자별 작업 통계: 각 관리자의 모더레이션 활동 통계 제공
3. 모더레이션 효율성 측정: 신고부터 처리까지의 시간, 처리 결과 분포 등 측정

### 6.5 커뮤니티 가이드라인 시행

명확한 커뮤니티 가이드라인을 수립하고 시행합니다:

1. 리뷰 작성 시 가이드라인 안내
2. 가이드라인 위반 시 경고 및 제재
3. 반복 위반자에 대한 계정 제한 또는 정지
4. 가이드라인 준수 시 인센티브 제공 (배지, 포인트 등)

## 7. 리뷰어 평판 시스템

리뷰어 평판 시스템은 신뢰할 수 있는 리뷰어를 식별하고 보상하는 기능입니다.

### 7.1 리뷰어 신뢰도 점수

각 사용자의 리뷰 활동을 기반으로 신뢰도 점수를 계산합니다:

1. 작성한 리뷰 수와 품질
2. 받은 좋아요 수
3. 신고 횟수 (감점 요소)
4. 활동 기간

```java
// UserServiceImpl.java (예시)
public int calculateUserCredibility(String username) {
    UserEntity user = userRepository.findByUsername(username);
    List<ReviewEntity> reviews = reviewRepository.findByUser(user);

    int baseScore = 50; // 기본 점수
    int reviewCount = reviews.size();
    int likesCount = reviews.stream().mapToInt(ReviewEntity::getLikesCount).sum();
    int reportCount = reviewReportRepository.countByReviewUserAndStatus(user, "CONFIRMED");

    return baseScore + (reviewCount * 2) + (likesCount) - (reportCount * 10);
}
```

### 7.2 구매 확인 배지

실제 재료를 구매한 사용자의 리뷰에는 특별한 배지가 표시됩니다:

1. 플랫폼 내에서 레시피 재료 구매 시 자동으로 구매 확인 배지 부여
2. 구매 확인 배지가 있는 리뷰는 더 높은 신뢰도를 가짐
3. 필터링 옵션으로 구매 확인 리뷰만 볼 수 있는 기능 제공

### 7.3 기여도 수준 표시

사용자의 리뷰 활동 수준에 따라 기여도 레벨을 표시합니다:

1. 작성 리뷰 수에 따른 레벨 시스템 (초보 리뷰어, 중급 리뷰어, 전문 리뷰어 등)
2. 활동 기간 반영 (6개월 이상 활동, 1년 이상 활동 등)
3. 시각적 레벨 배지 제공 (프로필 및 리뷰에 표시)

```java
// 리뷰어 레벨 계산 예시
public String calculateReviewerLevel(UserEntity user) {
    int reviewCount = reviewRepository.countByUser(user);

    if (reviewCount >= 100) {
        return "전문 리뷰어";
    } else if (reviewCount >= 50) {
        return "중급 리뷰어";
    } else if (reviewCount >= 20) {
        return "활발한 리뷰어";
    } else if (reviewCount >= 5) {
        return "초보 리뷰어";
    } else {
        return "신규 리뷰어";
    }
}
```

### 7.4 전문 리뷰어 인정

요리 전문가나 전문 요리사의 리뷰에는 특별한 인증 마크가 표시됩니다:

1. 전문 요리사 또는 전문가 인증 프로세스 제공
2. 인증된 전문가에게 특별 배지 부여
3. 전문가 리뷰를 우선적으로 표시
4. 전문가 리뷰만 볼 수 있는 필터링 옵션 제공

### 7.5 리뷰 이력 접근성

사용자의 리뷰 이력을 쉽게 확인할 수 있는 기능을 제공합니다:

1. 사용자 프로필에서 작성한 모든 리뷰 확인 가능
2. 리뷰 패턴 및 일관성 분석 제공
3. 시간에 따른 리뷰 성향 변화 시각화

## 8. 리뷰 관리 기능

사용자가 자신의 리뷰를 관리할 수 있는 다양한 기능을 제공합니다.

### 8.1 사용자 리뷰 대시보드

사용자는 자신이 작성한 모든 리뷰를 한 곳에서 관리할 수 있습니다:

1. 작성한 리뷰 목록 표시
2. 각 리뷰의 상태 및 통계 정보 제공 (조회수, 좋아요 수 등)
3. 받은 피드백 확인 (댓글, 좋아요 등)

```java
// UserController.java (예시)
@GetMapping("/user/reviews")
public String userReviewDashboard(@AuthenticationPrincipal PrincipalDetail principalDetail, Model model) {
    List<ReviewEntity> userReviews = reviewRepository.findByUserOrderByCreatedAtDesc(principalDetail.getUser());
    model.addAttribute("reviews", userReviews);
    return "user/reviews-dashboard";
}
```

### 8.2 편집 및 삭제 기능

사용자는 자신이 작성한 리뷰를 편집하거나 삭제할 수 있습니다:

1. 리뷰 편집 기능: 내용, 평점, 이미지 등 수정 가능
2. 리뷰 삭제 기능: 작성 후 일정 기간 내에 삭제 가능
3. 수정 이력 관리: 리뷰 수정 시 수정 일시 및 이력 기록

```java
// ReviewController.java
@GetMapping("/review/{id}/edit")
public String updateForm(@PathVariable Long id, Model model, @AuthenticationPrincipal PrincipalDetail principalDetail) {
    ReviewEntity review = reviewService.readReview(id);

    // 작성자 확인
    if (!review.getUser().getUsername().equals(principalDetail.getUsername())) {
        return "redirect:/recipe/" + review.getRecipe().getId();
    }

    model.addAttribute("review", review);
    return "reviews/update-form";
}

@PostMapping("/review/{id}/edit")
public String updateReview(@PathVariable Long id, @ModelAttribute ReviewDTO reviewDTO, 
                         @RequestParam("image") MultipartFile image,
                         @RequestParam(value = "deleteImage", required = false) Boolean deleteImage,
                         @AuthenticationPrincipal PrincipalDetail principalDetail,
                         Model model) {
    // 작성자 확인 및 리뷰 업데이트 로직
    reviewService.updateReview(id, reviewDTO);
    return "redirect:/review/" + id;
}
```

### 8.3 리뷰 이력 추적

시스템은 사용자의 리뷰 활동 이력을 추적합니다:

1. 사용자별 리뷰 활동 기록 유지
2. 시간순 리뷰 이력 제공
3. 리뷰 패턴 분석 (선호하는 레시피 유형, 평균 평점 등)

### 8.4 응답 알림 시스템

리뷰에 대한 다양한 상호작용 시 알림을 제공합니다:

1. 리뷰에 댓글이 달렸을 때 알림
2. 리뷰가 좋아요를 받았을 때 알림
3. 레시피 작성자가 리뷰에 응답했을 때 알림
4. 알림 설정 관리 기능 (이메일, 앱 내 알림 등)

### 8.5 리뷰 영향력 지표

사용자의 리뷰가 미치는 영향을 측정하는 지표를 제공합니다:

1. 리뷰 조회수 통계
2. 유용함 투표 수
3. 리뷰 기반 레시피 인기도 변화 (리뷰 작성 전후 비교)
4. 리뷰 영향력 점수 계산 및 표시

## 9. 프론트엔드 구현 상세

리뷰 시스템의 프론트엔드 구현에 대한 상세 내용입니다.

### 9.1 동적 별점 인터페이스

사용자가 직관적으로 별점을 선택할 수 있는 인터페이스를 제공합니다:

1. JavaScript 기반 별점 선택 UI
2. 마우스 오버 시 별점 미리보기 효과
3. 클릭으로 별점 확정
4. 모바일 터치 지원

```javascript
// 별점 선택 UI 예시 (JavaScript)
document.querySelectorAll('.star-rating .star').forEach(star => {
    star.addEventListener('mouseover', function() {
        const rating = this.getAttribute('data-rating');
        highlightStars(rating);
    });

    star.addEventListener('click', function() {
        const rating = this.getAttribute('data-rating');
        document.getElementById('rating-input').value = rating;
        highlightStars(rating);
    });
});

function highlightStars(rating) {
    document.querySelectorAll('.star-rating .star').forEach(star => {
        const starRating = star.getAttribute('data-rating');
        if (starRating <= rating) {
            star.classList.add('active');
        } else {
            star.classList.remove('active');
        }
    });
}
```

### 9.2 실시간 유효성 검사 피드백

사용자가 리뷰를 작성하는 동안 실시간으로 유효성 검사 피드백을 제공합니다:

1. 입력 중 글자 수 표시 (최소/최대 길이 제한)
2. 필수 필드 검증 (제목, 내용, 평점 등)
3. 오류 메시지 즉시 표시
4. 제출 버튼 활성화/비활성화

```javascript
// 실시간 유효성 검사 예시 (JavaScript)
const contentInput = document.getElementById('review-content');
const charCounter = document.getElementById('char-counter');
const minLength = 20;
const maxLength = 1000;

contentInput.addEventListener('input', function() {
    const currentLength = this.value.length;
    charCounter.textContent = `${currentLength}/${maxLength}`;

    if (currentLength < minLength) {
        charCounter.classList.add('text-danger');
        charCounter.textContent += ` (최소 ${minLength}자 필요)`;
    } else if (currentLength > maxLength) {
        charCounter.classList.add('text-danger');
        this.value = this.value.substring(0, maxLength);
    } else {
        charCounter.classList.remove('text-danger');
    }

    validateForm();
});
```

### 9.3 모바일 최적화 리뷰 폼

모바일 기기에서도 사용하기 편리한 리뷰 폼을 제공합니다:

1. 터치 친화적 UI 요소 (큰 버튼, 충분한 간격)
2. 모바일 화면에 최적화된 레이아웃
3. 이미지 업로드 간소화 (카메라 직접 접근 등)
4. 반응형 디자인으로 다양한 화면 크기 지원

```css
/* 모바일 최적화 CSS 예시 */
@media (max-width: 768px) {
    .review-form .form-control {
        font-size: 16px; /* iOS에서 자동 확대 방지 */
        padding: 12px;
    }

    .review-form .btn {
        padding: 12px 16px;
        font-size: 16px;
        width: 100%;
        margin-bottom: 10px;
    }

    .star-rating .star {
        font-size: 30px;
        margin: 0 5px;
    }
}
```

### 9.4 무한 스크롤 리뷰 로딩

사용자가 스크롤을 내릴 때 추가 리뷰를 자동으로 로드하는 기능을 제공합니다:

1. 스크롤 이벤트 감지
2. AJAX를 통한 비동기 리뷰 로딩
3. 로딩 상태 표시 (스피너, 스켈레톤 UI 등)
4. 부드러운 전환 효과

```javascript
// 무한 스크롤 구현 예시 (JavaScript)
let page = 1;
let loading = false;
const recipeId = document.getElementById('recipe-id').value;

window.addEventListener('scroll', function() {
    if (loading) return;

    if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 500) {
        loading = true;
        loadMoreReviews();
    }
});

function loadMoreReviews() {
    const loadingIndicator = document.getElementById('loading-indicator');
    loadingIndicator.style.display = 'block';

    fetch(`/api/recipe/${recipeId}/reviews?page=${page}`)
        .then(response => response.json())
        .then(data => {
            if (data.reviews.length > 0) {
                appendReviews(data.reviews);
                page++;
            } else {
                loadingIndicator.textContent = '더 이상 리뷰가 없습니다.';
            }
            loading = false;
        })
        .catch(error => {
            console.error('Error loading reviews:', error);
            loading = false;
        });
}
```

### 9.5 대화형 평점 시각화

평점 데이터를 시각적으로 표현하는 다양한 방법을 제공합니다:

1. 애니메이션 효과의 별점 표시
2. 평점 분포 그래프 (막대 그래프, 원형 차트 등)
3. 색상으로 평점 수준 구분 (빨간색: 낮음, 녹색: 높음 등)
4. 인터랙티브 요소 (호버 시 상세 정보 표시 등)

## 10. 백엔드 구현 상세

리뷰 시스템의 백엔드 구현에 대한 상세 내용입니다.

### 10.1 리뷰 엔티티 구조

ReviewEntity 클래스는 리뷰 데이터를 저장하는 핵심 모델입니다:

```java
@Entity
@Table(name = "reviews")
@Getter @Setter
public class ReviewEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private int rating; // 1-5 별점

    private String imagePath; // 이미지 경로

    private boolean buyer; // 재료 구매 여부

    private boolean viewer; // 실제 요리 여부

    private String reply; // 레시피 작성자의 답변

    private LocalDateTime replyDate; // 답변 작성 시간

    private int likesCount; // 좋아요 수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private RecipeEntity recipe; // 리뷰 대상 레시피

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user; // 리뷰 작성자

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewLikeEntity> likes = new ArrayList<>(); // 좋아요 목록
}
```

### 10.2 평점 계산 서비스

ReviewService는 평점 계산 및 관리를 담당합니다:

```java
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    @Override
    public double calculateAverageRating(Long recipeId) {
        List<ReviewEntity> reviews = getReviewsByRecipe(recipeId);

        if (reviews.isEmpty()) {
            return 0.0;
        }

        double sum = reviews.stream()
                .mapToDouble(ReviewEntity::getRating)
                .sum();

        return Math.round((sum / reviews.size()) * 10) / 10.0;
    }

    // 캐싱을 통한 성능 최적화 예시
    @Cacheable(value = "averageRatings", key = "#recipeId")
    public double getAverageRatingCached(Long recipeId) {
        return calculateAverageRating(recipeId);
    }

    @CacheEvict(value = "averageRatings", key = "#recipeId")
    public void clearRatingCache(Long recipeId) {
        // 캐시 삭제 메소드 (리뷰 추가/수정/삭제 시 호출)
    }
}
```

### 10.3 모더레이션 서비스

리뷰 모더레이션을 위한 서비스 로직을 구현합니다:

```java
@Service
@RequiredArgsConstructor
public class ModerationServiceImpl implements ModerationService {
    private final ReviewRepository reviewRepository;
    private final ReviewReportRepository reviewReportRepository;

    @Override
    public void reportReview(Long reviewId, String reason, String reporterUsername) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        UserEntity reporter = userRepository.findByUsername(reporterUsername);

        ReviewReportEntity report = new ReviewReportEntity();
        report.setReview(review);
        report.setReporter(reporter);
        report.setReason(reason);
        report.setStatus("PENDING");
        report.setReportDate(LocalDateTime.now());

        reviewReportRepository.save(report);

        // 신고 횟수가 임계값을 넘으면 자동 숨김 처리
        int reportCount = reviewReportRepository.countByReviewAndStatus(review, "PENDING");
        if (reportCount >= 5) {
            review.setHidden(true);
            reviewRepository.save(review);
        }
    }

    @Override
    public void moderateReview(Long reportId, String action, String moderatorUsername) {
        ReviewReportEntity report = reviewReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));
        UserEntity moderator = userRepository.findByUsername(moderatorUsername);

        report.setStatus(action); // "APPROVED", "REJECTED", "EDIT_REQUESTED"
        report.setModerator(moderator);
        report.setModeratedDate(LocalDateTime.now());

        reviewReportRepository.save(report);

        // 조치에 따른 리뷰 상태 변경
        ReviewEntity review = report.getReview();
        if ("APPROVED".equals(action)) {
            review.setHidden(true);
        } else if ("REJECTED".equals(action)) {
            review.setHidden(false);
        }

        reviewRepository.save(review);
    }
}
```

### 10.4 소셜 상호작용 서비스

리뷰에 대한 소셜 상호작용을 처리하는 서비스 로직을 구현합니다:

```java
@Service
@RequiredArgsConstructor
public class ReviewSocialServiceImpl implements ReviewSocialService {
    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    public boolean toggleReviewLike(Long reviewId, String username) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
        UserEntity user = userRepository.findByUsername(username);

        Optional<ReviewLikeEntity> existingLike = reviewLikeRepository
                .findByReviewAndUser(review, user);

        if (existingLike.isPresent()) {
            // 좋아요 취소
            reviewLikeRepository.delete(existingLike.get());
            updateReviewLikesCount(reviewId);
            return false;
        } else {
            // 좋아요 추가
            ReviewLikeEntity like = new ReviewLikeEntity();
            like.setReview(review);
            like.setUser(user);
            like.setCreatedAt(LocalDateTime.now());
            reviewLikeRepository.save(like);

            // 리뷰 작성자에게 알림 발송
            notificationService.sendNotification(
                review.getUser().getUsername(),
                "새로운 좋아요",
                username + "님이 회원님의 리뷰를 좋아합니다.",
                "/review/" + reviewId
            );

            updateReviewLikesCount(reviewId);
            return true;
        }
    }

    @Override
    public void updateReviewLikesCount(Long reviewId) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        int likesCount = reviewLikeRepository.countByReview(review);
        review.setLikesCount(likesCount);
        reviewRepository.save(review);
    }
}
```

### 10.5 데이터 검증 및 정제

리뷰 데이터의 유효성을 검사하고 정제하는 로직을 구현합니다:

```java
@Component
public class ReviewValidator {
    private static final int MIN_CONTENT_LENGTH = 20;
    private static final int MAX_CONTENT_LENGTH = 1000;
    private static final int MIN_TITLE_LENGTH = 5;
    private static final int MAX_TITLE_LENGTH = 100;

    public void validate(ReviewDTO reviewDTO) {
        List<String> errors = new ArrayList<>();

        // 제목 검증
        if (reviewDTO.getTitle() == null || reviewDTO.getTitle().trim().isEmpty()) {
            errors.add("제목은 필수 입력 항목입니다.");
        } else if (reviewDTO.getTitle().length() < MIN_TITLE_LENGTH) {
            errors.add("제목은 최소 " + MIN_TITLE_LENGTH + "자 이상이어야 합니다.");
        } else if (reviewDTO.getTitle().length() > MAX_TITLE_LENGTH) {
            errors.add("제목은 최대 " + MAX_TITLE_LENGTH + "자까지 입력 가능합니다.");
        }

        // 내용 검증
        if (reviewDTO.getContent() == null || reviewDTO.getContent().trim().isEmpty()) {
            errors.add("내용은 필수 입력 항목입니다.");
        } else if (reviewDTO.getContent().length() < MIN_CONTENT_LENGTH) {
            errors.add("내용은 최소 " + MIN_CONTENT_LENGTH + "자 이상이어야 합니다.");
        } else if (reviewDTO.getContent().length() > MAX_CONTENT_LENGTH) {
            errors.add("내용은 최대 " + MAX_CONTENT_LENGTH + "자까지 입력 가능합니다.");
        }

        // 평점 검증
        if (reviewDTO.getRating() < 1 || reviewDTO.getRating() > 5) {
            errors.add("평점은 1-5 사이의 값이어야 합니다.");
        }

        // XSS 방지를 위한 HTML 태그 정제
        if (reviewDTO.getContent() != null) {
            reviewDTO.setContent(sanitizeHtml(reviewDTO.getContent()));
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private String sanitizeHtml(String html) {
        // 허용할 태그와 속성 정의
        Whitelist whitelist = Whitelist.basic();
        whitelist.addTags("h1", "h2", "h3", "h4", "h5", "h6");
        whitelist.addAttributes(":all", "class");

        // HTML 정제
        return Jsoup.clean(html, whitelist);
    }
}
```

## 11. 데이터베이스 스키마 및 관계

리뷰 시스템의 데이터베이스 스키마와 엔티티 간 관계에 대한 상세 내용입니다.

### 11.1 리뷰 테이블 구조

리뷰 데이터는 `reviews` 테이블에 저장됩니다:

```sql
CREATE TABLE reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    rating INT NOT NULL,
    image_path VARCHAR(255),
    buyer BOOLEAN DEFAULT FALSE,
    viewer BOOLEAN DEFAULT FALSE,
    reply TEXT,
    reply_date DATETIME,
    likes_count INT DEFAULT 0,
    recipe_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    FOREIGN KEY (recipe_id) REFERENCES recipes(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_recipe_id (recipe_id),
    INDEX idx_user_id (user_id),
    INDEX idx_rating (rating),
    INDEX idx_created_at (created_at)
);
```

주요 필드 설명:
- `id`: 리뷰의 고유 식별자
- `title`: 리뷰 제목
- `content`: 리뷰 내용
- `rating`: 1-5점 척도의 평점
- `image_path`: 리뷰 이미지 경로
- `buyer`: 재료 구매 여부
- `viewer`: 실제 요리 여부
- `reply`: 레시피 작성자의 답변
- `reply_date`: 답변 작성 시간
- `likes_count`: 좋아요 수
- `recipe_id`: 리뷰 대상 레시피 ID (외래 키)
- `user_id`: 리뷰 작성자 ID (외래 키)
- `created_at`: 리뷰 작성 시간
- `updated_at`: 리뷰 수정 시간

### 11.2 리뷰 좋아요 테이블 구조

리뷰 좋아요 데이터는 `review_likes` 테이블에 저장됩니다:

```sql
CREATE TABLE review_likes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    review_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (review_id) REFERENCES reviews(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE KEY uk_review_user (review_id, user_id),
    INDEX idx_review_id (review_id),
    INDEX idx_user_id (user_id)
);
```

주요 필드 설명:
- `id`: 좋아요의 고유 식별자
- `review_id`: 좋아요 대상 리뷰 ID (외래 키)
- `user_id`: 좋아요를 누른 사용자 ID (외래 키)
- `created_at`: 좋아요 시간

### 11.3 리뷰 신고 테이블 구조

리뷰 신고 데이터는 `review_reports` 테이블에 저장됩니다:

```sql
CREATE TABLE review_reports (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    review_id BIGINT NOT NULL,
    reporter_id BIGINT NOT NULL,
    moderator_id BIGINT,
    reason VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    report_date DATETIME NOT NULL,
    moderated_date DATETIME,
    FOREIGN KEY (review_id) REFERENCES reviews(id),
    FOREIGN KEY (reporter_id) REFERENCES users(id),
    FOREIGN KEY (moderator_id) REFERENCES users(id),
    INDEX idx_review_id (review_id),
    INDEX idx_status (status)
);
```

주요 필드 설명:
- `id`: 신고의 고유 식별자
- `review_id`: 신고 대상 리뷰 ID (외래 키)
- `reporter_id`: 신고자 ID (외래 키)
- `moderator_id`: 처리한 관리자 ID (외래 키)
- `reason`: 신고 사유
- `status`: 신고 상태 (PENDING, APPROVED, REJECTED, EDIT_REQUESTED)
- `report_date`: 신고 시간
- `moderated_date`: 처리 시간

### 11.4 엔티티 간 관계

리뷰 시스템의 주요 엔티티 간 관계는 다음과 같습니다:

1. **리뷰-레시피 관계**:
   - 다대일(ManyToOne) 관계: 하나의 레시피에 여러 리뷰가 작성될 수 있음
   - `ReviewEntity`에서 `@ManyToOne` 어노테이션으로 `RecipeEntity` 참조

2. **리뷰-사용자 관계**:
   - 다대일(ManyToOne) 관계: 한 사용자가 여러 리뷰를 작성할 수 있음
   - `ReviewEntity`에서 `@ManyToOne` 어노테이션으로 `UserEntity` 참조

3. **리뷰-좋아요 관계**:
   - 일대다(OneToMany) 관계: 하나의 리뷰에 여러 좋아요가 있을 수 있음
   - `ReviewEntity`에서 `@OneToMany` 어노테이션으로 `ReviewLikeEntity` 컬렉션 참조

4. **좋아요-사용자 관계**:
   - 다대일(ManyToOne) 관계: 한 사용자가 여러 리뷰에 좋아요를 누를 수 있음
   - `ReviewLikeEntity`에서 `@ManyToOne` 어노테이션으로 `UserEntity` 참조

5. **신고-리뷰 관계**:
   - 다대일(ManyToOne) 관계: 하나의 리뷰에 여러 신고가 있을 수 있음
   - `ReviewReportEntity`에서 `@ManyToOne` 어노테이션으로 `ReviewEntity` 참조

### 11.5 인덱싱 전략

효율적인 쿼리 성능을 위해 다음과 같은 인덱스를 설정합니다:

1. **리뷰 테이블 인덱스**:
   - `recipe_id`: 특정 레시피의 리뷰 조회 성능 향상
   - `user_id`: 특정 사용자의 리뷰 조회 성능 향상
   - `rating`: 평점 기준 정렬 성능 향상
   - `created_at`: 최신순 정렬 성능 향상

2. **리뷰 좋아요 테이블 인덱스**:
   - `review_id`: 특정 리뷰의 좋아요 조회 성능 향상
   - `user_id`: 특정 사용자의 좋아요 조회 성능 향상
   - 복합 유니크 키 `(review_id, user_id)`: 중복 좋아요 방지

3. **리뷰 신고 테이블 인덱스**:
   - `review_id`: 특정 리뷰의 신고 조회 성능 향상
   - `status`: 상태별 신고 조회 성능 향상

## 12. 다른 시스템과의 통합

리뷰 시스템은 다른 시스템과 긴밀하게 통합되어 있습니다.

### 12.1 레시피 표시 통합

리뷰 시스템은 레시피 표시 시스템과 통합되어 있습니다:

1. **레시피 상세 페이지에 리뷰 섹션 통합**:
   - RecipeController의 `recipeRead` 메소드에서 리뷰 데이터 함께 로드
   - 레시피 상세 페이지(recipe/view.html)에 리뷰 섹션 포함
   - 평균 평점 및 리뷰 수 표시

```java
// RecipeController.java
@GetMapping("/recipe/{id}")
public String recipeRead(@PathVariable Long id, Model model) {
    RecipeEntity recipe = recipeService.readRecipe(id);
    Page<ReviewEntity> reviews = reviewService.getReviewsByRecipePaged(id, "latest", pageable);
    double averageRating = reviewService.calculateAverageRating(id);
    int reviewCount = reviews.getTotalElements();

    model.addAttribute("recipe", recipe);
    model.addAttribute("reviews", reviews);
    model.addAttribute("averageRating", averageRating);
    model.addAttribute("reviewCount", reviewCount);

    return "recipe/view";
}
```

2. **레시피 목록에 평점 정보 표시**:
   - 레시피 목록 페이지에 각 레시피의 평균 평점 표시
   - 평점 기준 정렬 옵션 제공

```java
// RecipeController.java
@GetMapping("/recipes")
public String recipeList(@RequestParam(defaultValue = "latest") String sortBy, Model model) {
    List<RecipeDTO> recipes;

    if ("rating".equals(sortBy)) {
        recipes = recipeService.getRecipesSortedByRating();
    } else {
        recipes = recipeService.getRecipesSortedByDate();
    }

    model.addAttribute("recipes", recipes);
    model.addAttribute("sortBy", sortBy);

    return "recipe/list";
}
```

### 12.2 사용자 프로필 연결

리뷰 시스템은 사용자 프로필 시스템과 연결되어 있습니다:

1. **사용자 프로필에 작성 리뷰 표시**:
   - 사용자 프로필 페이지에 해당 사용자가 작성한 리뷰 목록 표시
   - 리뷰 통계 정보 제공 (총 리뷰 수, 평균 평점 등)

```java
// UserController.java
@GetMapping("/user/{username}/profile")
public String userProfile(@PathVariable String username, Model model) {
    UserEntity user = userService.findByUsername(username);
    List<ReviewEntity> reviews = reviewRepository.findByUserOrderByCreatedAtDesc(user);

    double averageRating = reviews.stream()
            .mapToDouble(ReviewEntity::getRating)
            .average()
            .orElse(0.0);

    model.addAttribute("user", user);
    model.addAttribute("reviews", reviews);
    model.addAttribute("reviewCount", reviews.size());
    model.addAttribute("averageRating", averageRating);

    return "user/profile";
}
```

2. **리뷰 작성자 정보 연결**:
   - 각 리뷰에 작성자 정보 및 프로필 링크 표시
   - 작성자의 리뷰어 레벨 및 배지 표시

### 12.3 검색 및 발견 영향

리뷰 시스템은 검색 및 발견 시스템에 영향을 미칩니다:

1. **평점 기반 검색 결과 정렬**:
   - 검색 결과를 평점 순으로 정렬하는 옵션 제공
   - 높은 평점의 레시피를 검색 결과 상위에 노출

```java
// SearchController.java
@GetMapping("/search")
public String search(@RequestParam String keyword, 
                    @RequestParam(defaultValue = "relevance") String sortBy,
                    Model model) {
    List<RecipeDTO> results;

    if ("rating".equals(sortBy)) {
        results = searchService.searchRecipesSortedByRating(keyword);
    } else {
        results = searchService.searchRecipesSortedByRelevance(keyword);
    }

    model.addAttribute("results", results);
    model.addAttribute("keyword", keyword);
    model.addAttribute("sortBy", sortBy);

    return "search/results";
}
```

2. **리뷰 키워드 기반 검색**:
   - 리뷰 내용에 포함된 키워드로 레시피 검색 가능
   - 리뷰 내용이 검색 결과 관련성에 영향

3. **추천 시스템 통합**:
   - 높은 평점의 레시피를 추천 알고리즘에 반영
   - 사용자의 리뷰 패턴을 기반으로 한 개인화된 추천

### 12.4 알림 시스템 트리거

리뷰 시스템은 알림 시스템과 연동되어 있습니다:

1. **새 리뷰 알림**:
   - 레시피 작성자에게 새 리뷰 작성 시 알림 발송
   - 팔로워에게 팔로우한 사용자의 새 리뷰 알림 발송

```java
// ReviewServiceImpl.java
@Override
public void registerReview(ReviewDTO reviewDTO) {
    // 리뷰 등록 로직

    // 레시피 작성자에게 알림 발송
    RecipeEntity recipe = recipeRepository.findById(reviewDTO.getRecipeId())
            .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

    notificationService.sendNotification(
        recipe.getUser().getUsername(),
        "새로운 리뷰",
        reviewDTO.getUser().getUsername() + "님이 회원님의 레시피에 리뷰를 작성했습니다.",
        "/recipe/" + recipe.getId()
    );
}
```

2. **리뷰 댓글 알림**:
   - 리뷰 작성자에게 댓글 작성 시 알림 발송
   - 댓글 작성자에게 답글 작성 시 알림 발송

3. **좋아요 알림**:
   - 리뷰 작성자에게 좋아요 시 알림 발송
   - 일정 수 이상의 좋아요 달성 시 특별 알림 발송

### 12.5 보고 및 분석 피드

리뷰 시스템은 보고 및 분석 시스템에 데이터를 제공합니다:

1. **리뷰 데이터 분석**:
   - 평점 분포 및 추세 분석
   - 인기 레시피 및 인기 리뷰어 분석
   - 키워드 분석을 통한 사용자 선호도 파악

2. **평점 추세 보고서**:
   - 시간에 따른 평점 변화 추적
   - 카테고리별 평점 비교 분석
   - 개선 필요 레시피 식별

3. **사용자 피드백 인사이트**:
   - 리뷰 내용 분석을 통한 개선점 도출
   - 자주 언급되는 키워드 및 주제 파악
   - 사용자 만족도 측정 및 개선 방향 제시

```java
// AnalyticsService.java (예시)
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {
    private final ReviewRepository reviewRepository;

    @Override
    public Map<String, Object> generateReviewAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        // 평점 분포 분석
        Map<Integer, Long> ratingDistribution = reviewRepository.findAll().stream()
                .collect(Collectors.groupingBy(ReviewEntity::getRating, Collectors.counting()));
        analytics.put("ratingDistribution", ratingDistribution);

        // 월별 평균 평점 추세
        Map<YearMonth, Double> monthlyRatingTrend = reviewRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                    review -> YearMonth.from(review.getCreatedAt()),
                    Collectors.averagingDouble(ReviewEntity::getRating)
                ));
        analytics.put("monthlyRatingTrend", monthlyRatingTrend);

        // 카테고리별 평균 평점
        Map<String, Double> categoryRatings = reviewRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                    review -> review.getRecipe().getCategory(),
                    Collectors.averagingDouble(ReviewEntity::getRating)
                ));
        analytics.put("categoryRatings", categoryRatings);

        return analytics;
    }
}
```

이러한 통합을 통해 리뷰 시스템은 플랫폼 전체에 걸쳐 사용자 경험을 향상시키고, 데이터 기반의 의사 결정을 지원하며, 콘텐츠 품질 향상에 기여합니다.
