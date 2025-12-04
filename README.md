# Event Management System -- Spring Boot (MongoDB)

A backend application developed with Spring Boot and MongoDB, supporting OTP login, JWT authentication, and CRUD operations.

------------------------------------------------------------------------

## 🚀 Features

-   User & Admin Registration\
-   OTP-based Login\
-   JWT Authentication\
-   MongoDB Database\
-   CRUD APIs for Users/Admins\
-   Clean Layered Architecture (Controller → Service → Repository)

------------------------------------------------------------------------

## 🧰 Prerequisites

Before running the project, ensure you have installed:

### 1. **Java Development Kit (JDK)**

-   Version: **JDK 17 or higher**
-   Download from: https://adoptium.net or https://www.oracle.com/java/

### 2. **Maven**

If Maven is not installed, install it:

-https://maven.apache.org/download.cgi

-   Verify installation:

    mvn -version
    
### 3. **IDE (recommended)**

-   IntelliJ IDEA (recommended)
-   Spring Tools Suite
-   VS Code with Java extensions

------------------------------------------------------------------------

## 📥 Clone the Project

- Command:

    git clone https://github.com/infocodelancer/campus_event_bknd.git
    cd event-backend

------------------------------------------------------------------------

## ▶️ Run the Project

- From terminal:

    mvn spring-boot:run

Or from IDE: - Open project → Run `EventApplication.java`

------------------------------------------------------------------------

## 🔗 API Endpoints

### **Authentication**

  Method   Endpoint                Description
  -------- ----------------------- --------------------
  POST     `/api/users/register`   Register new user
  POST     `/api/users/otp`        Send OTP
  POST     `/api/users/verify`     Verify OTP + Login

### **Admin**

  Method   Endpoint                 Description
  -------- ------------------------ --------------------
  POST     `/api/admins/register`   Register admin
  POST     `/api/admins/otp`        Send OTP
  POST     `/api/admins/verify`     Verify OTP + Login
  
------------------------------------------------------------------------

## 🗂️ Project Structure

    src/
     ├── controller/
     ├── service/
     ├── repository/
     ├── payload/
     ├── security/
     ├── entity/
     └── EventApplication.java

------------------------------------------------------------------------

## 🛠 Build JAR File

- Terminal:

    mvn clean package

JAR file will be generated at:

    target/event-backend.jar

Run:

- Terminal:

    java -jar target/event-backend.jar

------------------------------------------------------------------------

## 🧪 Testing APIs

Use tools like: - Postman(recommended) - Thunder Client - cURL

------------------------------------------------------------------------

## 📄 License

Open-source project --- feel free to use, modify, and improve.
