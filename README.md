# Description

Twitch bot using the Twitch4J dependency.

# How to build

1. Clone the repository
2. Run the following command:

```shell
mvn clean install
```

# How to run

you have to define the following environment variables:

- DATABASE_PLATFORM (default: org.hibernate.dialect.PostgreSQLDialect)
- DATABASE_DRIVER (default: org.postgresql.Driver)
- DATABASE_URL
- DATABASE_USERNAME
- DATABASE_PASSWORD
- CLIENT_ID (https://dev.twitch.tv/console)
- TWITCH_OAUTH_RETRY (default: 10)
- TWITCH_OAUTH_TIMEOUT in seconds (default: 6)
- SAINT_URL
- TWITCH_SCHEDULED_CRON

Run the following command:

```shell
mvn -pl bot-runners org.springframework.boot:spring-boot-maven-plugin:run
```