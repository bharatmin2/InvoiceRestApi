# InvoiceRestApi
backend springboot app for invoice CRUD in ES

# Running the app

git clone the directory and cd into it.
Execute mvn run
```
mvn clean spring-boot:run
```
# Elasticsearch

The application requires Elasticsearch running on the system on default port 9300.

# Service crud operation

Front End app makes a POST call in this particular demo, but it can make GET and DELETE calls as well.

## GET
```
http://localhost:8080/SpringBootRestApi/api/invoice/100
This will retrieve Invoice with Id 100 with XGET call
```

## POST
```http://localhost:8080/SpringBootRestApi/api/invoice/
This will post/save the invoice in ES with XPOST call. Sample Post body
[{
            "name": "Sam6633",
            "email": "sam5@gmail.com",
            "date": 1412924400000,
            "description": "Test5",
            "amount": 5.3,
            "id" : "1",
            "item_id" : "1"
}]
```

## DELETE
```http://localhost:8080/SpringBootRestApi/api/invoice/4
Will delete invoice with id 4 in XDELETE call```
```


