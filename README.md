# 🔗 URL Shortener Service (Spring Boot + Redis)

A scalable backend system that converts long URLs into short, shareable links with support for fast redirection, caching, rate limiting, and analytics tracking.

---

##  Features

* 🔗 **Short URL Generation** using Base62 encoding
* ✏️ **Custom Short URLs** (user-defined codes)
* ⚡ **Fast Redirection** with Redis caching
* 🚫 **Rate Limiting** to prevent abuse
* 📊 **Click Analytics** (track usage per link)
* 🏆 **Top URLs API** (most accessed links)
* 🧱 **Layered Architecture** (Controller → Service → Repository)
* 🔒 **Clean API Design using DTOs**

---

## 🧠 How It Works

1. User submits a long URL
2. System generates a unique short code (Base62 or custom)
3. URL is stored in MySQL
4. On access:

   * Redis cache checked first ⚡
   * Fallback to DB if needed
5. Redirect happens instantly
6. Click count is tracked

---

## 🛠 Tech Stack

* **Backend:** Java, Spring Boot
* **Database:** MySQL
* **Caching:** Redis
* **ORM:** Spring Data JPA (Hibernate)
* **Build Tool:** Maven

---

## 📂 Project Structure

```
com.urlshortener.demo
 ├── controller
 ├── service
 ├── repository
 ├── model
 ├── dto
 ├── util
 └── config
```

---

## ⚙️ Setup & Run

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/your-username/url-shortener-springboot.git
cd url-shortener-springboot
```

---

### 2️⃣ Setup MySQL

Create database:

```sql
CREATE DATABASE url_shortener;
```

Update `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/url_shortener
spring.datasource.username=your-username
spring.datasource.password=your-password

spring.jpa.hibernate.ddl-auto=update
```

---

### 3️⃣ Run Redis (Recommended via Docker)

```bash
docker run -d -p 6379:6379 redis
```

Check:

```bash
redis-cli ping
```

Expected:

```
PONG
```

---

### 4️⃣ Run Application

```bash
mvn clean install
mvn spring-boot:run
```

---

## 📡 API Endpoints

### 🔹 Create Short URL

```http
POST /api/shorten
```

**Request:**

```json
{
  "longUrl": "https://google.com"
}
```

**With Custom Code:**

```json
{
  "longUrl": "https://google.com",
  "customCode": "my-link"
}
```

---

### 🔹 Redirect

```http
GET /{shortCode}
```

➡ Automatically redirects to original URL

---

### 🔹 Get Analytics

```http
GET /api/analytics/{shortCode}
```

---

### 🔹 Top URLs

```http
GET /api/top
```

---

## 📊 Example Response (Top URLs)

```json
[
  {
    "shortCode": "abc123",
    "longUrl": "https://google.com",
    "clickCount": 10
  }
]
```

---

## ⚠️ Notes

* Redis is optional but recommended for performance
* If Redis is down, system gracefully falls back to DB
* Rate limiting is IP-based (basic implementation)

---

## 💡 Future Improvements

* User authentication (JWT)
* Dashboard UI
* Expiry customization per link
* Geo-based analytics
* Distributed ID generation

---

## 👨‍💻 Author

**Suhail Khan**

* Java Backend Developer
* Focused on building scalable backend systems

---
