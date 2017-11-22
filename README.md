# Inventory REST API

## Database
### Dockerized postgres

Start container:

```
docker run --name inventory-db \
    -p 5432:5432 \
    -e POSTGRES_USER=root \
    -e POSTGRES_PASSWORD=changeMe \
    -e POSTGRES_DB=inventory \
    -d postgres
```

Connect using psql:
```
docker exec -it inventory-db psql -U root -W inventory
```

### Build application

## Run unit tests

### Start application

### Examples

### Documentation