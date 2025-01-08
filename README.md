# Threads Backend

Threads Backend is a backend service for a social media application built with Spring Boot. It provides APIs for user authentication, post creation, likes, reposts, notifications, and more.

To frontend repo: [Frontend Repository](https://github.com/vietddude/threads-clone-fe)

## Table of Contents

- [Requirements](#requirements)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)
- [Running Tests](#running-tests)
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
git clone https://github.com/yourusername/threadsbe.git
cd threadsbe
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

## API Endpoints

### Authentication

- `POST /auth/google/callback` - Google OAuth callback
- `PUT /auth/setup` - Setup user profile
- `POST /auth/refresh-token` - Refresh JWT token

### Users

- `GET /users/{username}` - Get user profile
- `POST /users/{username}/follow` - Follow/unfollow user
- `GET /users` - Search users

### Posts

- `POST /posts` - Create a post
- `POST /posts/replies` - Reply to a post
- `POST /posts/{id}/repost` - Repost a post
- `POST /posts/{id}/like` - Like/unlike a post
- `GET /posts` - Query posts
- `GET /posts/{id}` - Get a post
- `GET /posts/{id}/nested` - Get nested posts
- `GET /posts/{id}/like-info` - Get like info
- `DELETE /posts/{id}` - Delete a post
- `GET /posts/replies` - Get user replies
- `GET /posts/reposts` - Get user reposts

### Notifications

- `GET /notifications` - Get notifications

## Running Tests

To run the tests, use the following command:

```sh
./gradlew test
```

## Contributing

Contributions are welcome! Please open an issue or submit a pull request.

## License

This project is licensed under the MIT License.
