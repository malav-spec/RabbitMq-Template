# External RabbitMQ Test Application

MQ-TEST is a maven test application that will connect to the rabbitmq and consume messages from a specified queue. It will parse the message from UTF-8 encoding to a specified encoding. 

## RabbitMQHelper
-  It contains the main code that will consume the message and parse the message according to the requirement.
-  **getRabbitMQConnection()**: Establishes connection to RabbitMQ server
    -   ConnectionFactory: Specify the host server
    -   Connection: Get a new connection to the server
    -   Channel: Create a channel with the server
- **basicConsume**: Function to consume message from a specified queue.
```java
rabbitMqHelper.channel.basicConsume(rabbitMqHelper.getQueue_name(), true, consumer);
```
- **DefaultConsumer**: Callback function expected by RabbitMQ API for consuming a message. Contains the logic to be executed after message is received.Message received from RabbitMQ is encoded in UTF-8 format.
```java
        DefaultConsumer consumer = new DefaultConsumer(rabbitMqHelper.channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                String message = "";
				try {
					message = new String(body, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
                System.out.println("Received message: '" + message + "'");
                rabbitMqHelper.parseMessage(message);
            }
        };
```
- **parseMessage()**: Parses the message received. Converts the message to a byte array encoded in a specified encoding.

- **getHexFromBytesWithFormat(byte[])**: Returns a hex string converted from byte array
- **getHexFromBytes(byte[])**: Convert to hex string from byte array using BigInteger
- **printBytes(byte[])**: Prints the bytes of byte array

## Usage

- **Prerequisite**: Run maven install on MQ-TEST to install the required dependencies. 

- Run RabbitMQHelper.java as Java Application. Once started, the application will wait on the queue specified. 
- Put a message on the queue using the RabbitMQ Managment or through another application. 
- The message is then received and converted to a UTF-8 string in consumer callback function. 
```java
message = new String(body, "UTF-8");
```
- Message gets passed to parseMessage() function and then converted to byte array of specified encoding.
```java
byte[] messageBytes = message.getBytes(Charset.forName(encoding"));
```