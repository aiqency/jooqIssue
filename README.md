# Jooq issue

Needs a postgres 11 instance running with a database called `app`.

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
