#URL shortener
This application takes long URLs and squeezes them into 6 characters to make a link that is easier to share or type.
It contains two modules:
1. Gateway: 
Gateway works as a load balancer. It receives requests from network and send them to the backend instances.
2. Backend:
Backend provides two APIs to take a long URL and squeeze it into an id, or take an id and return the URL which was shortened with this id.

####Compile and run unit tests
```
mvn clean install
```

####Deploy the gateway on port 9000
```
cd gateway
SERVER_PORT=9000 mvn spring-boot:run
```

####Deploy the first backend on port 9001
```
cd backend
SERVER_PORT=9001 mvn spring-boot:run
```

####Deploy the second backend on port 9002
```
cd backend
SERVER_PORT=9002 mvn spring-boot:run
```

####Service usage
#####Shorten an URL
```
curl --location --request POST 'localhost:9000/shorten' \
--header 'Content-Type: text/plain' \
--data-raw 'https://en.wikipedia.org/wiki/URL_shortening'
```
This command will return an id which is a string of 6 digits or letters (for example, 4KGGln).

#####Retrieve an URL from an id
```
curl --request GET 'localhost:9000/retrieve/4KGGln'
```
