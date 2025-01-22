# Threads Backend

Threads Backend is a backend service for a social media application built with Spring Boot. It provides APIs for user authentication, post creation, likes, reposts, notifications, and more.

The service supports login via Google OAuth, enabling seamless and secure user authentication using their Google accounts.

To frontend repo: [Frontend Repository](https://github.com/vietddude/threads-clone-fe)

## Table of Contents

- [Requirements](#requirements)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [Swagger Documentation](#swagger-documentation)
- [Database Migration](#database-migration)
- [Contributing](#contributing)
- [License](#license)

## Requirements

- Java 17
- PostgreSQL
- Redis
- Gradle

## Installation

1. Clone the repository:

```sh
git clone https://github.com/vietddude/threads-clone-be.git
cd threads-clone-be
```

2. Build the project:

```sh
./gradlew build
```

## Configuration

1. Create a `.env` file in the root directory by copying `.env.example`:

```sh
cp .env.example .env
```

2. Update the `.env` file with your configuration values:

```env
ENV=dev
PORT=4000
POSTGRES_URI=jdbc:postgresql://localhost:5432/threads
POSTGRES_USER=your_postgres_user
POSTGRES_PASSWORD=your_postgres_password
JWT_SECRET=your_jwt_secret
JWT_ACCESS_EXPIRES=1800000
JWT_REFRESH_EXPIRES=3600000
REDIS_HOST=localhost
REDIS_PORT=6379
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
GOOGLE_REDIRECT_URI=your_google_redirect_uri
```

## Usage

1. Run the application:

```sh
./gradlew bootRun
```

2. The application will be available at `http://localhost:4000`.

## Swagger Documentation

The API documentation is available via Swagger. Once the application is running, you can access the Swagger UI at:

```
http://localhost:4000/api/swagger-ui/index.html
```

## Database Migration

The database schema is managed using migration files located in the `src/main/resources/db/migration` directory.

To manually apply the migrations, follow these steps:

1. Ensure your PostgreSQL database is running.
2. Use the Flyway CLI or a Flyway plugin for your build tool (Gradle or Maven) to apply migrations.

## Contributing

Contributions are welcome! Please open an issue or submit a pull request.

## License

This project is licensed under the MIT License.
