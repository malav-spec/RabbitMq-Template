package com.test.queue.RabbitMQ;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * Class to read from a RabbitMQ queue
 */
public class RabbitMQHelper {
	private Connection connection;
    private Channel channel;
    private ConnectionFactory factory;
    
    /**
     * Get the connection to the RabbitMQ server
     */
	public void getRabbitMQConnection(String host) {
		this.factory = new ConnectionFactory();
        this.factory.setHost(host);
        
        try {
			this.connection = factory.newConnection();
			this.channel = connection.createChannel();
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		} 

	}
	
	/**
	 * Function can be changed according to the requirement
	 * @param message
	 * 			message to be parsed
	 * 
	 */
	public void parseMessage(String message, String encoding) {
		
    	/*
    	 * Covnert the message from queue to bytes encoded with CP1047
    	 */
    	byte[] messageBytes = message.getBytes(Charset.forName(encoding));

    	String hex = getHexFromBytesWithFormat(messageBytes);
    	System.out.println(hex);
	}
	
	/**
	 * Helper method to get the hex value from byte array using BigDecimal
	 * @param bytes
	 * 			byte array that needs to be converted
	 * @return String 
	 * 
	 */
	public  String getHexFromBytes(byte[] bytes) {
		return new BigInteger(1, bytes).toString(16);
	}
	
	/**
	 * Helper method to get the hex value from byte array
	 * @param bytes
	 * 			byte array that needs to be converted
	 * @return String 
	 * 
	 */
	public  String getHexFromBytesWithFormat(byte[] bytes) {
		String hex = "";
		for (byte i : bytes) {
		    hex += String.format("%02X", i);
		}
		return hex; 
	}
	
	/**
	 * Helper method to print bytes
	 * @param bytes
	 * 			byte array to be printed to console log
	 * 
	 */
	public  void printBytes(byte[] bytes) {
		for (byte b : bytes) {
			System.out.print(" " + b);
		}
		System.out.println();
	}
	
	public void sendData(String data, String queue) {
		
            System.out.println("Creating queue: " + queue);
            try {
				channel.queueDeclare(queue, false, false, false, null);
			} catch (IOException e) {
				e.printStackTrace();
			}
        
		try {
			this.channel.basicPublish("", queue , null, data.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		

	}

	public void receiveData(String queue, String encoding) {
		
        System.out.println("Waiting for messages");
        
        try {
			channel.queueDeclare(queue, false, false, false, null);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        /*
         * Creating a consumer
         */
        DefaultConsumer consumer = new DefaultConsumer(this.channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                String message = "";
				try {
					message = new String(body, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
                System.out.println("Received message: '" + message + "'");
                parseMessage(message, encoding);
            }
        };
        
        /*
         * Consume message from the queue
         */
        try {
			this.channel.basicConsume(queue, true, consumer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]) {
		RabbitMQHelper rabbitMqHelper = new RabbitMQHelper();
		
		/*
		 *  Establishing connection to RabbitMQ server
		 */
		rabbitMqHelper.getRabbitMQConnection("localhost");
		
		/*
		 * Send data to a queue
		 */
		String sendMsg = "Hello World!";
		rabbitMqHelper.sendData(sendMsg, "TEST");
		
		/*
		 * Receive data from specific queue, specify the encoding
		 */
		rabbitMqHelper.receiveData("TEST", "UTF-16");
        
	}

}
