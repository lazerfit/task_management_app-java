---
apply: always
---

# Gemini - 테스트 코드 작성 지침

이 문서는 프로젝트의 테스트 코드를 작성할 때 일관성과 품질을 유지하기 위한 지침을 제공합니다.

## 1. 기본 원칙

- **테스트 파일 위치**: 소스 코드와 동일한 패키지 구조를 `src/test/java` 디렉토리 내에 유지합니다.
  - 예: `src/main/java/com/steamline/service/MyService.java` -> `src/test/java/com/steamline/service/MyServiceTest.java`
- **테스트 파일명**: 테스트 대상 클래스 이름 뒤에 `Test`를 접미사로 붙입니다.
  - 예: `ProjectController` -> `ProjectControllerTest`
- **테스트 메서드명**: `@DisplayName` 어노테이션을 사용하여 테스트 목적을 한글로 명확하게 설명합니다.
  - 예: `@DisplayName("ID로 프로젝트를 조회하면, 프로젝트 정보를 반환한다.")`
- **Given-When-Then 패턴**: 테스트 코드는 `Given(준비) - When(실행) - Then(검증)` 구조를 따릅니다. 이를 통해 테스트의 가독성과 유지보수성을 높입니다.

```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("ID로 프로젝트를 조회하면, 프로젝트 정보를 반환한다.")
@Test
void getProject_withValidId_returnsProjectResponse() throws Exception {
    // Given - 테스트 준비
    Long projectId = 1L;
    ProjectResponse projectResponse = new ProjectResponse(/* ... */);
    given(projectService.getProject(projectId)).willReturn(projectResponse);

    // When & Then - 실행 및 검증
    mockMvc.perform(get("/project/{id}", projectId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(projectId));
}
```

## 2. 테스트 유형별 작성법

프로젝트의 각 계층에 맞는 테스트 전략을 사용합니다.

### 2.1. Controller (Web Layer) 테스트

- **어노테이션**: `@WebMvcTest(ControllerName.class)`를 사용하여 웹 계층 관련 빈만 로드합니다. 이를 통해 테스트 속도를 높이고 Controller의 책임에만 집중할 수 있습니다.
- **의존성 모킹**: Controller가 의존하는 서비스(`Service`)는 `@MockitoBean`을 사용하여 가짜(Mock) 객체로 만듭니다. (구 `@MockBean` 대체)
- **요청 및 검증**: `MockMvc`를 사용하여 HTTP 요청을 시뮬레이션하고, 응답 상태, 헤더, 본문을 검증합니다.
- **참고 파일**: `src/test/java/com/steamline/task_management_app_java/controller/ProjectControllerTest.java`

```java
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // Spring Boot 3.4+ / 4.x 권장
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
// ... imports

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean // Service는 실제 로직을 수행하지 않도록 Mock 객체로 대체
    private ProjectService projectService;

    // ... 테스트 코드
}
```

### 2.2. Service (Unit) 테스트

- **어노테이션**: Spring 컨텍스트를 로드하지 않는 순수 JUnit 테스트로 작성합니다. `@ExtendWith(MockitoExtension.class)`를 사용합니다.
- **의존성 모킹**: 서비스가 의존하는 `Repository`는 `@Mock` 어노테이션으로 가짜 객체를 만듭니다.
- **테스트 대상 주입**: `@InjectMocks`를 사용하여 `@Mock`으로 만든 가짜 객체들을 테스트 대상 서비스에 주입합니다.
- **검증**: `BDDMockito`의 `given()`으로 Mock 객체의 행동을 정의하고, `then()`으로 특정 메서드가 호출되었는지 검증합니다. `Assertions` (AssertJ)를 사용하여 반환 값이나 객체의 상태를 검증합니다.

```java
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

// 예시: ProjectServiceTest
@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @DisplayName("프로젝트 생성 요청 시, save가 1회 호출된다.")
    @Test
    void createProject_whenGivenRequest_thenSavesProject() {
        // Given
        ProjectCreateRequest request = new ProjectCreateRequest("New Project", Status.TODO);
        Project project = request.toEntity(); // toEntity 호출 방식 수정 (일반적으로 인자 불필요)
        given(projectRepository.save(any(Project.class))).willReturn(project);

        // When
        projectService.createProject(request);

        // Then
        then(projectRepository).should().save(any(Project.class));
    }
}
```

### 2.3. Repository (Data Layer) 테스트

- **어노테이션**: `@DataJpaTest`를 사용하여 JPA 관련 설정만 로드합니다. 이 어노테이션은 기본적으로 내장 데이터베이스(H2)를 사용하고, 각 테스트가 끝난 후 트랜잭션을 롤백하여 테스트 간 독립성을 보장합니다.
- **검증**: 실제 데이터베이스와 상호작용하는 것을 테스트합니다. 데이터를 저장(`save`)한 후, 올바르게 조회(`findById`, `findAll` 등)되는지, 또는 사용자 정의 쿼리가 정확히 동작하는지 검증합니다.

```java
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;

// 예시: ProjectRepositoryTest
@DataJpaTest
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @DisplayName("프로젝트를 저장하고 ID로 조회하면, 동일한 프로젝트가 반환된다.")
    @Test
    void saveAndFindById_whenSaved_thenReturnsSameProject() {
        // Given
        Project project = new Project("Test Project", Status.TODO);

        // When
        Project savedProject = projectRepository.save(project);
        Optional<Project> foundProject = projectRepository.findById(savedProject.getId());

        // Then
        assertThat(foundProject).isPresent();
        assertThat(foundProject.get().getName()).isEqualTo("Test Project");
    }
}
```
