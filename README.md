#### REST Api for money transfers between predefined accounts

```
get balance
curl -i -X GET http://localhost:9000/api/v1/accounts/${id}/balance

create transfer
curl -i -X POST -H "Content-Type: application/json" http://localhost:9000/api/v1/transfers -d '{"from": ${id1},"to": ${id2}, "amount": 100}'

get transfer
curl -i -X GET http://localhost:9000/api/v1/transfers/${id}

execute transfer
curl -i -X PUT http://localhost:9000/api/v1/transfers/${id}
```

#### Predefined accounts

```
Account(id = 1, amount = 740))
Account(id = 2, amount = 350))
Account(id = 3, amount = 740))
Account(id = 4, amount = 350))
Account(id = 5, amount = Int.MaxValue))
```
