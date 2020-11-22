# Jooq issue

Needs a postgres 11 instance running with a database called `app`.

### Reproduce

```bash
./gradlew build
./gradlew app:bootRun
```

Go to http://localhost:8080/playground

And copy this:
```graphql
query {
    getUserPersonalRoles {
        id
        name
        elements {
            id
            name
        }
    }
}
```

This should throw the exception.

Search for the [TODO](./app/src/main/kotlin/com/example/app/dao/UserDao.kt) to see the lines causing the error.
