# Finance Tracker (Spring Modulith)

A simplified, modular monolithic application for personal finance tracking. Built with **Spring Boot 3.2+** and **Spring Modulith**, demonstrating strict package boundaries and event-driven architecture.

## 🏗 Architecture

The project follows a **Modular Monolith** structure, ensuring loose coupling between functional areas. Communication between modules is handled exclusively via **Spring Application Events**.

### Modules

1.  **Account Module** (`com.ajax.finance_tracker.account`)
    *   **Responsibility:** Identity & Authentication (User Registration).
    *   **Key Event:** Publishes `UserRegisteredEvent` upon successful registration.
    *   **Constraints:** No dependencies on other modules.

2.  **Statistics Module** (`com.ajax.finance_tracker.statistics`)
    *   **Responsibility:** The core ledger. Tracks income, expenses, and savings.
    *   **Behavior:** Reacts to `UserRegisteredEvent` to initialize a user's ledger.
    *   **Logic:** Calculates savings (`Income - Expenses`). Handles `YEARLY` vs `MONTHLY` items.

3.  **Notification Module** (`com.ajax.finance_tracker.notification`)
    *   **Responsibility:** User alert preferences.
    *   **Behavior:** Reacts to `UserRegisteredEvent` to create default notification settings.

---

## 🚀 Getting Started

### Prerequisites

*   **Java:** JDK 21 or higher
*   **Maven:** 3.8+ (or use the included `mvnw`)

### Running the Application

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/Aaron-joyce/Finance-Tracker.git
    cd Finance-Tracker
    ```

2.  **Run with Maven:**
    ```bash
    ./mvnw spring-boot:run
    ```

The application will start on port `8080`.

### Database Configuration

By default, the application uses an **in-memory H2 database**. Data is transient and will be lost on restart.
To use **PostgreSQL**, configure `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/finance_tracker
spring.datasource.username=postgres
spring.datasource.password=yourpassword
```

---

## 🔌 API Endpoints

### 1. Account Management

*   **Create User** (Triggers Event)
    *   `POST /accounts/`
    *   **Body:**
        ```json
        {
          "username": "jdoe",
          "password": "securepassword",
          "email": "jdoe@example.com"
        }
        ```

### 2. Statistics (Ledger)

*   **Get Current Ledger**
    *   `GET /statistics/current?accountId={username}`
    *   **Example:** `GET /statistics/current?accountId=jdoe`
    *   **Response:** Returns `Statistic` object with total savings and item list.

*   **Add Expense/Income Item**
    *   `POST /statistics/items?accountId={username}`
    *   **Body:**
        ```json
        {
          "title": "Salary",
          "amount": 5000.00,
          "currency": "USD",
          "period": "MONTHLY",
          "type": "INCOME"
        }
        ```
    *   **Note:** `period` can be `MONTHLY` or `YEARLY`. `type` can be `INCOME` or `EXPENSE`.

### 3. Notifications

*   **Get Settings**
    *   `GET /notifications/settings?accountName={username}`
    *   **Example:** `GET /notifications/settings?accountName=jdoe`
    *   **Response:** JSON map of notification preferences (e.g., "Weekly Backup": "Active").

---

## 🛠 Access & Tools

*   **Base URL:** `http://localhost:8080`
*   **H2 Console (Dev Database):** `http://localhost:8080/h2-console`
    *   **JDBC URL:** `jdbc:h2:mem:testdb` (Check `application.properties` if different)
    *   **User:** `sa`
    *   **Password:** (Empty or referring to properties)

