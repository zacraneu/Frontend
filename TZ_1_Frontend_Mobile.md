# ТЗ #1: Мобильное приложение для подачи документов (Kotlin + Jetpack Compose)

**Назначение:** Клиентская часть системы подачи документов для получения государственной услуги на Android (API 33+).

**Технологический стек:**
- Язык: Kotlin
- UI: Jetpack Compose
- Networking: Retrofit/OkHttp
- Аутентификация: JWT (Access Token + Refresh Token)
- Локальное хранилище: Encrypted Shared Preferences, Room Database (опционально для кэша)
- Версия Android: minSdk 33 (Android 13+)

---

## 1. Функциональные требования

### 1.1 Аутентификация и авторизация
- **Экран входа:**
  - Email/телефон + пароль
  - Кнопка "Вход", "Регистрация", "Забыли пароль"
  - Валидация полей на клиенте (email format, пароль не менее 8 символов)
  - Сообщения об ошибках из backend
  
- **Управление токенами:**
  - Сохранение Access Token и Refresh Token в защищённом Shared Preferences
  - Автоматическое обновление Access Token при срока истечения (перед каждым запросом проверять валидность)
  - Логаут: очистка токенов, редирект на экран входа
  - Обработка 401 Unauthorized: попытка обновить токен, если не удалось — логаут

### 1.2 Экран профиля пользователя
- Отображение: ФИО, email, дата регистрации
- Кнопка "Выход"
- Возможность редактирования профиля (ФИО, телефон) — опционально
- Отображение статуса верификации (если применимо)

### 1.3 Экран подачи документов (основной функционал)
- **Экран списка заявок:**
  - Список всех поданных заявок с информацией:
    - ID заявки (короткий идентификатор)
    - Статус (новая, на проверке, одобрена, отклонена)
    - Дата подачи
    - Дата последнего обновления
  - Pull-to-refresh для обновления списка
  - Возможность нажать на заявку → перейти к деталям

- **Экран создания новой заявки:**
  - **Форма заполнения:**
    - Поле "ФИО" (pre-fill из профиля, можно отредактировать)
    - Поле "Email" (pre-fill из профиля)
    - Поле "Телефон"
    - Поле "Причина подачи" (текстовое поле, макс. 500 символов)
    - Поле "Дополнительные сведения" (опционально)
  
  - **Загрузка документов:**
    - Кнопка "Добавить документ" → открыть picker (галерея + камера)
    - Поддержка форматов: PDF, JPEG, PNG
    - Ограничение на размер одного файла: макс. 10 MB
    - Ограничение количества файлов: макс. 5 файлов на заявку
    - Отображение список загруженных файлов с иконками, именами, размерами
    - Возможность удалить файл из списка до отправки
    - Сжатие изображений перед отправкой (JPEG, качество 85%)
  
  - **Валидация перед отправкой:**
    - ФИО не пусто и корректно
    - Email валидный
    - Телефон валидный (российский формат: +7-XXX-XXX-XX-XX)
    - Минимум 1 документ загружен
    - Все документы < 10 MB
    - Сумма размеров документов < 30 MB
  
  - **Отправка заявки:**
    - Multipart/form-data запрос к `POST /api/v1/applications`
    - Показать индикатор прогресса загрузки
    - Обработать ошибки сети (показать retry)
    - На успех: показать toast "Заявка успешно подана", переход к списку заявок

### 1.4 Экран просмотра деталей заявки
- Отображение всех данных заявки:
  - ID заявки, дата подачи, текущий статус
  - ФИО, email, телефон (из заявки)
  - Причина подачи, дополнительные сведения
  - Список загруженных документов (с возможностью скачать/просмотреть)
  - История изменений статуса (если есть комментарии от администратора)

- **Возможные статусы:**
  - `NEW` — только что подана
  - `REVIEWING` — на проверке
  - `APPROVED` — одобрена
  - `REJECTED` — отклонена (с причиной)
  - `RETURNED` — возвращена на доработку

- **Для отклонённых/возвращённых заявок:**
  - Кнопка "Загрузить исправленные документы" → открыть экран редактирования заявки
  - Поле с комментарием от администратора (если есть)

### 1.5 Экран редактирования заявки (для статуса `RETURNED`)
- Аналогичен экрану создания, но с предзаполненными данными
- Возможность заменить документы (удалить старые, загрузить новые)
- Кнопка "Переотправить заявку"

### 1.6 Уведомления и статусы
- Отображение статуса синхронизации (если идёт отправка)
- Toast-сообщения для ошибок и успехов
- Обновление списка заявок в фоне (при открытии приложения или периодически)

---

## 2. Нефункциональные требования

### 2.1 Безопасность
- Все чувствительные данные (токены) хранить в **EncryptedSharedPreferences** (AndroidX Security)
- HTTPS для всех запросов (cert pinning опционально)
- Проверка сертификата SSL
- Не логировать чувствительные данные (токены, пароли)

### 2.2 Производительность
- Загрузка списка заявок < 2 сек
- Загрузка изображений с кэшированием (Coil или Glide)
- Сжатие изображений перед отправкой
- Оптимизация памяти при работе с большими файлами

### 2.3 UX/UI
- Материал Design 3 (Material You)
- Тёмная тема поддержка
- Обработка состояний Loading, Success, Error
- Retry механизм при ошибках сети
- Graceful degradation (показ оффлайн состояния если нет соединения)

### 2.4 Тестирование
- Unit тесты для валидации форм
- Integration тесты для API запросов (mock server)
- UI тесты (Espresso) для основных экранов

---

## 3. Архитектура приложения

```
app/
├── ui/
│   ├── screens/
│   │   ├── LoginScreen.kt
│   │   ├── ProfileScreen.kt
│   │   ├── ApplicationListScreen.kt
│   │   ├── CreateApplicationScreen.kt
│   │   ├── ApplicationDetailScreen.kt
│   │   └── EditApplicationScreen.kt
│   ├── components/
│   │   ├── DocumentUploadWidget.kt
│   │   ├── ApplicationStatusBadge.kt
│   │   ├── LoadingIndicator.kt
│   │   └── ErrorDialog.kt
│   ├── navigation/
│   │   └── NavigationGraph.kt
│   └── theme/
│       ├── Color.kt
│       ├── Type.kt
│       └── Theme.kt
├── viewmodel/
│   ├── AuthViewModel.kt
│   ├── ProfileViewModel.kt
│   ├── ApplicationListViewModel.kt
│   ├── CreateApplicationViewModel.kt
│   └── ApplicationDetailViewModel.kt
├── domain/
│   ├── model/
│   │   ├── User.kt
│   │   ├── Application.kt
│   │   ├── Document.kt
│   │   └── AuthResponse.kt
│   ├── repository/
│   │   ├── AuthRepository.kt
│   │   ├── ApplicationRepository.kt
│   │   └── UserRepository.kt
│   └── usecase/
│       ├── LoginUseCase.kt
│       ├── GetApplicationsUseCase.kt
│       └── CreateApplicationUseCase.kt
├── data/
│   ├── remote/
│   │   ├── ApiService.kt
│   │   ├── AuthInterceptor.kt
│   │   └── TokenRefreshService.kt
│   ├── local/
│   │   ├── PreferencesManager.kt
│   │   └── TokenStorage.kt
│   └── di/
│       ├── NetworkModule.kt
│       └── RepositoryModule.kt
└── utils/
    ├── FileUtils.kt
    ├── ValidationUtils.kt
    ├── ImageCompression.kt
    └── Constants.kt
```

---

## 4. API контракты (запросы к backend)

### 4.1 Аутентификация
```
POST /api/v1/auth/register
Content-Type: application/json
{
  "email": "user@example.com",
  "password": "securePassword123",
  "fullName": "Ivan Petrov"
}

Response 200:
{
  "userId": "uuid-1234",
  "email": "user@example.com",
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "expiresIn": 3600
}

---

POST /api/v1/auth/login
Content-Type: application/json
{
  "email": "user@example.com",
  "password": "securePassword123"
}

Response 200:
{
  "userId": "uuid-1234",
  "email": "user@example.com",
  "fullName": "Ivan Petrov",
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "expiresIn": 3600
}

---

POST /api/v1/auth/refresh
Content-Type: application/json
{
  "refreshToken": "eyJhbGc..."
}

Response 200:
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "expiresIn": 3600
}

---

POST /api/v1/auth/logout
Authorization: Bearer {accessToken}

Response 200: { "message": "Logged out successfully" }
```

### 4.2 Приложения (заявки)
```
GET /api/v1/applications
Authorization: Bearer {accessToken}
Query params: page=0&size=20&sort=createdAt,desc

Response 200:
{
  "content": [
    {
      "id": "app-uuid-1",
      "userId": "user-uuid",
      "status": "REVIEWING",
      "createdAt": "2025-05-18T10:30:00Z",
      "updatedAt": "2025-05-18T10:35:00Z",
      "documents": [
        {
          "id": "doc-uuid-1",
          "filename": "passport.pdf",
          "fileSize": 512000,
          "uploadedAt": "2025-05-18T10:30:00Z"
        }
      ]
    }
  ],
  "totalElements": 5,
  "totalPages": 1,
  "currentPage": 0
}

---

GET /api/v1/applications/{applicationId}
Authorization: Bearer {accessToken}

Response 200:
{
  "id": "app-uuid-1",
  "userId": "user-uuid",
  "fullName": "Ivan Petrov",
  "email": "ivan@example.com",
  "phone": "+79991234567",
  "status": "REVIEWING",
  "submissionReason": "Получение справки о доходах",
  "additionalInfo": "Требуется срочно",
  "createdAt": "2025-05-18T10:30:00Z",
  "updatedAt": "2025-05-18T10:35:00Z",
  "reviewedAt": null,
  "rejectionReason": null,
  "documents": [
    {
      "id": "doc-uuid-1",
      "applicationId": "app-uuid-1",
      "filename": "passport.pdf",
      "originalFilename": "passport.pdf",
      "fileSize": 512000,
      "mimeType": "application/pdf",
      "uploadedAt": "2025-05-18T10:30:00Z"
    }
  ]
}

---

POST /api/v1/applications
Authorization: Bearer {accessToken}
Content-Type: multipart/form-data

fullName: "Ivan Petrov"
email: "ivan@example.com"
phone: "+79991234567"
submissionReason: "Получение справки о доходах"
additionalInfo: "Требуется срочно"
documents: [file1, file2, ...]

Response 201:
{
  "id": "app-uuid-new",
  "userId": "user-uuid",
  "status": "NEW",
  "createdAt": "2025-05-18T10:40:00Z",
  "message": "Application created successfully"
}

---

PUT /api/v1/applications/{applicationId}
Authorization: Bearer {accessToken}
Content-Type: multipart/form-data
(только для заявок со статусом RETURNED)

fullName: "Ivan Petrov"
email: "ivan@example.com"
phone: "+79991234567"
submissionReason: "Получение справки о доходах"
additionalInfo: "Исправлено"
documents: [file1, file2, ...] (новые документы)

Response 200:
{
  "id": "app-uuid-1",
  "status": "REVIEWING",
  "updatedAt": "2025-05-18T11:00:00Z",
  "message": "Application updated and resubmitted"
}

---

GET /api/v1/applications/{applicationId}/documents/{documentId}
Authorization: Bearer {accessToken}

Response 200: (binary file)
Header: Content-Disposition: attachment; filename="passport.pdf"

---

GET /api/v1/user/profile
Authorization: Bearer {accessToken}

Response 200:
{
  "userId": "user-uuid",
  "email": "ivan@example.com",
  "fullName": "Ivan Petrov",
  "phone": "+79991234567",
  "registeredAt": "2025-05-01T15:00:00Z",
  "isVerified": true
}
```

---

## 5. Примеры реализации (Kotlin)

### 5.1 AuthViewModel
```kotlin
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenStorage: TokenStorage
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val response = authRepository.login(email, password)
                tokenStorage.saveAccessToken(response.accessToken)
                tokenStorage.saveRefreshToken(response.refreshToken)
                _uiState.value = AuthUiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
    
    fun refreshToken() {
        viewModelScope.launch {
            try {
                val refreshToken = tokenStorage.getRefreshToken() ?: return@launch
                val response = authRepository.refreshToken(refreshToken)
                tokenStorage.saveAccessToken(response.accessToken)
            } catch (e: Exception) {
                tokenStorage.clearTokens()
                _uiState.value = AuthUiState.TokenExpired
            }
        }
    }
}

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val response: AuthResponse) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
    object TokenExpired : AuthUiState()
}
```

### 5.2 CreateApplicationViewModel
```kotlin
@HiltViewModel
class CreateApplicationViewModel @Inject constructor(
    private val applicationRepository: ApplicationRepository,
    private val imageCompression: ImageCompression
) : ViewModel() {
    
    private val _formState = MutableStateFlow(ApplicationFormState())
    val formState: StateFlow<ApplicationFormState> = _formState.asStateFlow()
    
    private val _submitState = MutableStateFlow<SubmitState>(SubmitState.Idle)
    val submitState: StateFlow<SubmitState> = _submitState.asStateFlow()
    
    fun addDocument(uri: Uri, fileName: String) {
        viewModelScope.launch {
            try {
                val file = uriToFile(uri)
                if (file.length() > 10 * 1024 * 1024) {
                    _submitState.value = SubmitState.Error("Файл больше 10 MB")
                    return@launch
                }
                
                val compressedFile = if (fileName.endsWith(".jpg") || fileName.endsWith(".png")) {
                    imageCompression.compress(file)
                } else {
                    file
                }
                
                val document = DocumentUpload(
                    uri = uri,
                    file = compressedFile,
                    name = fileName
                )
                
                val currentDocs = _formState.value.documents.toMutableList()
                if (currentDocs.size < 5) {
                    currentDocs.add(document)
                    _formState.value = _formState.value.copy(documents = currentDocs)
                } else {
                    _submitState.value = SubmitState.Error("Максимум 5 документов")
                }
            } catch (e: Exception) {
                _submitState.value = SubmitState.Error(e.message ?: "Error")
            }
        }
    }
    
    fun removeDocument(index: Int) {
        val updated = _formState.value.documents.toMutableList()
        updated.removeAt(index)
        _formState.value = _formState.value.copy(documents = updated)
    }
    
    fun submitApplication() {
        viewModelScope.launch {
            val form = _formState.value
            
            // Валидация
            val validationError = form.validate()
            if (validationError != null) {
                _submitState.value = SubmitState.Error(validationError)
                return@launch
            }
            
            _submitState.value = SubmitState.Loading
            try {
                val response = applicationRepository.createApplication(
                    fullName = form.fullName,
                    email = form.email,
                    phone = form.phone,
                    submissionReason = form.submissionReason,
                    additionalInfo = form.additionalInfo,
                    documents = form.documents.map { it.file }
                )
                _submitState.value = SubmitState.Success(response.id)
            } catch (e: Exception) {
                _submitState.value = SubmitState.Error(e.message ?: "Error")
            }
        }
    }
}

data class ApplicationFormState(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val submissionReason: String = "",
    val additionalInfo: String = "",
    val documents: List<DocumentUpload> = emptyList()
) {
    fun validate(): String? {
        if (fullName.isBlank()) return "ФИО не может быть пустым"
        if (!email.contains("@")) return "Некорректный email"
        if (phone.isBlank()) return "Телефон не может быть пустым"
        if (submissionReason.isBlank()) return "Причина подачи не может быть пустой"
        if (documents.isEmpty()) return "Загрузите минимум 1 документ"
        return null
    }
}

sealed class SubmitState {
    object Idle : SubmitState()
    object Loading : SubmitState()
    data class Success(val applicationId: String) : SubmitState()
    data class Error(val message: String) : SubmitState()
}
```

### 5.3 Composable для загрузки документов
```kotlin
@Composable
fun DocumentUploadWidget(
    documents: List<DocumentUpload>,
    onAddDocument: () -> Unit,
    onRemoveDocument: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            "Загруженные документы (макс. 5)",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyColumn {
            itemsIndexed(documents) { index, doc ->
                DocumentItem(
                    name = doc.name,
                    size = doc.file.length(),
                    onRemove = { onRemoveDocument(index) }
                )
            }
        }
        
        Button(
            onClick = onAddDocument,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            enabled = documents.size < 5
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Добавить документ")
        }
        
        if (documents.size >= 5) {
            Text(
                "Максимум документов достигнут",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun DocumentItem(
    name: String,
    size: Long,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(getIconForFile(name)),
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp)
        ) {
            Text(name, style = MaterialTheme.typography.bodyMedium)
            Text(
                formatFileSize(size),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
        IconButton(onClick = onRemove) {
            Icon(Icons.Default.Delete, contentDescription = "Удалить")
        }
    }
}
```

---

## 6. Инструменты и библиотеки

**Core:**
- AndroidX Core, AppCompat, Lifecycle
- Jetpack Compose (latest stable)
- Jetpack Navigation Compose

**Networking:**
- Retrofit 2
- OkHttp 4 (с interceptors для JWT)
- Kotlinx Serialization / Moshi

**DI:**
- Hilt for Android

**Security:**
- AndroidX Security (EncryptedSharedPreferences)

**Image Loading:**
- Coil for Compose

**Storage:**
- Room Database (для кэша, опционально)

**Testing:**
- JUnit 4
- Mockito / MockK
- Espresso

---

## 7. Сценарии использования

### Сценарий 1: Первый запуск
1. Пользователь открывает приложение
2. Видит экран входа
3. Может зарегистрироваться или войти
4. После входа перенаправляется на список заявок

### Сценарий 2: Подача новой заявки
1. На экране списка заявок нажимает кнопку "Новая заявка"
2. Заполняет форму
3. Загружает документы (1-5 файлов)
4. Нажимает "Отправить"
5. Ждёт завершения загрузки
6. Видит подтверждение и перенаправляется на список

### Сценарий 3: Проверка статуса
1. На экране списка видит все свои заявки
2. Нажимает на заявку → видит детали
3. Может скачать загруженные документы
4. Видит текущий статус и историю изменений

### Сценарий 4: Переотправка после отклонения
1. Видит заявку со статусом "REJECTED"
2. Нажимает кнопку "Переотправить"
3. Редактирует форму и документы
4. Переотправляет заявку

---

## 8. Критерии приёмки

- [ ] Приложение компилируется и запускается на Android 13+
- [ ] Все экраны функциональны и связаны навигацией
- [ ] Валидация форм работает корректно
- [ ] Загрузка документов работает (одновременно с UI feedback)
- [ ] Токены сохраняются и обновляются корректно
- [ ] Сообщения об ошибках понятны и полезны
- [ ] Приложение работает в режиме offline (graceful degradation)
- [ ] Unit тесты для критических компонентов
- [ ] UI/UX соответствует Material Design 3
- [ ] Нет memory leaks (проверено в Profiler)
- [ ] APK размер < 30 MB

---

## 9. Сроки и этапы

1. **Этап 1 (неделя 1):** Архитектура, DI, базовая навигация
2. **Этап 2 (неделя 2):** Аутентификация, токены, безопасность
3. **Этап 3 (неделя 3):** Экраны форм, загрузка документов
4. **Этап 4 (неделя 4):** Экран списка и деталей, синхронизация
5. **Этап 5 (неделя 5):** Тестирование, оптимизация, полировка

**Итого:** 5-6 недель разработки при 40 часов/неделю.
