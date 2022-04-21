# WAREHOUSE STOCK-HANDLER 
# Running the API

It can be started either:
- As a new Springboot Application in IntelliJ, selecting Profile: local, or
- Command line: `mvn spring-boot:run -Dspring.profiles.active=local`

On success: `Tomcat started on port(s): 8080`, will appear in the command line

### Running all the Tests
- Command line: `mvn clean install`

### Swagger API endpoints information
- Once started, navigate to `http://localhost:8080/swagger-ui/`

### Postman
- Collection can be found under: `src/environments/local/StockHandler.postman_collection.json

### Import Files flow
- Update application-local file with the fullpath in the properties: 
`stock-handler.import.products.path` and `stock-handler.import.inventories.path`
- It can be, a classpath file (f.e: `classpath:products.json`) or a file located in the computer (f.e: `/home/user/products.json`)
- Once path is set, just call `/inventories/readfile` or `/products/readfile` endpoints

### Stock Handler How to
- Inventory contains all the articles available. (Please refer to the expected format in Swagger)
- Products contain the items available for sale and the information of which quantity of articles are needed for each one. (Please refer to the expected format in Swagger)
- Before adding products, articles should be already in database. If products contain articles not found in database, it will be treated as inconsistency data.


