# バックエンドテスト戦略

## テストピラミッド

```
        [E2E / Integration]
       /                   \
      [Controller / API Tests]
     /                       \
    [Unit Tests (Service/Domain)]
```

## テスト種別

### 1. Unit テスト（Service / Domain）

- **対象**: `service/`, `domain/`, `exception/`
- **ツール**: MockK + Kotest
- **方針**: 外部依存（DB・HTTP）はすべてモック化。純粋なビジネスロジックのみ検証。
- **命名**: `describe("ClassName") { it("does X when Y") { ... } }`

```kotlin
class CrossroadServiceTest : DescribeSpec({
    val repo = mockk<CrossroadRepository>()
    val service = CrossroadService(repo)

    describe("findById") {
        it("throws ResourceNotFoundException when crossroad does not exist") {
            every { repo.findById(any()) } returns Optional.empty()
            shouldThrow<ResourceNotFoundException> {
                service.findById(UUID.randomUUID())
            }
        }
    }
})
```

### 2. Controller テスト（`@WebMvcTest`）

- **対象**: `controller/`
- **ツール**: MockMvc + MockK
- **方針**: Spring MVC レイヤーのみロード（`@WebMvcTest`）。Service 層はモック化。
  - HTTP ステータス・レスポンス JSON・バリデーション動作を検証。
  - セキュリティ設定（`SecurityConfig`）は `@Import` で明示的に読み込む。
- **認証不要エンドポイント**: `@WithAnonymousUser` または認証なしでアクセス可能なことを確認。

```kotlin
@WebMvcTest(HealthController::class)
@Import(SecurityConfig::class)
class HealthControllerTest {
    @Autowired lateinit var mockMvc: MockMvc

    @Test
    fun `GET v1 health returns ok`() {
        mockMvc.get("/v1/health")
            .andExpect { status { isOk() } }
    }
}
```

### 3. Integration テスト（Testcontainers）

- **対象**: `repository/`, DB マイグレーション
- **ツール**: Testcontainers (PostgreSQL 16 + pgvector)、`@SpringBootTest`
- **方針**: 実際の PostgreSQL コンテナを起動し、Flyway マイグレーション適用済みの状態でリポジトリ操作を検証。
  - `@Transactional` でテスト後にロールバック。
  - `application-test.yml` に `jdbc:tc:postgresql:16:///` URL を使用。

```kotlin
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryTest(
    @Autowired val userRepository: UserRepository
) : FunSpec({
    test("save and find user") {
        val user = User(supabaseUid = UUID.randomUUID(), ageRange = "30s")
        val saved = userRepository.save(user)
        userRepository.findById(saved.id).shouldNotBeNull()
    }
})
```

## テスト設定

### `application-test.yml`

Testcontainers の JDBC URL を使用することで、テスト実行時に PostgreSQL コンテナが自動起動・終了する。

```yaml
spring:
  datasource:
    url: jdbc:tc:postgresql:16:///matamichi_test
    driver-class-name: org.testcontainers.jdbc.ContainerDatabaseDriver
  flyway:
    enabled: true
```

### `@WebMvcTest` の注意点

`@WebMvcTest` は Spring Security を自動適用するため、`SecurityConfig` を必ず `@Import` すること。
インポートしない場合、デフォルトの Basic 認証が有効になりテストが意図せず失敗する。

## カバレッジ目標

| 層 | 目標カバレッジ |
|---|---|
| Service / Domain | 80% 以上 |
| Controller | 全エンドポイントの正常系 + 主要エラー系 |
| Repository | 主要クエリのみ Integration テストで確認 |

## 実行方法

```bash
# 全テスト実行（Testcontainers は Docker が必要）
./gradlew test

# 特定クラスのみ
./gradlew test --tests "com.matamichi.controller.HealthControllerTest"
```
