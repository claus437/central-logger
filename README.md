Central Logger Api

Developed with JDK 17 and Maven 3.8.6.

The application is built on spring boot and is started either with the
maven spring boot plugin or using java -jar with the generated artifact.

The server listens by default on port 8080 and the rest endpoint is 
mapped to /log.

The KafkaLogger expects by default a kafka server running on 
localhost:9092 with a topic called "logs".

The FileLogger will by default write to out.log in the current directory.

New loggers can be added simple by creating a new class implementing the 
CentralLogger interface.

All fields in CentralLoggerEntry are strings, even though id's are usually
numbers, but you never know :)

For the performance test, 10 to 100 users things gets a bit slower but that 
is to expect, especially because the client and server is running on the 
same computer.

Increased to 1000 clients, a lot of requests failed, however this is duo to
tomcat is set to only allow 200 simultaneously connections (can be changed
with server.tomcat.max-threads)





