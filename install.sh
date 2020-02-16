#!/bin/bash

echo "Stop and remove containers"
echo "=========================="
docker stop mongodb backend1 backend2 gateway
docker rm mongodb backend1 backend2 gateway

echo ""
echo "Build backend image"
echo "==================="
cd backend
mvn clean install
docker build -t urlshortener/backend .

cd ..

echo ""
echo "Build gateway image"
echo "==================="
cd gateway
mvn clean install
docker build -t urlshortener/gateway .

echo ""
echo "Run Docker containers"
echo "====================="
docker pull mongo
docker run -d --hostname mongodbhost --name mongodb mongo
docker run -d --hostname backendhost1 --name backend1 --link mongodb urlshortener/backend
docker run -d --hostname backendhost2 --name backend2 --link mongodb urlshortener/backend
docker run -d -p 9000:8080 --name gateway --link backend1 --link backend2 urlshortener/gateway

docker ps -a

echo ""
echo "SUCCESS"
