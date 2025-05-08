# CSBoard 메커니즘 상세 가이드

## 목차
1. [CSBoard 시스템 개요](#1-csboard-시스템-개요)
2. [문의 생성 프로세스](#2-문의-생성-프로세스)
3. [문의 조회 및 관리 메커니즘](#3-문의-조회-및-관리-메커니즘)
4. [답변 시스템](#4-답변-시스템)
5. [권한 관리 및 보안](#5-권한-관리-및-보안)
6. [프론트엔드 구현 상세](#6-프론트엔드-구현-상세)
7. [백엔드 구현 상세](#7-백엔드-구현-상세)
8. [데이터베이스 스키마 및 관계](#8-데이터베이스-스키마-및-관계)
9. [다른 시스템과의 통합](#9-다른-시스템과의-통합)
10. [성능 최적화 및 확장성](#10-성능-최적화-및-확장성)

## 1. CSBoard 시스템 개요

CSBoard(Customer Service Board) 시스템은 사용자와 관리자 간의 소통을 위한 Q&A 게시판 기능을 제공합니다. 이 시스템은 사용자가 질문이나 문의사항을 등록하고, 관리자나 다른 사용자가 이에 답변할 수 있는 플랫폼입니다.

### 1.1 시스템 목적

CSBoard 시스템의 주요 목적은 다음과 같습니다:

- 사용자가 서비스 이용 중 발생하는 질문이나 문제를 등록할 수 있는 공간 제공
- 관리자가 사용자의 문의에 효율적으로 응답할 수 있는 체계 구축
- 자주 묻는 질문과 답변을 공유하여 다른 사용자들에게도 도움 제공
- 사용자 피드백을 수집하고 서비스 개선에 활용

### 1.2 시스템 구성 요소

CSBoard 시스템은 다음과 같은 주요 구성 요소로 이루어져 있습니다:

1. **CSBoardEntity**: 문의 데이터를 저장하는 핵심 데이터 모델
2. **CSBoardReplyEntity**: 문의에 대한 답변 데이터를 저장하는 모델
3. **CSBoardController**: 문의 관련 HTTP 요청을 처리하는 컨트롤러
4. **CSBoardService**: 문의 관련 비즈니스 로직을 처리하는 서비스
5. **CSBoardReplyService**: 답변 관련 비즈니스 로직을 처리하는 서비스
6. **CSBoardRepository**: 문의 데이터에 대한 데이터베이스 액세스를 담당
7. **CSBoardReplyRepository**: 답변 데이터에 대한 데이터베이스 액세스를 담당

### 1.3 시스템 아키텍처

CSBoard 시스템은 Spring Boot 기반의 MVC 아키텍처를 따르고 있으며, 다음과 같은 계층 구조로 구성되어 있습니다:

- **프레젠테이션 계층**: CSBoardController가 HTTP 요청을 처리하고 뷰를 반환
- **비즈니스 계층**: CSBoardService와 CSBoardReplyService가 비즈니스 로직을 처리
- **데이터 액세스 계층**: CSBoardRepository와 CSBoardReplyRepository가 데이터베이스 액세스를 담당
- **데이터 모델**: CSBoardEntity와 CSBoardReplyEntity가 데이터 구조를 정의

## 2. 문의 생성 프로세스

사용자가 새로운 문의를 생성하는 과정은 다음과 같은 단계로 이루어집니다.

### 2.1 문의 작성 폼 접근

1. 사용자가 CSBoard 목록 페이지에서 "새 질문 등록" 버튼을 클릭
2. CSBoardController의 `showRegisterForm` 메소드가 호출됨
3. 메소드는 문의 작성 폼 페이지(register.html)를 반환

```java
// CSBoardController.java
@GetMapping("/register")
public String showRegisterForm() {
    return "csboard/register";
}
```

### 2.2 문의 데이터 입력

사용자는 문의 작성 폼에서 다음과 같은 정보를 입력합니다:

1. **제목(title)**: 문의의 제목
2. **내용(content)**: 문의의 상세 내용

프론트엔드에서는 다음과 같은 유효성 검사를 수행합니다:
- 제목과 내용이 비어있지 않은지 확인 (HTML5의 required 속성 사용)

### 2.3 문의 제출 및 처리

1. 사용자가 폼을 제출하면 CSBoardController의 `createFromForm` 메소드가 호출됨
2. 메소드는 다음과 같은 작업을 수행:
   - 사용자 인증 정보 확인
   - CSBoardDTO 객체에 사용자 이름 설정
   - CSBoardService의 `createQBoard` 메소드 호출

```java
// CSBoardController.java
@PostMapping("/register")
public String createFromForm(@ModelAttribute CSBoardDTO dto, @AuthenticationPrincipal PrincipalDetail principalDetail) {
    // 사용자 인증 확인
    if (principalDetail != null) {
        dto.setWriter(principalDetail.getUsername());
    }

    csBoardService.createQBoard(dto);
    return "redirect:/csboard/list";
}
```

### 2.4 서비스 계층 처리

CSBoardService의 `createQBoard` 메소드는 다음과 같은 작업을 수행합니다:

1. CSBoardDTO를 CSBoardEntity로 변환
2. 필요한 추가 정보 설정 (예: 조회수 초기화)
3. CSBoardRepository를 통해 데이터베이스에 저장
4. 저장된 엔티티를 다시 DTO로 변환하여 반환

```java
// CSBoardServiceImpl.java
@Override
public CSBoardDTO createQBoard(CSBoardDTO dto) {
    CSBoardEntity board = new CSBoardEntity();
    board.setTitle(dto.getTitle());
    board.setWriter(dto.getWriter());
    board.setContent(dto.getContent());
    return toDTO(csBoardRepository.save(board));
}
```

### 2.5 문의 생성 완료

문의 생성이 완료되면 사용자는 CSBoard 목록 페이지로 리다이렉트되며, 새로 작성한 문의를 포함한 모든 문의 목록을 확인할 수 있습니다.

## 3. 문의 조회 및 관리 메커니즘

사용자와 관리자가 문의를 조회하고 관리하는 기능에 대한 상세 내용입니다.

### 3.1 문의 목록 조회

1. 사용자가 CSBoard 목록 페이지에 접근하면 CSBoardController의 `showList` 메소드가 호출됨
2. 메소드는 다음과 같은 작업을 수행:
   - 페이지 번호와 검색 키워드 파라미터 처리
   - Pageable 객체 생성
   - 검색 키워드가 있는 경우 CSBoardService의 `searchCSByTitle` 메소드 호출
   - 검색 키워드가 없는 경우 CSBoardService의 `getCSPage` 메소드 호출
   - 페이징 정보 계산 및 모델에 추가

```java
// CSBoardController.java
@GetMapping("/list")
public String showList(@RequestParam(required = false) String keyword,
                      @RequestParam(defaultValue = "0") int page,
                      Model model) {
    // 페이징 처리
    Pageable pageable = PageRequest.of(page, 10);

    // 검색 또는 전체 조회
    Page<CSBoardDTO> csPage;
    if (keyword != null && !keyword.isEmpty()) {
        csPage = csBoardService.searchCSByTitle(keyword, pageable);
    } else {
        csPage = csBoardService.getCSPage(pageable);
    }

    // 페이징 정보 계산
    int totalPages = csPage.getTotalPages();

    // 모델에 데이터 추가
    model.addAttribute("csPage", csPage);
    model.addAttribute("currentPage", page);
    model.addAttribute("totalPages", totalPages);
    model.addAttribute("keyword", keyword);

    return "csboard/list";
}
```

### 3.2 문의 상세 조회

1. 사용자가 목록에서 특정 문의를 클릭하면 CSBoardController의 `view` 메소드가 호출됨
2. 메소드는 다음과 같은 작업을 수행:
   - CSBoardService의 `getBoard` 메소드를 호출하여 문의 정보 조회 (조회수 증가)
   - CSBoardReplyService의 `getRepliesByCSBoard` 메소드를 호출하여 답변 목록 조회
   - 현재 사용자가 답변을 작성할 수 있는지 확인 (원작성자 또는 관리자)
   - 모델에 데이터 추가

```java
// CSBoardController.java
@GetMapping("/view")
public String view(@RequestParam Long id, Model model, @AuthenticationPrincipal PrincipalDetail principalDetail) {
    // 문의 정보 조회 (조회수 증가)
    CSBoardDTO dto = csBoardService.getBoard(id, true);

    // 답변 목록 조회
    List<CSBoardReplyDTO> replies = csBoardReplyService.getRepliesByCSBoard(id);

    // 답변 권한 확인
    boolean canReply = false;
    if (principalDetail != null) {
        String username = principalDetail.getUsername();
        // 원작성자 또는 관리자인 경우 답변 가능
        canReply = username.equals(dto.getWriter()) || principalDetail.getUser().getRole().equals("ADMIN");
    }

    // 모델에 데이터 추가
    model.addAttribute("cs", dto);
    model.addAttribute("replies", replies);
    model.addAttribute("canReply", canReply);

    return "csboard/view";
}
```

### 3.3 문의 수정

1. 사용자가 문의 상세 페이지에서 "수정" 버튼을 클릭하면 CSBoardController의 `updateForm` 메소드가 호출됨
2. 메소드는 CSBoardService의 `getBoard` 메소드를 호출하여 문의 정보를 조회하고 수정 폼 페이지를 반환
3. 사용자가 수정 폼을 제출하면 CSBoardController의 `updateSubmit` 메소드가 호출됨
4. 메소드는 CSBoardService의 `updateQBoard` 메소드를 호출하여 문의 정보를 업데이트

```java
// CSBoardController.java
@GetMapping("/update")
public String updateForm(@RequestParam Long id, Model model) {
    CSBoardDTO dto = csBoardService.getBoard(id, false);
    model.addAttribute("cs", dto);
    return "csboard/update";
}

@PostMapping("/update/submit")
public String updateSubmit(@ModelAttribute CSBoardDTO dto) {
    csBoardService.updateQBoard(dto.getId(), dto);
    return "redirect:/csboard/view?id=" + dto.getId();
}
```

### 3.4 문의 삭제

1. 사용자가 문의 상세 페이지에서 "삭제" 버튼을 클릭하면 JavaScript 함수 `deleteCS`가 호출됨
2. 함수는 확인 대화상자를 표시하고, 사용자가 확인하면 CSBoardController의 `delete` 메소드를 호출
3. 메소드는 CSBoardService의 `deleteQBoard` 메소드를 호출하여 문의를 삭제

```java
// CSBoardController.java
@DeleteMapping("/delete/{id}")
public String delete(@PathVariable Long id) {
    csBoardService.deleteQBoard(id);
    return "redirect:/csboard/list";
}
```

### 3.5 문의 검색

1. 사용자가 목록 페이지의 검색 폼에 키워드를 입력하고 검색 버튼을 클릭하면 CSBoardController의 `showList` 메소드가 호출됨
2. 메소드는 검색 키워드를 파라미터로 받아 CSBoardService의 `searchCSByTitle` 메소드를 호출
3. 메소드는 제목에 키워드가 포함된 문의를 검색하여 페이징 처리된 결과를 반환

```java
// CSBoardServiceImpl.java
@Override
public Page<CSBoardDTO> searchCSByTitle(String keyword, Pageable pageable) {
    return csBoardRepository
            .findByTitleContaining(keyword, pageable)
            .map(this::toDTO);
}
```

## 4. 답변 시스템

CSBoard 시스템의 핵심 기능 중 하나는 문의에 대한 답변 시스템입니다. 이 섹션에서는 답변 생성, 조회, 관리 등의 메커니즘을 설명합니다.

### 4.1 답변 생성

1. 권한이 있는 사용자(원작성자 또는 관리자)가 문의 상세 페이지의 답변 폼에 내용을 입력하고 제출하면 CSBoardController의 `addReply` 메소드가 호출됨
2. 메소드는 다음과 같은 작업을 수행:
   - 사용자 인증 정보 확인
   - 답변 권한 확인 (원작성자 또는 관리자)
   - CSBoardReplyDTO 객체 생성 및 데이터 설정
   - CSBoardReplyService의 `createReply` 메소드 호출

```java
// CSBoardController.java
@PostMapping("/reply/{id}")
public String addReply(@PathVariable Long id, @RequestParam String content,
                      @AuthenticationPrincipal PrincipalDetail principalDetail,
                      Model model) {
    // 사용자 인증 확인
    if (principalDetail == null) {
        return "redirect:/csboard/view?id=" + id + "&error=unauthorized";
    }

    // 문의 정보 조회
    CSBoardDTO csBoard = csBoardService.getBoard(id, false);

    // 답변 권한 확인
    String username = principalDetail.getUsername();
    boolean isAuthor = username.equals(csBoard.getWriter());
    boolean isAdmin = principalDetail.getUser().getRole().equals("ADMIN");

    if (!isAuthor && !isAdmin) {
        return "redirect:/csboard/view?id=" + id + "&error=unauthorized";
    }

    // 답변 DTO 생성
    CSBoardReplyDTO replyDTO = new CSBoardReplyDTO();
    replyDTO.setCsBoardId(id);
    replyDTO.setContent(content);
    replyDTO.setWriter(username);
    replyDTO.setUser(principalDetail.getUser());

    // 답변 생성
    csBoardReplyService.createReply(replyDTO);

    return "redirect:/csboard/view?id=" + id;
}
```

### 4.2 답변 서비스 처리

CSBoardReplyService의 `createReply` 메소드는 다음과 같은 작업을 수행합니다:

1. CSBoardReplyDTO를 CSBoardReplyEntity로 변환 (dtoToEntity 메소드 사용)
2. CSBoardReplyRepository를 통해 데이터베이스에 저장
3. 저장된 엔티티를 다시 DTO로 변환하여 반환

```java
// CSBoardReplyServiceImpl.java
@Override
@Transactional
public CSBoardReplyDTO createReply(CSBoardReplyDTO replyDTO) {
    CSBoardReplyEntity entity = dtoToEntity(replyDTO);
    CSBoardReplyEntity savedEntity = replyRepository.save(entity);
    return entityToDto(savedEntity);
}
```

### 4.3 답변 조회

1. 사용자가 문의 상세 페이지에 접근하면 CSBoardController의 `view` 메소드가 호출됨
2. 메소드는 CSBoardReplyService의 `getRepliesByCSBoard` 메소드를 호출하여 해당 문의에 대한 모든 답변을 조회
3. 조회된 답변 목록은 모델에 추가되어 뷰에 전달됨

```java
// CSBoardReplyServiceImpl.java
@Override
@Transactional(readOnly = true)
public List<CSBoardReplyDTO> getRepliesByCSBoard(Long csBoardId) {
    CSBoardEntity csBoard = csBoardRepository.findById(csBoardId)
            .orElseThrow(() -> new RuntimeException("CSBoard not found with id: " + csBoardId));

    List<CSBoardReplyEntity> replies = replyRepository.findByCsBoardOrderByRegDateDesc(csBoard);

    return replies.stream()
            .map(this::entityToDto)
            .collect(Collectors.toList());
}
```

### 4.4 답변 삭제

1. 권한이 있는 사용자가 답변 옆의 "삭제" 버튼을 클릭하면 JavaScript 함수 `deleteReply`가 호출됨
2. 함수는 확인 대화상자를 표시하고, 사용자가 확인하면 CSBoardController의 `deleteReply` 메소드를 호출
3. 메소드는 다음과 같은 작업을 수행:
   - 사용자 인증 정보 확인
   - 답변 삭제 권한 확인 (답변 작성자 또는 관리자)
   - CSBoardReplyService의 `deleteReply` 메소드 호출

```java
// CSBoardController.java
@DeleteMapping("/reply/delete/{id}")
public String deleteReply(@PathVariable Long id, @RequestParam Long csBoardId,
                         @AuthenticationPrincipal PrincipalDetail principalDetail) {
    // 권한 확인 로직 생략...

    // 답변 삭제
    csBoardReplyService.deleteReply(id);

    return "redirect:/csboard/view?id=" + csBoardId;
}
```

## 5. 권한 관리 및 보안

CSBoard 시스템은 다양한 권한 관리 및 보안 메커니즘을 통해 안전한 문의 및 답변 관리를 보장합니다.

### 5.1 사용자 인증

1. Spring Security를 사용하여 사용자 인증 처리
2. 인증된 사용자 정보는 PrincipalDetail 객체를 통해 컨트롤러에서 접근 가능
3. 인증되지 않은 사용자는 로그인 페이지로 리다이렉트

```java
// CustomSecurityConfig.java (일부)
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // ... 다른 설정들 ...
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/login", "/userinfo/**", "/recipe/**", "/csboard/**", "/notice/**", "/review/**")
            .permitAll()
            .anyRequest().authenticated()
        );

    return http.build();
}
```

### 5.2 권한 기반 접근 제어

1. 문의 수정 및 삭제: 원작성자만 가능
2. 답변 작성: 원작성자 또는 관리자만 가능
3. 답변 삭제: 답변 작성자 또는 관리자만 가능

이러한 권한 검사는 컨트롤러 메소드에서 수행됩니다:

```java
// 예: 답변 작성 권한 확인
String username = principalDetail.getUsername();
boolean isAuthor = username.equals(csBoard.getWriter());
boolean isAdmin = principalDetail.getUser().getRole().equals("ADMIN");

if (!isAuthor && !isAdmin) {
    return "redirect:/csboard/view?id=" + id + "&error=unauthorized";
}
```

### 5.3 CSRF 보호

1. Spring Security의 CSRF 보호 기능 활성화
2. 모든 POST, PUT, DELETE 요청에 CSRF 토큰 포함
3. Thymeleaf 템플릿에서 자동으로 CSRF 토큰 처리

```html
<!-- 예: 폼에 자동으로 CSRF 토큰 포함 -->
<form th:action="@{/csboard/register}" method="post">
    <!-- CSRF 토큰은 Thymeleaf에 의해 자동으로 추가됨 -->
    <!-- 폼 필드들... -->
</form>
```

### 5.4 입력 데이터 검증

1. 서버 측에서 입력 데이터 유효성 검사
2. HTML 이스케이프 처리로 XSS 공격 방지
3. 데이터베이스 쿼리 파라미터화로 SQL 인젝션 방지

```java
// 예: 입력 데이터 검증
if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
    throw new IllegalArgumentException("Title cannot be empty");
}

if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
    throw new IllegalArgumentException("Content cannot be empty");
}
```

## 6. 프론트엔드 구현 상세

CSBoard 시스템의 프론트엔드 구현에 대한 상세 내용입니다.

### 6.1 사용자 인터페이스 구성

CSBoard 시스템의 사용자 인터페이스는 다음과 같은 페이지로 구성됩니다:

1. **목록 페이지(list.html)**: 모든 문의 목록을 표시하고 검색 및 페이징 기능 제공
2. **상세 페이지(view.html)**: 특정 문의의 상세 내용과 답변을 표시하고 답변 작성 폼 제공
3. **등록 페이지(register.html)**: 새로운 문의를 작성하는 폼 제공
4. **수정 페이지(update.html)**: 기존 문의를 수정하는 폼 제공

### 6.2 반응형 디자인

1. Bootstrap 프레임워크를 사용한 반응형 디자인
2. 모바일 및 데스크톱 환경에서 최적화된 레이아웃
3. 다양한 화면 크기에 맞게 조정되는 UI 요소

```html
<!-- 예: 반응형 카드 컴포넌트 -->
<div class="card mx-auto shadow-lg border-0 rounded-4 p-5 bg-white" style="max-width: 760px;">
    <!-- 카드 내용 -->
</div>
```

### 6.3 사용자 경험 향상

1. 직관적인 네비게이션 및 버튼 배치
2. 적절한 아이콘 및 시각적 요소 사용
3. 폼 입력 필드의 유효성 검사 및 피드백
4. 확인 대화상자를 통한 중요 작업 확인

```javascript
// 예: 삭제 확인 대화상자
function deleteCS() {
    const id = document.getElementById("csId").value;
    if (confirm("정말 삭제하시겠습니까?")) {
        fetch("/csboard/delete/" + id, {
            method: "DELETE"
        }).then(res => {
            if (res.ok) {
                alert("삭제되었습니다.");
                window.location.href = "/csboard/list";
            } else {
                alert("삭제 실패 😥");
            }
        });
    }
}
```

### 6.4 페이징 및 검색 UI

1. 페이지 번호 및 이전/다음 버튼을 포함한 페이징 컨트롤
2. 현재 페이지 강조 표시
3. 검색 폼 및 결과 표시
4. 검색 키워드 유지 및 페이지 간 전달

```html
<!-- 예: 페이징 컨트롤 -->
<nav th:if="${totalPages > 0}" class="mt-4">
    <ul class="pagination justify-content-center">
        <!-- 이전 버튼 -->
        <li class="page-item rounded-pill overflow-hidden me-1" th:classappend="${currentPage == 0} ? 'disabled'">
            <a class="page-link border-0 fw-semibold"
               th:href="@{/csboard/list(page=${currentPage - 1}, keyword=${keyword})}">이전</a>
        </li>

        <!-- 페이지 번호 -->
        <li class="page-item rounded-pill overflow-hidden mx-1"
            th:each="i : ${#numbers.sequence(0, totalPages - 1)}"
            th:classappend="${i == currentPage} ? 'active'">
            <a class="page-link border-0 fw-semibold"
               th:href="@{/csboard/list(page=${i}, keyword=${keyword})}"
               th:text="${i + 1}">1</a>
        </li>

        <!-- 다음 버튼 -->
        <li class="page-item rounded-pill overflow-hidden ms-1" th:classappend="${currentPage + 1 >= totalPages} ? 'disabled'">
            <a class="page-link border-0 fw-semibold"
               th:href="@{/csboard/list(page=${currentPage + 1}, keyword=${keyword})}">다음</a>
        </li>
    </ul>
</nav>
```

## 7. 백엔드 구현 상세

CSBoard 시스템의 백엔드 구현에 대한 상세 내용입니다.

### 7.1 엔티티 구조

#### 7.1.1 CSBoardEntity

CSBoardEntity는 문의 데이터를 저장하는 핵심 모델입니다:

```java
@Getter
@Setter
@Entity(name="cs_board")
public class CSBoardEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String writer;
    @Column(length = 5000)
    private String content;
    private Long hitcount;
    private boolean secret;

    @CreationTimestamp
    @Column(name = "regdate", updatable = false)
    @DateTimeFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public void change(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void updateHitcount() {
        this.hitcount = (this.hitcount == null ? 0 : this.hitcount) + 1;
    }
}
```

주요 필드 설명:
- `id`: 문의의 고유 식별자
- `title`: 문의 제목
- `writer`: 작성자 이름
- `content`: 문의 내용 (최대 5000자)
- `hitcount`: 조회수
- `secret`: 비밀글 여부
- `createdAt`: 작성 시간

#### 7.1.2 CSBoardReplyEntity

CSBoardReplyEntity는 문의에 대한 답변 데이터를 저장하는 모델입니다:

```java
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="csboard_reply")
public class CSBoardReplyEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "csboard_id")
    private CSBoardEntity csBoard;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private String writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
```

주요 필드 설명:
- `id`: 답변의 고유 식별자
- `csBoard`: 답변이 속한 문의 (다대일 관계)
- `content`: 답변 내용 (최대 1000자)
- `writer`: 답변 작성자 이름
- `user`: 답변 작성자 (다대일 관계)

### 7.2 DTO 구조

#### 7.2.1 CSBoardDTO

CSBoardDTO는 문의 데이터를 전송하기 위한 객체입니다:

```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CSBoardDTO {
    private Long id;
    private String title;
    private String writer;
    private String content;
    private LocalDateTime createdAt;
    private Long hitcount;
    private boolean secret;
}
```

#### 7.2.2 CSBoardReplyDTO

CSBoardReplyDTO는 답변 데이터를 전송하기 위한 객체입니다:

```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CSBoardReplyDTO {
    private Long id;
    private Long csBoardId;
    private String content;
    private String writer;
    private UserEntity user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### 7.3 서비스 계층 구현

#### 7.3.1 CSBoardService

CSBoardService는 문의 관련 비즈니스 로직을 처리하는 인터페이스입니다:

```java
public interface CSBoardService {
    List<CSBoardDTO> getAllQBoards();
    CSBoardDTO getBoard(Long id);
    CSBoardDTO createQBoard(CSBoardDTO dto);
    void updateQBoard(Long id, CSBoardDTO dto);
    void deleteQBoard(Long id);
    CSBoardDTO getBoard(Long id, boolean increaseHitcount);

    // 페이징 추가
    Page<CSBoardDTO> getCSPage(Pageable pageable);
    Page<CSBoardDTO> searchCSByTitle(String keyword, Pageable pageable);
}
```

#### 7.3.2 CSBoardServiceImpl

CSBoardServiceImpl은 CSBoardService 인터페이스의 구현체입니다:

```java
@Service
public class CSBoardServiceImpl implements CSBoardService {

    @Autowired
    private CSBoardRepository csBoardRepository;

    @Override
    public List<CSBoardDTO> getAllQBoards() {
        return csBoardRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<CSBoardDTO> getCSPage(Pageable pageable) {
        return csBoardRepository.findAll(pageable)
                .map(this::toDTO);
    }

    @Override
    public Page<CSBoardDTO> searchCSByTitle(String keyword, Pageable pageable) {
        return csBoardRepository
                .findByTitleContaining(keyword, pageable)
                .map(this::toDTO);
    }

    @Override
    public CSBoardDTO getBoard(Long id) {
        return getBoard(id, true);
    }

    @Override
    public CSBoardDTO getBoard(Long id, boolean increaseHitcount) {
        CSBoardEntity entity = csBoardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("csBoard not found with id: " + id));

        if (increaseHitcount) {
            entity.updateHitcount();
            csBoardRepository.save(entity);
        }

        return toDTO(entity);
    }

    @Override
    public CSBoardDTO createQBoard(CSBoardDTO dto) {
        CSBoardEntity board = new CSBoardEntity();
        board.setTitle(dto.getTitle());
        board.setWriter(dto.getWriter());
        board.setContent(dto.getContent());
        return toDTO(csBoardRepository.save(board));
    }

    @Override
    public void updateQBoard(Long id, CSBoardDTO dto) {
        CSBoardEntity board = csBoardRepository.findById(id).orElseThrow();
        board.change(dto.getTitle(), dto.getContent());
        csBoardRepository.save(board);
    }

    @Override
    public void deleteQBoard(Long id) {
        csBoardRepository.deleteById(id);
    }

    private CSBoardDTO toDTO(CSBoardEntity board) {
        return CSBoardDTO.builder()
                .id(board.getId())
                .title(board.getTitle())
                .writer(board.getWriter())
                .content(board.getContent())
                .createdAt(board.getCreatedAt())
                .hitcount(board.getHitcount())
                .build();
    }
}
```

#### 7.3.3 CSBoardReplyService

CSBoardReplyService는 답변 관련 비즈니스 로직을 처리하는 인터페이스입니다:

```java
public interface CSBoardReplyService {
    // Create a new reply
    CSBoardReplyDTO createReply(CSBoardReplyDTO replyDTO);

    // Get all replies for a csboard post
    List<CSBoardReplyDTO> getRepliesByCSBoard(Long csBoardId);

    // Update a reply
    CSBoardReplyDTO updateReply(Long id, CSBoardReplyDTO replyDTO);

    // Delete a reply
    void deleteReply(Long id);

    // Convert entity to DTO
    CSBoardReplyDTO entityToDto(CSBoardReplyEntity entity);

    // Convert DTO to entity
    CSBoardReplyEntity dtoToEntity(CSBoardReplyDTO dto);
}
```

### 7.4 컨트롤러 구현

CSBoardController는 문의 및 답변 관련 HTTP 요청을 처리하는 컨트롤러입니다:

```java
@Controller
@RequestMapping("/csboard")
@Log4j2
public class CSBoardController {

    @Autowired
    private CSBoardService csBoardService;

    @Autowired
    private CSBoardReplyService csBoardReplyService;

    // 문의 등록 폼 표시
    @GetMapping("/register")
    public String showRegisterForm() {
        return "csboard/register";
    }

    // 문의 등록 처리
    @PostMapping("/register")
    public String createFromForm(@ModelAttribute CSBoardDTO dto, @AuthenticationPrincipal PrincipalDetail principalDetail) {
        if (principalDetail != null) {
            dto.setWriter(principalDetail.getUsername());
        }

        csBoardService.createQBoard(dto);
        return "redirect:/csboard/list";
    }

    // 문의 목록 표시
    @GetMapping("/list")
    public String showList(@RequestParam(required = false) String keyword,
                          @RequestParam(defaultValue = "0") int page,
                          Model model) {
        // 페이징 처리
        Pageable pageable = PageRequest.of(page, 10);

        // 검색 또는 전체 조회
        Page<CSBoardDTO> csPage;
        if (keyword != null && !keyword.isEmpty()) {
            csPage = csBoardService.searchCSByTitle(keyword, pageable);
        } else {
            csPage = csBoardService.getCSPage(pageable);
        }

        // 페이징 정보 계산
        int totalPages = csPage.getTotalPages();

        // 모델에 데이터 추가
        model.addAttribute("csPage", csPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("keyword", keyword);

        return "csboard/list";
    }

    // 문의 상세 조회
    @GetMapping("/view")
    public String view(@RequestParam Long id, Model model, @AuthenticationPrincipal PrincipalDetail principalDetail) {
        // 문의 정보 조회 (조회수 증가)
        CSBoardDTO dto = csBoardService.getBoard(id, true);

        // 답변 목록 조회
        List<CSBoardReplyDTO> replies = csBoardReplyService.getRepliesByCSBoard(id);

        // 답변 권한 확인
        boolean canReply = false;
        if (principalDetail != null) {
            String username = principalDetail.getUsername();
            // 원작성자 또는 관리자인 경우 답변 가능
            canReply = username.equals(dto.getWriter()) || principalDetail.getUser().getRole().equals("ADMIN");
        }

        // 모델에 데이터 추가
        model.addAttribute("cs", dto);
        model.addAttribute("replies", replies);
        model.addAttribute("canReply", canReply);

        return "csboard/view";
    }

    // 기타 메소드 생략...
}
```

## 8. 데이터베이스 스키마 및 관계

CSBoard 시스템의 데이터베이스 스키마와 엔티티 간 관계에 대한 상세 내용입니다.

### 8.1 테이블 구조

#### 8.1.1 cs_board 테이블

cs_board 테이블은 문의 데이터를 저장합니다:

```sql
CREATE TABLE cs_board (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    writer VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    hitcount BIGINT,
    secret BOOLEAN DEFAULT FALSE,
    regdate DATETIME NOT NULL
);
```

#### 8.1.2 csboard_reply 테이블

csboard_reply 테이블은 문의에 대한 답변 데이터를 저장합니다:

```sql
CREATE TABLE csboard_reply (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    csboard_id BIGINT NOT NULL,
    content VARCHAR(1000) NOT NULL,
    writer VARCHAR(255) NOT NULL,
    user_id BIGINT,
    reg_date DATETIME NOT NULL,
    update_date DATETIME,
    FOREIGN KEY (csboard_id) REFERENCES cs_board(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### 8.2 엔티티 관계

CSBoard 시스템의 주요 엔티티 간 관계는 다음과 같습니다:

1. **CSBoardEntity와 CSBoardReplyEntity의 관계**:
   - 일대다(OneToMany) 관계: 하나의 문의에 여러 답변이 있을 수 있음
   - CSBoardReplyEntity에서 `@ManyToOne` 어노테이션으로 CSBoardEntity 참조

2. **CSBoardReplyEntity와 UserEntity의 관계**:
   - 다대일(ManyToOne) 관계: 한 사용자가 여러 답변을 작성할 수 있음
   - CSBoardReplyEntity에서 `@ManyToOne` 어노테이션으로 UserEntity 참조

### 8.3 인덱싱 전략

효율적인 쿼리 성능을 위해 다음과 같은 인덱스를 설정합니다:

1. **cs_board 테이블 인덱스**:
   - `title`: 제목 기반 검색 성능 향상
   - `writer`: 작성자 기반 검색 성능 향상
   - `regdate`: 날짜 기준 정렬 성능 향상

2. **csboard_reply 테이블 인덱스**:
   - `csboard_id`: 특정 문의의 답변 조회 성능 향상
   - `user_id`: 특정 사용자의 답변 조회 성능 향상
   - `reg_date`: 날짜 기준 정렬 성능 향상

## 9. 다른 시스템과의 통합

CSBoard 시스템은 다른 시스템과 통합되어 전체 애플리케이션의 일부로 작동합니다.

### 9.1 사용자 관리 시스템과의 통합

CSBoard 시스템은 사용자 관리 시스템과 통합되어 있습니다:

1. **사용자 인증 및 권한 확인**:
   - Spring Security를 통한 사용자 인증
   - PrincipalDetail 객체를 통한 사용자 정보 접근
   - 사용자 역할(Role)에 따른 권한 관리

2. **사용자 프로필 연동**:
   - 문의 및 답변 작성 시 현재 로그인한 사용자 정보 사용
   - 사용자 프로필 페이지에서 작성한 문의 및 답변 목록 표시

### 9.2 알림 시스템과의 통합

CSBoard 시스템은 알림 시스템과 통합될 수 있습니다:

1. **새 답변 알림**:
   - 문의 작성자에게 새 답변 작성 시 알림 발송
   - 관리자에게 새 문의 등록 시 알림 발송

2. **상태 변경 알림**:
   - 문의 처리 상태 변경 시 작성자에게 알림 발송
   - 답변 수정 시 관련 사용자에게 알림 발송

### 9.3 관리자 대시보드와의 통합

CSBoard 시스템은 관리자 대시보드와 통합될 수 있습니다:

1. **문의 통계 제공**:
   - 기간별 문의 수 통계
   - 카테고리별 문의 분포
   - 미답변 문의 수 및 비율

2. **관리자 작업 도구**:
   - 일괄 답변 처리 기능
   - 문의 카테고리 관리
   - 자주 묻는 질문(FAQ) 관리

### 9.4 검색 시스템과의 통합

CSBoard 시스템은 검색 시스템과 통합될 수 있습니다:

1. **통합 검색 지원**:
   - 사이트 전체 검색에 문의 및 답변 포함
   - 검색 결과에 문의 제목 및 내용 표시

2. **고급 검색 기능**:
   - 날짜 범위 검색
   - 작성자 기반 검색
   - 답변 상태 기반 검색

## 10. 성능 최적화 및 확장성

CSBoard 시스템의 성능 최적화 및 확장성에 대한 상세 내용입니다.

### 10.1 페이징 및 캐싱

1. **페이징 처리**:
   - Spring Data JPA의 Pageable 인터페이스를 사용한 효율적인 페이징
   - 한 번에 로드되는 데이터 양 제한으로 메모리 사용량 최적화

2. **캐싱 전략**:
   - 자주 조회되는 문의 목록 캐싱
   - 통계 데이터 캐싱
   - 캐시 무효화 전략 구현

### 10.2 데이터베이스 최적화

1. **인덱스 최적화**:
   - 자주 사용되는 검색 조건에 인덱스 설정
   - 복합 인덱스를 통한 쿼리 성능 향상

2. **쿼리 최적화**:
   - N+1 문제 해결을 위한 조인 페치(Join Fetch) 사용
   - 페이징 쿼리 최적화
   - 불필요한 데이터 로딩 방지

### 10.3 확장성 고려사항

1. **수평적 확장**:
   - 마이크로서비스 아키텍처로의 전환 가능성
   - 서비스 분리를 통한 독립적 확장

2. **수직적 확장**:
   - 리소스 사용량 모니터링
   - 병목 지점 식별 및 최적화
   - 하드웨어 리소스 증설 계획

### 10.4 성능 모니터링

1. **성능 지표 수집**:
   - 응답 시간 모니터링
   - 데이터베이스 쿼리 성능 모니터링
   - 사용자 경험 지표 수집

2. **로깅 및 분석**:
   - 오류 로깅 및 알림
   - 성능 병목 현상 분석
   - 사용 패턴 분석을 통한 최적화 방향 도출

### 10.5 부하 테스트

1. **시나리오 기반 테스트**:
   - 일반적인 사용 패턴 시뮬레이션
   - 피크 시간대 부하 시뮬레이션
   - 극단적 상황 테스트

2. **성능 개선 사이클**:
   - 테스트 결과 분석
   - 병목 지점 식별 및 개선
   - 개선 효과 검증을 위한 재테스트
