# BiliApplication 架构审计与优化建议

> 审计日期：本报告基于当前仓库（`BiliApplication`，AGP 8.13.2 / Gradle 9.4.1 / Kotlin 2.1.0 / Hilt 2.57.1）源码静态阅读生成。
> 范围：`app`、`core`、`features:designsystem`、`features:home`、`features:login` 五个模块的构建配置与源码。
> 原则：**本文档只做分析、给出建议，不修改任何代码。**

---

## 0. 结论速览（TL;DR）

| 问题 | 结论 |
| --- | --- |
| **「网络库 → Repository → UseCase」这个封装可以吗？** | **方向正确**（这是标准 Clean Architecture 数据流），但当前**实现走样**：Repository 按接口 1:1 建、UseCase 全部纯透传、真正有逻辑的 QR 状态机却写在 ViewModel、网络模型反向依赖领域模型、PagingSource 绕过 Repository。详见 §2 |
| 项目整体结构 | 有分层意识（app/core/features 拆分 + Hilt + UDF），方向对；但存在**两套 package 树并存**、**空壳模块**、**旧时代 support 库残留**三类硬伤 |
| P0（影响功能/构建） | ① 登录成功后无处可跳转（`onLoginSuccess = {}` 空实现 + home 未接线）② 4 个模块的 `testInstrumentationRunner` 指向不存在的 support 库类 ③ support 库（appcompat-v7 / support runner / support espresso）与 androidx 混用 + Jetifier ④ 密码登录按钮空实现 ⑤ 疑似缺失 `paging-runtime` 依赖 |
| P1（影响架构） | 双 package 树、core 职责过重、designsystem 空模块、网络层依赖领域层、拦截器 `runBlocking` 读 DataStore、passport Retrofit 未挂 OkHttpClient、BODY 全量日志、Room 双通道编译、PagingSource 绕过仓储 |
| P2（影响质量） | `BlBl` 拼写错误、错误消息不一致、UiState 泄漏 domain 模型与 Bitmap、死字段、主线程生成二维码、硬编码品牌色、无 IME 适配、无表单校验等 |
| **Login 界面优化** | 详见 §7：设计 token 化、输入体验、状态反馈、性能、可测试性五个维度给出完整方案 |

---

## 1. 项目现状概览

### 1.1 模块与依赖方向

```
:app ──────────────→ :core
  │                  :features:login
  └──（未依赖 :features:home / :features:designsystem）

:features:login ───→ :core
:features:home      （空壳：4 个占位 class，无任何实现）
:features:designsystem（空模块：只有 build.gradle 与测试占位，无主题/组件代码）
```

**发现 1-1**：`features:home` 在 `settings.gradle.kts` 中已 include，但 `app/build.gradle.kts` 的 dependencies 只有 `:core` 和 `:features:login`，home 模块**从未被引用**，里面 4 个文件（`BiliHomeViewModel/UiScreen/UiState/UiEvent`）全是 `class X {}` 空壳。

**发现 1-2**：`features:designsystem` 是空模块（无任何 `src/main/java` 代码）；而 `app` 自己维护了一份 `ui/theme/`（Color.kt/Theme.kt/Type.kt），login 界面又直接硬编码品牌色 `Color(0xFFFB7299)`。**主题三处分散**：designsystem（空）、app/theme、login 硬编码。

### 1.2 已存在但未接线的代码

core 里写好了 10 个 Repository、12 个 UseCase、2 个 PagingSource、二维码登录全链路——但 app 的 NavHost 只注册了 `login()`，`DiscoverRoute / HomeRoute / LibraryRoute` 都定义了却没有任何页面注册。**代码量远超可用功能量**，属于"分层先行、页面未跟上"的半成品状态。

---

## 2. 核心问题回答：网络库 → Repository → UseCase 是否可以

### 2.1 结论

**方向正确，值得保留；但"现在这么写"有 6 个具体问题，需要重构。**

「Retrofit ApiService → Repository（接口）→ UseCase → ViewModel」是 Clean Architecture 的标准数据流，Google 官方示例（Now in Android）也采用该结构。问题不在"要不要用"，而在**每个环节是否真的承担了职责**。

### 2.2 当前实现的 6 个问题

#### 问题 1：Repository 按接口 1:1 建立，没有聚合，是"换皮"而不是抽象

现有 10 个 Repository 与 10 个 API 端点一一对应（`BiliGetUserInfoRepository`、`BiliLikeVideoRepository`、`BlBlQrCodeDataRepository`……），每个 Repository 只包一个接口调用 + `try/catch` + `toDomain()`。

Repository 模式的价值在于：**屏蔽数据来源、聚合多个来源、做缓存、统一错误处理**。1:1 包装既没有屏蔽（上层还是面对 endpoint 语义），也没有缓存，纯粹多了一层。

> 建议：按**业务领域**聚合为 3~4 个仓储：
> - `AuthRepository`（二维码生成、轮询、会话保存/清除）
> - `UserRepository`（用户信息）
> - `VideoRepository`（详情、播放地址、点赞、评论、热门、推荐列表 + 分页流）
> - （可选）`CommentRepository`（回复列表）

#### 问题 2：UseCase 全部纯透传，而真正的业务逻辑却在 ViewModel

12 个 UseCase 中绝大多数是这种形态（`BiliGetUserInfoUseCase.kt`）：

```kotlin
class BiliGetUserInfoUseCase @Inject constructor(
    private val biliGetUserInfoRepository: BiliGetUserInfoRepository
) {
    suspend operator fun invoke(cookie: String): Result<UserInfoDomain> =
        biliGetUserInfoRepository.getUserInfo(cookie)   // 纯透传
}
```

而**唯一有业务规则的部分**——二维码轮询状态机（`code == 0` 成功、`86101` 未扫、`86090` 已扫待确认、`86038` 过期）——却写在了 `BiliLoginViewModel.biliPollQrCodeStatus()` 里（`BiliLoginViewModel.kt:68-137`）。**逻辑和分层正好装反了。**

> 建议：UseCase 只保留"有业务规则"的：
> - 把二维码状态机下沉为 `PollQrCodeStatusUseCase` 或 `AuthRepository` 内部逻辑（输入 qrcodeKey，输出一个语义化状态：`Waiting / Scanned / Expired / Success(url, refreshToken)`）；
> - 纯透传的 UseCase 直接删除，ViewModel 按需直接调 Repository（中小项目完全可以接受）。
>
> 判断标准：**这个 UseCase 删掉后，把 `invoke()` 换成直接调 Repository，业务行为是否不变？** 不变 → 它就是多余的一层。

#### 问题 3：网络模型与领域模型字段 1:1 复制，映射是纯样板代码

`UserInfo`（`core/network/model/UserInfo.kt`）与 `UserInfoDomain`（`core/domain/model/UserInfoDomain.kt`）字段完全一致，`toDomain()` 逐字段复制；`RecommendData → RecommendDataDomain` 同理（`RecommendData.kt:45-80`）。

映射层只有在**两个模型真正不同**（网络字段名丑、UI 需要派生字段、要屏蔽敏感字段）时才有价值。逐字段复制只会让改动成本翻倍（加一个字段要改 3 个文件）。

> 建议（三选一）：
> 1. **合并模型**（推荐，中小项目）：只保留一份 `core:model`（或 `domain/model`），网络层直接返回领域模型，删掉所有 `toDomain()`。B 站这类"字段即所需"的接口最合适。
> 2. 保留两层，但**只映射真正变化的字段**，其余直接透传或用 `copy()`。
> 3. 如果坚持双模型，至少把 `toDomain()` 从网络模型文件里移走（见问题 4）。

#### 问题 4：依赖方向倒挂——网络层文件 import 领域层

`core/network/model/RecommendData.kt` 第 3-6 行 import 了 `com.software.biliapp.domain.model.*`，`UserInfo.kt` import 了 `com.software.core.domain.model.UserInfoDomain`。**网络（数据）层反向依赖领域层**，且把映射函数与 DTO 写在同一个文件。

> 建议：若保留双模型，映射函数放**数据层**（如 `core/network/mapper/` 或 repository 内部），DTO 文件不 import 领域层；领域层永远不 import 网络层。

#### 问题 5：PagingSource 绕过 Repository 直接注入 ApiService

`BiliRecommendPagingSource.kt:11-13` 通过 `@Inject constructor(private val apiSerVice: BiliApiService)` 直接拿 ApiService；而 `BiliRecommendVideoRepositoryImpl.getRecommendVideoPagingFlow()` 里又**手动 `new BiliRecommendPagingSource(apiService)`**（`BiliRecommendVideoRepository.kt:70`）——`@Inject` 注解根本没被使用。

同一份数据（推荐列表），非分页路径走 Repository，分页路径绕过 Repository，两条路不统一。

> 建议：PagingSource 属于数据层，由 Repository 持有并暴露 `Flow<PagingData<...>>`；UseCase/ViewModel 只消费流，不知道 PagingSource 存在。写法上给 `BiliRecommendPagingSource` 保留 `@Inject` 构造器，Repository 通过 `pagingSourceFactory = { pagingSource }` 注入，而不是 `new`。

#### 问题 6：错误处理不一致、错误消息丢失

| 位置 | 写法 | 问题 |
| --- | --- | --- |
| `BiliGetUserInfoRepository.kt:23-27` | `Exception("Failed to get user info")` | 丢弃了 B 站返回的 `message` 字段 |
| `BiliRecommendVideoRepository.kt:53` | `Exception("Code is not 0")` | 空泛，无 code/message |
| `BlBlPollQrCodeStatusRepository.kt:22` | `Exception("Code is not 200")` | 实际判断的是 `code == 0`，消息却写 200，自相矛盾 |
| `BlBlQrCodeDataRepository.kt:30` | `Result.failure(Exception(e))` | 用 `Exception` 包 `Exception` |

> 建议：统一错误模型。`BiliResponse` 已有 `code/message`，可定义一个共享的 `AppException(code, message)` 或 `Result.failure` 时携带 message：
> ```kotlin
> Result.failure(BiliApiException(code = response.code, message = response.message))
> ```
> 上层 UI 直接用 `message` 提示用户，而不是显示笼统的"失败"。

### 2.3 目标结构（重构后）

```
core:network          Retrofit / OkHttp / DTO（不 import 领域层）
core:data             AuthRepository / UserRepository / VideoRepository（接口+实现，错误统一）
core:domain           （可选）仅保留有规则的 UseCase + 领域模型
core:model            （若合并模型）共享模型
feature:login         BiliLoginViewModel 只做编排：调 AuthRepository / PollQrStatus 状态机
                      UiState 只含 UI 模型（String/Boolean），不泄漏 domain 模型与 Bitmap
```

---

## 3. 审计发现总表（按严重程度）

### P0 —— 影响功能 / 构建

| # | 位置 | 问题 | 建议 |
| --- | --- | --- | --- |
| P0-1 | `app/AppNavHost.kt:18` | `onLoginSuccess = {}` 空实现；NavHost 只注册 `login`，无 home/主页路由 | 注册 home 导航图，`onLoginSuccess` 里 `navigate(HomeRoute)` 并清空返回栈 |
| P0-2 | `core`、`features:designsystem`、`features:home`、`features:login` 四个 `build.gradle.kts` | `testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"` —— **support 库类在 androidx 工程中不存在**，跑 instrumented test 必崩 | 统一改为 `androidx.test.runner.AndroidJUnitRunner` |
| P0-3 | `libs.versions.toml:25-28` + 各模块 `build.gradle.kts` | `com.android.support:appcompat-v7:28.0.0`、`com.android.support.test:runner:1.0.2`、`com.android.support.test.espresso` 与 androidx 混用，`gradle.properties` 还开着 `android.enableJetifier=true` | Compose 工程不需要 appcompat/support；删除 support 依赖与 Jetifier，测试统一走 `androidx.test` |
| P0-4 | `BiliLoginUiScreen.kt:140` | 密码登录 `onLoginClick = { /* 实现密码登录逻辑 */ }` 空实现，但入口已展示给用户 | 要么实现，要么先隐藏"账号登录" Tab（B 站密码登录还需 RSA 加密，工作量不小，建议先只做扫码） |
| P0-5 | `core/build.gradle.kts:35` 只引 `paging-common` | `BiliRecommendVideoRepositoryImpl` 使用 `androidx.paging.Pager`，**`Pager` 在 `paging-runtime` 而非 `paging-common`**，当前能编译可能只是靠传递依赖，属未声明依赖 | 显式添加 `androidx.paging:paging-runtime` |

### P1 —— 影响架构

| # | 位置 | 问题 | 建议 |
| --- | --- | --- | --- |
| P1-1 | 全局 | **两套 package 树并存**：`com.software.biliapp.data.repository / biliapp.domain.usecase / biliapp.domain.model / biliapp.data.remote.model` 与 `com.software.core.network.repository / core.domain.usecase / core.network.model / core.domain.model`；且多数文件**物理路径与包名不符**（如 `core/.../network/repository/BiliGetUserInfoRepository.kt` 第 1 行声明 `package com.software.biliapp.data.repository`） | 统一为 `com.software.core.*`（或 `com.software.biliapp.core.*`），文件路径与包名严格一致；这是从单模块拆多模块时没做包迁移留下的债 |
| P1-2 | §2 | 分层"形似神不至"（透传 UseCase / 1:1 Repository / 映射样板 / PagingSource 绕过） | 按 §2.3 重构 |
| P1-3 | `core/build.gradle.kts:33-69` | **core 职责过重**：Compose、activity-compose、navigation-compose、media3-exoplayer、Room、appcompat 全塞进 core | 拆分为 `core:network / core:data / core:domain / core:model / core:designsystem`；core 的数据层不应该有 Compose/ExoPlayer |
| P1-4 | `features:designsystem` | 空模块，与 app 自带 theme、login 硬编码颜色三者分裂 | 主题/颜色/通用组件迁入 designsystem，app 与 feature 统一引用 |
| P1-5 | `core/network/model/*.kt` | 网络 DTO import 领域模型（依赖倒挂） | 见 §2 问题 4 |
| P1-6 | `NetworkModule.kt:52` | OkHttp 拦截器里 `runBlocking { biliSessionManager.cookieFlow.first() }`：每个请求都阻塞读 DataStore | 用 `MutableStateFlow` 镜像 cookie 常驻内存，拦截器只读内存；或用 OkHttp 的 suspend 拦截器 |
| P1-7 | `NetworkModule.kt:110-120` | `BiliLoginNetwork` 的 Retrofit **没有挂 okhttpClient**：passport 请求无 UA/Referer/Cookie/日志，扫码登录链路与主链路行为不一致 | 复用同一个 OkHttpClient |
| P1-8 | `NetworkModule.kt:46` | `HttpLoggingInterceptor.Level.BODY` 全量打印，release 包会泄露 Cookie/token | 按 buildType 区分：debug BODY、release BASIC/NONE |
| P1-9 | `NetworkModule.kt:105 / 132` | 同一个 `BiliApiService` 接口创建了两个实例（`@BiliAppNetwork` 与 `@BiliApiNetwork`），仅 baseUrl 不同，语义混乱 | 要么合并成一个 Retrofit（`api.bilibili.com`），要么把接口按 baseUrl 拆成不同命名接口 |
| P1-10 | `BiliUseCaseModule.kt`（整个文件） | 所有 UseCase 都有 `@Inject` 构造器，Dagger **不需要任何 Module 就能注入**；`BiliRepositoryModule` 的 `@Provides` 也可用 `@Binds` 精简 | 删除 `BiliUseCaseModule`；Repository 绑定改 `@Binds abstract fun` |
| P1-11 | `core/build.gradle.kts:50-51`、`app/build.gradle.kts:57-58` | Room 同时 `ksp(...)` 与 `annotationProcessor(...)` 双通道编译 | 只保留 KSP 一条通道 |
| P1-12 | `BiliRecommendPagingSource.kt:11` | PagingSource 直接注入 ApiService 绕过仓储 | 见 §2 问题 5 |

### P2 —— 代码质量 / 一致性

| # | 位置 | 问题 | 建议 |
| --- | --- | --- | --- |
| P2-1 | `BlBlQrCodeDataRepository`、`BlBlPollQrCodeStatusRepository`、`provideBLBLNetwork`、`provideBLBLApiService`、`provideBlBlpollQrCodeStatusRepository`、`apiSerVice` | `BlBl`/`BLBL`/`SerVice` 拼写错误 | 统一改为 `Bili` / `service` |
| P2-2 | 各 Repository | 错误消息不一致、message 丢失（§2 问题 6） | 统一错误模型 |
| P2-3 | `BiliLoginUiState.kt:15-17` | `currentPasswordError / currentConfirmPassword / currentConfirmPasswordError` 从未被 UI 使用（注册功能的残留）；`qrPollData` 只写不读 | 删除死状态 |
| P2-4 | `BiliLoginViewModel.kt` | 大量 `Log.d` 调试日志（QRCode/API_DEBUG 等） | 用 `Timber` 或统一 logger，发布前清理 |
| P2-5 | `BiliLoginViewModel.kt:150` | `ZQRCodeUtils.generateQRCode()`（512×512 双重循环约 26 万次像素操作）在 `viewModelScope`（主线程）执行 | `withContext(Dispatchers.Default)` 生成 |
| P2-6 | `BiliLoginUiScreen.kt` | 品牌色 `Color(0xFFFB7299)` 硬编码 6+ 处 | 收敛到 designsystem 的 `BiliPink` token |
| P2-7 | `BiliSessionManager` 包名 `core.mongo.bili` | `mongo` 命名可疑（既非 MongoDB 也非现有语义） | 改名 `core.session` 或 `core.auth` |
| P2-8 | `libs.versions.toml` | `androidx-compose-runtime` 单独钉 `1.11.4`（与 BOM 2024.09.00 的 1.7.x 不一致）；`room-compiler` 有两个别名（`androidx-room-room-compiler` / `androidx-room-compiler`）；runner/espresso 用 support 旧版 | Compose 全家桶统一走 BOM；清理重复别名与旧依赖 |
| P2-9 | `BiliLoginUiScreen.kt` | 通配 import（`androidx.compose.material3.*` 等） | 改为显式 import |
| P2-10 | `AppNavHost.kt:12` | `Box() {}` 空壳包裹 | 移除或填充内容 |

---

## 4. core 层问题明细（逐文件）

| 文件 | 问题 | 严重度 |
| --- | --- | --- |
| `core/build.gradle.kts` | 职责过重（Compose/media3/Room/appcompat）；Room 双通道；paging-common 缺 paging-runtime；support runner | P0-2/P0-5/P1-3/P1-11 |
| `core/.../di/NetworkModule.kt` | 拦截器 runBlocking；BODY 日志；passport 无 OkHttpClient；两个 BiliApiService 实例 | P1-6~P1-9 |
| `core/.../di/BiliRepositoryModule.kt` | 10 个 @Provides 手写绑定，可改 @Binds | P1-10 |
| `core/.../di/BiliUseCaseModule.kt` | 整个文件冗余，可删除 | P1-10 |
| `core/.../network/repository/*`（10 个） | 按接口 1:1、错误处理不一致、部分文件包名与路径不符 | P1-1/P1-2/P2-2 |
| `core/.../network/paging/BiliRecommendPagingSource.kt` | 绕过仓储直接注入 ApiService；`apiSerVice` 拼写 | P1-12/P2-1 |
| `core/.../network/model/*` | DTO import 领域模型（依赖倒挂）；与 domain 模型 1:1 重复 | P1-5/P2-8 |
| `core/.../domain/usecase/*`（12 个） | 绝大多数纯透传；真正逻辑在 ViewModel | P1-2 |
| `core/.../domain/model/*` | 与网络模型字段 1:1 复制 | P1-5 |
| `core/.../mongo/bili/BiliSessionManager.kt` | 包名 `mongo` 语义不明；cookie 仅存 DataStore 无内存镜像（加剧拦截器阻塞） | P1-6/P2-7 |
| `core/.../util/ZQRCodeUtils.kt` | 调用方在主线程跑 512×512 逐像素循环 | P2-5 |

---

## 5. feature 层问题明细

### 5.1 `features:login`

**功能链路问题**

1. **登录成功无处可去**（P0-1）：`BiliLoginViewModel` 成功登录后发 `NavigateToRecommend` 效果 → `BiliLoginUiScreen` 调 `onNavigateToMain()` → `AppNavHost` 里是空 lambda。扫码登录"成功"了，但界面纹丝不动。
2. **已登录用户也被卡在登录页**：`checkLoginStatus()` 检测到 cookie 存在同样发 `NavigateToRecommend`，但同一空回调问题。
3. **密码登录是空实现**（P0-4），但 Tab 已展示。

**状态管理问题**

4. `BiliLoginUiState` 直接持有**领域模型**（`QrCodeDataDomain`、`QrPollDataDomain`）和 **`Bitmap`**——UI 层不该感知 domain 模型；Bitmap 建议由 UI 侧 `remember(url) { 异步生成 }` 持有，UiState 只放 `qrUrl: String?` 与状态枚举。
5. 死状态：`qrPollData` 只写不读；`currentPasswordError/currentConfirmPassword/currentConfirmPasswordError` 无人使用。
6. 二维码状态机（86101/86090/86038/0）散落在 ViewModel 的 while 循环里，无状态枚举、无测试覆盖。

**实现细节问题**

7. `LaunchedEffect(Unit)` 出现两次，其中第二个读取 `uiState.qrBitmap` 判断是否拉取二维码——闭包捕获的是组合时刻的旧状态，逻辑脆弱（当前能跑但属于"碰巧对"）。
8. 轮询 `catch (e: Exception)` 静默吞异常，用户无感知；失败分支虽发 Toast，但轮询会无限重试直到过期，无最大重试/退避。
9. `Log.d` 遍布 ViewModel。

### 5.2 `features:home` —— 空壳模块

`BiliHomeViewModel / BiliHomeUiScreen / BiliHomeUiState / BiliHomeUiEvent` 四个文件全部是 `class X {}`，且 app 未依赖、未接线。建议：要么按规范真正实现首页（推荐列表/顶部 Tab），要么暂时从 `settings.gradle.kts` 移除，避免"看起来有、实际上没有"的误导。

### 5.3 `features:designsystem` —— 空模块

只有 build.gradle 和测试占位，**没有任何主题代码**。而 app 有 `ui/theme/`，login 硬编码品牌色。建议：把 `app/ui/theme` 迁入 designsystem，定义 `BiliPink` 等 color token、Typography、`BiliTheme {}`，app 与各 feature 统一依赖它。

---

## 6. 构建与依赖配置问题（gradle 层面）

| 位置 | 问题 | 建议 |
| --- | --- | --- |
| `gradle.properties:25` | `android.enableJetifier=true` —— 只为 support 库开的"转译税"，拖慢构建、有兼容风险 | 删除 support 依赖后关闭 Jetifier |
| `libs.versions.toml:25-28` | support 版 `runner 1.0.2` / `espresso 3.0.2` / `appcompat-v7 28.0.0` 与 androidx 混用 | 全部替换为 androidx 对应物，或直接删除（Compose 工程不需要 appcompat） |
| `libs.versions.toml:30,77` | `runtime = 1.11.4` 单独钉 compose-runtime，与 BOM 冲突 | Compose 组件一律走 BOM |
| 四个模块 | `testInstrumentationRunner` 指向 support 类 | 统一 `androidx.test.runner.AndroidJUnitRunner` |
| `core/build.gradle.kts:51` / `app/build.gradle.kts:58` | `annotationProcessor(room-compiler)` 与 `ksp(room-compiler)` 并存 | 只留 KSP |
| `core/build.gradle.kts:36,43` | core 引入 media3-exoplayer / compose / navigation | 迁移到对应 feature 或 core:designsystem |
| `app/build.gradle.kts:56-58` | app 重复依赖 Room（运行时无必要） | 删除 |
| 全局 | 各模块 build.gradle 大量重复（compileSdk/minSdk/jvmTarget/测试依赖） | 引入 convention plugin（build-logic）统一收口 |

---

## 7. Login 界面优化方案

> 本方案只描述"怎么改"，**不落地代码**。核心思路：**把 UI 做"无状态化"，把品牌与交互收进设计系统，把状态机从 ViewModel 挪到领域层。**

### 7.1 现状问题（UI 视角）

1. **品牌色硬编码**：`Color(0xFFFB7299)` 在 `BiliLoginUiScreen.kt` 出现 6+ 处（Logo 色调、Tab 指示器、加载圈、按钮、边框、文字），改主题要全局搜索替换。
2. **反馈手段原始**：成功/失败全靠 Android `Toast`，无法表达"已扫码待确认"这类持续状态；错误文案是 ViewModel 里拼的字符串。
3. **输入体验缺失**：无 IME 适配（软键盘可能遮挡登录按钮）、无键盘 Next/Done action、无 autofill（账号密码无法被系统自动填充）、密码框无明文切换。
4. **无表单校验**：空账号/空密码直接点登录无任何反馈。
5. **无加载态**：登录按钮无 loading 指示，重复点击无法防止。
6. **二维码状态可视化不足**：只有"二维码 + 刷新按钮"，扫码后到确认之间的"已扫码，请在手机确认"只以 Toast 一闪而过，无持续性提示；二维码过期只有 Toast。
7. **性能隐患**：512×512 二维码在主线程生成（§P2-5）。
8. **不可测试**：`BiliLoginScreen` 直接 `hiltViewModel()`，无法脱离 Hilt 单独预览/测试。
9. **死代码**：注册相关字段、`qrPollData`（§P2-3）。

### 7.2 优化目标

```
1. 主题 token 化：所有颜色/间距/字号走 designsystem，UI 零魔法值
2. UI 无状态化：BiliLoginScreen 只接收 (UiState + 回调)，可 Preview、可测试
3. 状态可视化：二维码区域用状态机驱动（Loading / Ready / Scanned / Expired / Success）
4. 反馈升级：Toast → Snackbar（可带 action 重试）
5. 输入体验：imePadding + keyboard actions + autofill + 密码可见性
6. 校验与防抖：提交前校验、按钮 loading、防重复点击
7. 性能：二维码生成切 IO 线程；Bitmap 不在 UiState 中
```

### 7.3 分模块方案

#### A. 设计系统（`features:designsystem`，当前为空——先把它用起来）

- 定义 color token：`BiliPink = Color(0xFFFB7299)`、`BiliPinkDark`、`Surface/OnSurface` 等；亮/暗两套 `ColorScheme`。
- 定义 `BiliTheme { }`，app 的 `ui/theme` 迁移进来后删除 app 内重复主题。
- 定义通用组件：`BiliPrimaryButton`（含 loading 态）、`BiliOutlinedTextField`（统一形状/聚焦色）、`BiliSnackbarHost`。
- 品牌 Logo 资源统一放 designsystem 的 res。

#### B. 状态模型（`BiliLoginUiState` 重构）

```kotlin
// 目标形态（示意，非代码落地）
data class BiliLoginUiState(
    val qrState: QrCodeUiState,        // 密封类：Loading / Ready(url) / Scanned / Expired / Success
    val username: String = "",
    val password: String = "",
    val usernameError: String? = null,
    val passwordError: String? = null,
    val isSubmitting: Boolean = false, // 密码登录 loading
    val snackbarMessage: String? = null,
)
```

- 移除：`QrCodeDataDomain`、`QrPollDataDomain`、`Bitmap`、`qrPollData`、注册残留字段。
- 领域模型只存在于 ViewModel 内部，跨出边界即转成 UI 模型。

#### C. 领域层（状态机下沉）

把二维码轮询收敛为领域层的一个语义化状态机，ViewModel 只做订阅与转发：

```kotlin
// 示意：AuthRepository / PollQrCodeStatusUseCase 内部
sealed interface QrPollResult {
    data object WaitingScan
    data object WaitingConfirm
    data object Expired
    data class Success(val url: String, val refreshToken: String)
}
```

- 轮询循环（`while(isActive) + delay(3000)`）可以留在 ViewModel，但**状态判定**（code → 语义）必须下沉，便于单元测试。
- 失败分支：连续失败 N 次或网络异常时给出可感知状态，而不是静默重试。

#### D. UI 层（无状态化 + 体验细节）

组件树目标结构（保持现有"扫码 / 账号 两个 Tab + AnimatedContent"骨架）：

```
BiliLoginRoute（状态容器：hiltViewModel + collectAsStateWithLifecycle + Snackbar）
└── BiliLoginScreen(uiState, callbacks…)   ← 无状态，可 Preview
    ├── Logo（designsystem 资源 + BiliPink token）
    ├── 欢迎标题
    ├── TabRow（扫码登录 / 账号登录）—— 指示器色走 token
    ├── AnimatedContent
    │   ├── QrCodeSection(uiState.qrState, onRefresh)
    │   │   ├── 二维码（remember(url) + Dispatchers.Default 生成）
    │   │   ├── 状态提示区：Ready→"请使用哔哩哔哩手机端扫码"
    │   │   │                 Scanned→"已扫码，请在手机确认"（常驻，不再一闪而过）
    │   │   │                 Expired→"二维码已过期" + 自动/手动刷新
    │   │   └── 刷新按钮（Expired 时高亮为 PrimaryButton）
    │   └── PasswordSection(username, password, errors, isSubmitting, callbacks…)
    │       ├── 账号输入：autofill、Next 键、校验（非空/格式）、错误文案
    │       ├── 密码输入：autofill、Done 键、可见性切换 Icon、校验
    │       └── 登录按钮：isSubmitting → CircularProgressIndicator；disabled 防重复
```

关键体验细节清单：

| 项 | 现状 | 目标 |
| --- | --- | --- |
| 键盘遮挡 | 无处理 | `Modifier.imePadding()` + `windowInsetsPadding(WindowInsets.ime)`，登录按钮可滚动到可见 |
| 键盘 action | 无 | 账号框 `Next` → 聚焦密码框；密码框 `Done` → 触发登录 |
| Autofill | 无 | `KeyboardOptions` + autofill 提示（用户名/密码） |
| 密码可见性 | 无 | `TrailingIcon` 切换 `PasswordVisualTransformation` / `VisualTransformation.None` |
| 校验 | 无 | 提交时校验：账号非空、密码长度 ≥ 6；错误用 `supportingText` + `isError` |
| 防重复 | 无 | `isSubmitting` 期间禁用按钮 + 输入框 |
| 提示 | Toast | `SnackbarHost`；二维码轮询状态常驻文字提示 |
| 二维码 | 主线程生成 | `withContext(Dispatchers.Default)`；过期自动刷新（`LaunchedEffect(qrState)` + 倒计时） |
| 无障碍 | 部分 | 二维码 contentDescription 随状态变化；按钮/输入框语义化 |

#### E. 可测试性

- `BiliLoginScreen` 纯函数化后，用 Compose UI Test 覆盖：Tab 切换、输入校验、按钮 loading、二维码过期文案。
- 状态机下沉后，用 JUnit 覆盖 `QrPollResult` 分支（86101/86090/86038/0）。
- 现有 `ExampleUnitTest` / `ExampleInstrumentedTest` 是模板占位，无实际断言。

---

## 8. 演进路线（三阶段）

### 阶段 1：止血（半天~1 天，全是 P0）

1. 修 `testInstrumentationRunner`（4 个模块）。
2. `onLoginSuccess` 接通：注册 home 路由（哪怕是占位页），登录成功/已登录跳转。
3. 密码登录要么实现要么隐藏 Tab。
4. 删 support 依赖（appcompat-v7 / support runner / support espresso）+ 关 Jetifier；补 `paging-runtime`。
5. Room 去掉 annotationProcessor 通道。

### 阶段 2：统一分层（3~5 天，P1）

1. **包结构统一**：把 `com.software.biliapp.*` 全部归入 `com.software.core.*`（或统一前缀），文件路径与包名一致——这是当前最影响阅读成本的债。
2. 按领域聚合 Repository（Auth/User/Video），删纯透传 UseCase，QR 状态机下沉。
3. 网络模型与领域模型二选一（合并 or 保留映射但修正依赖方向）。
4. NetworkModule 整改：拦截器去 runBlocking（内存镜像 cookie）、passport 挂 OkHttpClient、日志分级。
5. core 瘦身：media3/Compose 迁出；designsystem 真正建起来（迁入 theme + token）。
6. PagingSource 走 Repository 注入。

### 阶段 3：质量与工程化（持续，P2）

- 命名清理（BlBl→Bili 等）、错误模型统一、死代码清理。
- Login 界面按 §7 方案重做。
- 建 convention plugin 统一模块构建脚本；补单元测试与 Compose UI 测试。

---

## 9. 附录：目标目录结构（示意）

```
BiliApplication/
├── app/                          # 壳：MainActivity + AppNavHost + BottomBar（后续）
├── core/
│   ├── network/                  # Retrofit、OkHttp、DTO（不 import 领域层）
│   ├── data/                     # AuthRepository / UserRepository / VideoRepository
│   ├── domain/                   # 有规则的 UseCase + 领域模型（可保留可合并）
│   ├── model/                    # （若合并模型）共享模型
│   └── session/                  # 原 mongo/bili/BiliSessionManager
├── features/
│   ├── designsystem/             # BiliTheme、color token、通用组件、Logo 资源
│   ├── login/                    # 扫码 + 账号登录（无状态 UI + 状态机）
│   └── home/                     # 推荐流（PagingSource → Repository → Flow）
└── build-logic/                  # （可选）convention plugin
```

---

## 附：一句话总结

你的分层方向（网络 → Repository → UseCase）**是对的，不用推翻**；要改的是**每个环节的纯度**：Repository 要按领域聚合、UseCase 只留真逻辑、模型别 1:1 复制、PagingSource 别绕路；再配合包结构统一和移除 support 旧依赖，这个项目就能从"形似分层"走向"可用、可测、可扩展"。
