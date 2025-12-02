# 프로젝트 컨텍스트: Task Management App (Java)

## 1. 프로젝트 개요
이 프로젝트는 **Java 21**과 **Spring Boot 4.0.0**을 사용하여 구축된 작업 관리 애플리케이션의 백엔드 서비스입니다. 프로젝트와 작업을 관리하기 위한 RESTful API를 제공합니다.

### 주요 기술 스택
*   **언어:** Java 21
*   **프레임워크:** Spring Boot 4.0.0
*   **빌드 도구:** Gradle (Groovy DSL)
*   **데이터베이스:** H2 (개발/테스트용 In-memory)
*   **퍼시스턴스:** Spring Data JPA (Hibernate)
*   **유틸리티:** Lombok

## 2. 시작하기

### 사전 요구사항
*   JDK 21 설치 필요.

### 빌드 및 실행
Gradle Wrapper가 포함되어 있어 별도의 Gradle 설치가 필요하지 않습니다.

*   **프로젝트 빌드:**
    ```bash
    ./gradlew build
    ```
*   **애플리케이션 실행:**
    ```bash
    ./gradlew bootRun
    ```
*   **테스트 실행:**
    ```bash
    ./gradlew test
    ```

## 3. 아키텍처

이 프로젝트는 표준 계층형 아키텍처를 따릅니다:

1.  **Presentation Layer (`controller`)**: HTTP 요청 처리, 유효성 검사, 응답 반환. DTO를 사용합니다.
2.  **Business Layer (`service`)**: 핵심 비즈니스 로직 포함. 트랜잭션 관리가 이곳에서 수행됩니다.
3.  **Data Access Layer (`repository`)**: Spring Data JPA를 사용하여 데이터베이스와 상호작용합니다.
4.  **Domain Layer (`domain`)**: 핵심 데이터 모델을 나타내는 JPA Entity입니다.

### 디렉토리 구조
```
src/main/java/com/streamline/task_management_app_java/
├── controller/       # REST Controllers (예: ProjectController)
│   └── dto/          # Data Transfer Objects (Request/Response 레코드/클래스)
├── service/          # Business Logic (예: ProjectService)
├── repository/       # JPA Repositories
├── domain/           # Entities (Project, Task) & Enums
└── utils/            # 유틸리티 클래스
```

## 4. 개발 컨벤션

### Entity 설계
*   **불변성 지향:** Entity는 `@Getter`와 `@NoArgsConstructor(access = AccessLevel.PROTECTED)`를 사용하여 무분별한 수정을 방지합니다.
*   **비즈니스 메서드:** Public Setter 대신 상태 변경을 위한 구체적인 메서드를 사용합니다 (예: `updateName`, `updateStatus`).

### Controller 설계
*   `@RestController`와 `@RequiredArgsConstructor`를 사용합니다.
*   반환 타입으로 `ResponseEntity<T>`를 사용하여 유연성을 확보합니다.
*   Entity를 직접 노출하지 않고 반드시 DTO를 사용합니다.

## 5. 테스트 작성 지침 (Spring Boot 4.x.x / 3.4+ 표준)

테스트 코드는 **JUnit 5**, **AssertJ**, **Mockito**를 기반으로 작성하며, Spring Boot의 최신 테스트 지원 기능을 활용합니다.

### 5.1. 기본 원칙
*   **명명 규칙:** 클래스명은 `Test`로 끝납니다 (예: `ProjectControllerTest`). 메서드명은 `@DisplayName`을 사용하여 한글로 명확히 의도를 기술합니다.
*   **패턴:** `Given (준비) - When (실행) - Then (검증)` 패턴을 준수하여 가독성을 높입니다.

### 5.2. Controller 테스트 (Slice Test)
웹 계층만 로드하여 빠르게 테스트합니다. Spring Boot 3.4+ 및 4.0부터는 `@MockBean` 대신 **`@MockitoBean`**을 사용하는 것이 표준입니다.

*   **어노테이션:** `@WebMvcTest(TargetController.class)`
*   **의존성 모킹:** `@MockitoBean`을 사용하여 Service 계층을 모킹합니다.
*   **검증:** `MockMvc`를 사용하여 HTTP 요청 및 응답(상태 코드, 본문)을 검증합니다.

```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // Spring Boot 4.0 / 3.4+ 표준
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    @DisplayName("ID로 프로젝트 조회 시 성공하면 200 OK와 데이터를 반환한다")
    @Test
    void getProject_success() throws Exception {
        // Given
        Long id = 1L;
        ProjectResponse response = new ProjectResponse(id, "Test Project", Status.TODO);
        given(projectService.getProject(id)).willReturn(response);

        // When & Then
        mockMvc.perform(get("/project/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Project"));
    }
}
```

### 5.3. Service 테스트 (Unit Test)
Spring 컨텍스트 없이 순수한 자바 코드로 테스트하여 실행 속도를 극대화합니다.

*   **어노테이션:** `@ExtendWith(MockitoExtension.class)`
*   **의존성 주입:** `@Mock`으로 Repository 등을 모킹하고, `@InjectMocks`로 테스트 대상 Service에 주입합니다.

```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @DisplayName("프로젝트 생성 시 저장소가 호출된다")
    @Test
    void createProject_callsRepository() {
        // Given
        ProjectCreateRequest request = new ProjectCreateRequest("New", Status.TODO);

        // When
        projectService.createProject(request);

        // Then
        then(projectRepository).should().save(any(Project.class));
    }
}
```

### 5.4. Repository 테스트 (Data Layer Test)
JPA 컴포넌트만 로드하여 데이터베이스 상호작용을 검증합니다.

*   **어노테이션:** `@DataJpaTest`
*   **특징:** 기본적으로 인메모리 DB(H2)를 사용하며, 테스트 끝난 후 트랜잭션이 롤백됩니다.
*   **검증:** `AssertJ`를 사용하여 저장된 데이터의 상태를 확인합니다.

```java
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void saveAndFind() {
        Project project = new Project("JPA Test", Status.IN_PROGRESS);
        Project saved = projectRepository.save(project);

        assertThat(saved.getId()).isNotNull();
        assertThat(projectRepository.findById(saved.getId())).isPresent();
    }
}
```

## 6. 공통 명령어
*   **코드 품질 확인:** `./gradlew check`
*   **클린 빌드:** `./gradlew clean build`
