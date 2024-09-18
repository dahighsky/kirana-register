# Kirana Register

Kirana Register is a Spring Boot application for managing transactions and generating reports for a local store (kirana).
The API documentation is available as a GitHub Pages site. You can access it [here](https://dahighsky.github.io/kirana-register/).

## Table of Contents
- [Project Structure](#project-structure)
- [Technologies Used](#technologies-used)
- [Features](#features)
- [Setup and Installation](#setup-and-installation)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Contributing](#contributing)
- [License](#license)

## Project Structure

```
kirana-register/
├── .mvn/wrapper/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── kirana_register/
│   │   │               ├── client/
│   │   │               ├── config/
│   │   │               ├── controller/
│   │   │               ├── dto/
│   │   │               │   ├── request/
│   │   │               │   └── response/
│   │   │               ├── model/
│   │   │               ├── repository/
│   │   │               ├── security/
│   │   │               ├── service/
│   │   │               │   └── impl/
│   │   │               └── utility/
│   │   └── resources/
│   │       ├── static/
│   │       └── templates/
│   └── test/
│       └── java/
│           └── com/
│               └── example/
│                   └── kirana_register/
└── target/
```

## Technologies Used

- Java 21
- Spring Boot 3.3.3
- Spring Security
- Spring Data MongoDB
- Lombok
- JWT (JSON Web Tokens)
- Resilience4j
- Swagger (SpringDoc OpenAPI)
- Maven

## Features

- User authentication and authorization
- Transaction management
- Report generation (weekly, monthly, yearly)
- API rate limiting and circuit breaking
- OpenAPI documentation

## Setup and Installation

1. Ensure you have Java 21 and Maven installed on your system.
2. Clone the repository:
   ```
   git clone https://github.com/yourusername/kirana-register.git
   ```
3. Navigate to the project directory:
   ```
   cd kirana-register
   ```
4. Configure the application (see [Configuration](#configuration) section).
5. Build the project:
   ```
   mvn clean install
   ```
6. Run the application:
   ```
   mvn spring-boot:run
   ```

The application will start running on `http://localhost:8080`.

## Configuration

The project includes an `example.application.properties` file. To configure the application:

1. Rename `example.application.properties` to `application.properties`.
2. Open `application.properties` and add the required credentials and configuration settings.
3. If you need the actual credentials or have questions about the configuration, please contact the project author.

**Note:** Never commit your `application.properties` file with sensitive information to the repository.

## API Documentation

The API documentation is available as a GitHub Pages site. You can access it [here](https://dahighsky.github.io/kirana-register/).

For local access to the Swagger UI, run the application and navigate to:
```
http://localhost:8080/swagger-ui.html
```