# URL shortener
This application takes long URLs and squeezes them into 6 characters to make a link that is easier to share or type.
It contains two modules:
1. Gateway: 
Gateway works as a load balancer. It receives requests from network and send them to the backend instances.
2. Backend:
Backend provides two APIs to take a long URL and squeeze it into an id, or take an id and return the URL which was shortened with this id.

### Installation
```
./install.sh
```

### Usage

#### Shorten an URL
```
curl --location --request POST 'localhost:9000/shorten' \
--header 'Content-Type: text/plain' \
--data-raw 'http://codeleaked.com'
```
This command will return an id which is a string of 6 digits or letters (for example, 4KGGln).

#### Retrieve an URL from an id
```
curl --request GET 'localhost:9000/retrieve/4KGGln'
```
The above request should return `http://codeleaked.com`.