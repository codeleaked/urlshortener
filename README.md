# URL shortener
This application takes long URLs and squeezes them into 6 characters to make a link that is easier to share or note down.
It contains two modules:
1. **Gateway**: 
Gateway works as a load balancer. It receives requests from network and send them to the backend instances.
2. **Backend**:
Backend provides two APIs to take a long URL and squeeze it into an id, or take an id and return the URL which was shortened with this id.

![Image description](architecture_2.png)


### Installation
Docker is required to run the application.

The following command will run a MongoDB container, a Gateway container and two Backend containers.     
```
./install.sh
```
The Gateway container should expose the port 9000.

### Usage

#### Shorten an URL
```
curl --location --request POST 'localhost:9000/shorten' \
--header 'Content-Type: text/plain' \
--data-raw 'http://codeleaked.com'
```
This command will return an id which is a string of 6 digits or letters (for example, 4KGGln).

#### Retrieve an URL from an id
The following command should return `http://codeleaked.com`.
```
curl --request GET 'localhost:9000/retrieve/4KGGln'
```
Note: Replace `4KGGln` by the id you obtained from the _Shorten an URL_ section.
