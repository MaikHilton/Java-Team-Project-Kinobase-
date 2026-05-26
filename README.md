# 🎬 Кінобаза — Інформаційна система оцінювання фільмів

> GUI-застосунок на основі **JavaFX + JDBC + PostgreSQL**  
> Аналог IMDb для локального кола користувачів  

---

## 👥 Склад команди

| Роль | ПІБ | GitHub |
|---|---|---|
| 🟡 **Team Lead** | Яценко Іван Олександрович | [@MaikHilton](https://github.com/MaikHilton) |
| 🔵 **Developer 1** | Самков Данило Олександрович | [@DanilSamkov](https://github.com/DanilSamkov) |
| 🔵 **Developer 2** | Цебро Нікіта Олександрович | [@SwiftWhisper](https://github.com/SwiftWhisper) |

---

## 📋 Опис застосунку

**Кінобаза** — локальна інформаційна система обліку фільмів та глядацьких оцінок із прозорою бізнес-логікою. Альтернатива публічним платформам (IMDb) для замкненого кола користувачів із власними правилами оцінювання (варіант 2 КР БД).

**Бізнес-правило:** рейтинг фільму обчислюється як середнє арифметичне всіх оцінок лише якщо проголосувало **більше ніж 10 глядачів** — інакше рейтинг = 0.

---

## ⚙️ Вимоги до архітектури та технологій

🛠 Технологічний стек (Tech Stack)

Згідно з технічним аудитом середовища розробки, проєкт базується на наступних версіях ПЗ:

| Технологія| Версія | Призначення |
|---|---|---|
| Java (JDK) | 25.0.2 LTS | Основна мова та середовище виконання |
|JavaFX | 25 | Графічний інтерфейс користувача (GUI) |
| PostgreSQL | 18.3 | Реляційна система управління базами даних |
| Apache Maven | 3.9.11 | Керування залежностями та збірка проєкту |
| OS| Windows 11 | Операційна система розробки команди |

### Запланована Maven-структура проєкту

```
Java-Team-Project/
├── src/
│   ├── main/
│   │   ├── java/          ← Java-код (entity, dao, controller)
│   │   └── resources/     ← FXML, db.properties
├── docs/                  ← згенерована JavaDoc документація
├── sql/                   ← SQL-скрипти
├── pom.xml
├── .gitignore
└── README.md
```

### Архітектура MVC

| Шар | Що містить | Пакет |
|---|---|---|
| **Model** | Класи предметної області: `Movie`, `Viewer`, `Rating`, `Genre`, `Country` | `entity/` |
| **View** | JavaFX інтерфейс — FXML-розмітка екранів | `resources/fxml/` |
| **Controller** | Обробка подій UI, взаємодія з Model | `controller/` |
| **JDBCHelper / Service** | Доступ до БД через JDBC, бізнес-логіка | `dao/` / `service/` |

### Вимоги до підключення

- Підключення до БД — **виключно через JDBC**
- Запити — **тільки через `PreparedStatement`**
- Параметри підключення — зчитуються з файлу **`db.properties`**

```properties
# src/main/resources/db.properties
db.url=jdbc:postgresql://localhost:5432/kinobase
db.user=postgres
db.password=YOUR_PASSWORD
```

---

## 🖥️ Запланований функціонал

Через графічний інтерфейс застосунок забезпечуватиме повний **CRUD** для таблиць `movie`, `viewer`, `rating`:

| Операція | Опис |
|---|---|
| **Create** | Форма додавання нового запису у таблицю |
| **Read / Search** | Пошук за мінімум **2 різними критеріями** (жанр, країна, рік, глядач) |
| **Update** | Редагування існуючого запису |
| **Delete** | Видалення запису з таблиці |

### Додатковий функціонал

- Перелік фільмів, оцінених конкретним глядачем
- Жанрові уподобання глядача
- Середній рейтинг фільмів за країною і діапазоном років
- Фільтрація за жанром, країною, роком

---

## 🎨 Запланований GUI

Кожен екран застосунку повинен містити:

- **Форму введення** — для додавання та оновлення запису (`TextField`, `ComboBox`)
- **Таблицю результатів** — `TableView` для відображення даних
- **Блок пошуку** — поля критеріїв + кнопка «Пошук»
- **Повідомлення користувачу** — валідація введення, помилки підключення до БД, «нічого не знайдено»

---

## 🗄️ Схема бази даних

БД складається з **5 таблиць**. Довідники `genre` і `country` з рядковими PK унеможливлюють присвоєння фільму неіснуючого жанру чи країни. Таблиця `rating` — зв'язок M:N між `movie` і `viewer` зі складеним PK.

```
┌──────────────────┐          ┌──────────────────┐
│      genre       │          │     country      │
│──────────────────│          │──────────────────│
│ genre_name  PK   │          │ country_name  PK │
│ varchar(100)     │          │ varchar(100)     │
└────────┬─────────┘          │ exists  boolean  │
         │ in                 └────────┬─────────┘
         │                            │ from
         ▼                            ▼
┌──────────────────────────────────────────────────┐
│                      movie                       │
│──────────────────────────────────────────────────│
│ movie_id      int           PK                   │
│ genre         varchar(100)  FK → genre.genre_name│
│ country       varchar(100)  FK → country.country │
│ movie_name    varchar(100)  NOT NULL              │
│ release_year  int           NOT NULL              │
└────────────────────────┬─────────────────────────┘
                         │ has
                         ▼
┌───────────────────────────┐    ┌──────────────────────┐
│          rating           │    │        viewer        │
│───────────────────────────│    │──────────────────────│
│ viewer_id   int  PK, FK ──┼────│ viewer_id  int    PK │
│ movie_id    int  PK, FK   │    │ name  varchar(100)   │
│ rating      int           │    │       NOT NULL       │
└───────────────────────────┘    └──────────────────────┘
```

### Таблиці

| Таблиця | Тип | Призначення |
|---|---|---|
| `genre` | Довідник | Жанри фільмів (рядковий PK) |
| `country` | Довідник | Країни (рядковий PK + exists) |
| `movie` | Основна | Каталог фільмів |
| `viewer` | Основна | Глядачі системи |
| `rating` | Зв'язок M:N | Оцінки глядачів зі складеним PK |

### JDBC маппінг типів

| SQL тип | Java тип | Метод ResultSet |
|---|---|---|
| `INTEGER` | `int` | `getInt()` |
| `VARCHAR` | `String` | `getString()` |
| `NUMERIC` | `BigDecimal` | `getBigDecimal()` |
| `DATE` | `LocalDate` | `toLocalDate()` |

---

## 📚 Запланована документація (JavaDoc)

- Ключові класи та методи (`Model`, `Controller`, `DAO/Service`) задокументовані через **JavaDoc**
- Згенерована документація розміщена у папці **`docs/`** у корені репозиторію
- GitHub Pages: `https://maikhilton.github.io/Java-Team-Project/` *(після увімкнення)*

### Генерація JavaDoc вручну

```bash
mvn javadoc:javadoc
# Документація з'явиться у target/site/apidocs/
# Скопіювати у docs/
cp -r target/site/apidocs/* docs/
```

### GitHub Actions (можливо)

Файл `.github/workflows/javadoc.yml` — автоматична генерація та деплой JavaDoc при push в `main`.

---

## 📅 Запланований план робіт

| № | Завдання | Відповідальний |
|---|---|---|
| 1 | Репозиторій, Maven-структура, pom.xml (JavaFX + JDBC) | Team Lead |
| 2 | SQL-скрипти: створення таблиць, довідники | Team Lead |
| 3 | Entity-класи: `Movie`, `Viewer`, `Rating` + JavaDoc | Developer 1 |
| 4 | Entity-класи: `Genre`, `Country` | Developer 1 |
| 5 | `db.properties` + `JDBCHelper` — підключення через db.properties | Team Lead |
| 6 | `MovieDao` — CRUD + PreparedStatement + JavaDoc | Team Lead |
| 7 | `ViewerDao`, `RatingDao` — CRUD + PreparedStatement | Team Lead |
| 8 | Бізнес-логіка: рейтинг > 10 голосів, аналітика | Developer 1 |
| 9 | FXML + Controller: головний екран, TableView | Developer 2 |
| 10 | FXML + Controller: форма додавання/редагування, валідація | Developer 2 |
| 11 | Блок пошуку (2+ критерії), повідомлення про помилки | Developer 2 |
| 12 | Тестові дані (SQL INSERT), наповнення БД | Team Lead |
| 13 | Генерація JavaDoc → папка `docs/` | Team Lead |
| 14 | Тестування, виправлення помилок | Всі |
| 15 | Фінальний коміт, оформлення README | Team Lead |

---

## 🌿 Правила роботи з гілками

### Структура гілок

```
main          ← стабільна версія (тільки через Pull Request)
└── develop   ← гілка розробки
    ├── feature/movie-entity
    ├── feature/rating-dao
    ├── feature/javafx-main-screen
    └── fix/rating-zero-check
```

### Префікси гілок

| Префікс | Призначення | Приклад |
|---|---|---|
| `feature/` | Новий функціонал | `feature/movie-crud` |
| `fix/` | Виправлення помилки | `fix/rating-null-check` |
| `refactor/` | Рефакторинг | `refactor/dao-layer` |
| `docs/` | Документація | `docs/javadoc-deploy` |

### Правила злиття (Merge Policy)

1. **Заборонено** пушити напряму в `main`
2. Всі зміни — через **Pull Request** у гілку `develop`
3. PR повинен мати описову назву та короткий опис змін
4. Перед злиттям в `main` — код перевіряє **Team Lead**
5. Гілка `feature/*` видаляється після успішного злиття
6. Конфлікти вирішує **автор гілки** до створення PR

```
feature/xxx  →  Pull Request  →  develop  →  PR (Team Lead)  →  main
```

---

## ✍️ Правила комітів

### Формат

```
<тип>(<область>): <короткий опис англійською>
```

### Типи комітів

| Тип | Коли використовувати |
|---|---|
| `feat` | Новий функціонал |
| `fix` | Виправлення помилки |
| `docs` | Зміни в документації / JavaDoc |
| `refactor` | Рефакторинг без зміни поведінки |
| `test` | Тестові дані або тести |
| `chore` | Налаштування проєкту, залежності |

### Приклади

```bash
feat(entity): add Movie class with fields and JavaDoc
feat(dao): implement MovieDao CRUD with PreparedStatement
feat(javafx): add TableView and search block on main screen
feat(javafx): add validation and error messages on form
fix(rating): return 0 when voter count is less than 10
docs(javadoc): generate docs and add to docs/ folder
chore(pom): add JavaFX SDK and PostgreSQL JDBC dependency
refactor(dao): extract DB connection to JDBCHelper class
```

### Вимоги

- Опис — **англійською мовою**
- Один коміт = **одна логічна зміна**
- Заборонені коміти типу `fix`, `update`, `changes` без пояснення

---

## 🚀 Як запустити проєкт

### 1. Клонувати репозиторій

```bash
git clone https://github.com/MaikHilton/Java-Team-Project.git
cd Java-Team-Project
```

### 2. Створити базу даних PostgreSQL

```sql
CREATE DATABASE kinobase;
```

### 3. Виконати SQL-скрипти

```bash
psql -U postgres -d kinobase -f sql/01_create_tables.sql
psql -U postgres -d kinobase -f sql/02_insert_data.sql
```

### 4. Налаштувати підключення

Відредагувати `src/main/resources/db.properties`:

```properties
db.url=jdbc:postgresql://localhost:5432/kinobase
db.user=postgres
db.password=YOUR_PASSWORD
```

### 5. Зібрати та запустити

```bash
mvn clean compile
mvn javafx:run
```
