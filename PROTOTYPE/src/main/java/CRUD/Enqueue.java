package CRUD;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Enqueue {

	private ConnectionFactory factory = null;
	private Connection connection = null;
	private Channel channel = null;

	public boolean init() {
		boolean isInitiated = false;
		try {
			factory = new ConnectionFactory();
			factory.setHost("localhost");
			connection = factory.newConnection();
			channel = connection.createChannel();
			isInitiated = true;
		} catch (IOException | TimeoutException e) {
			// TODO Auto-generated catch block
			System.err.println("Error initiating the queue:");
			e.printStackTrace();
		}
		return isInitiated;
	}

	public boolean declareMessageQueue(String queueName) {
		boolean isQueueDeclared = false;
		try {
			channel.queueDeclare(queueName, false, false, false, null);
			isQueueDeclared = true;
		} catch (IOException e) {
			System.err.println("Error Declaring Queue:");
			e.printStackTrace();
		}
		return isQueueDeclared;
	}

	public boolean postMessageToQueue(String queueName, String message) {
		boolean isMessagePosted = false;
		try {
			channel.basicPublish("", queueName, null, message.getBytes());
			isMessagePosted = true;
		} catch (IOException e) {
			System.err.println("Error Writing to Message Queue:");
			e.printStackTrace();
		}
		return isMessagePosted;
	}

	public boolean closeConnection() {
		boolean isClosedSuccesfully = false;
		try {
			channel.close();
			connection.close();
			isClosedSuccesfully = true;
		} catch (IOException | TimeoutException e) {
			// TODO Auto-generated catch block
			System.err.println("Error Closing the queue:");
			e.printStackTrace();
		}
		return isClosedSuccesfully;
	}

	/*public void processQueueInput() {
		Scanner scanner = new Scanner(System.in);
		int userChoice = 0;
		StringBuffer buffer = null;
		String line = null;
		while (true) {
			System.out.print("\t\tMenu:\n\t1. Post file To Queue\n\t2. Quit\n\tChoice:");
			userChoice = scanner.nextInt();
			switch (userChoice) {
			case 1:
				System.out.print("\n\tEnter Filename:");
				try {
					scanner.nextLine();// cleaning the new line char!
					String file = scanner.nextLine();
					System.out.println(file);
					BufferedReader reader = new BufferedReader(new FileReader(file));
					buffer = new StringBuffer();
					while ((line = reader.readLine()) != null) {
						buffer.append(line);
					}
					reader.close();

					// posting to Queue!
					if (postMessageToQueue(QUEUE_NAME, buffer.toString())) {
						System.out.println("posted to Queue!");
					} else {
						System.out.println("posting Failed!");
					}

				} catch (FileNotFoundException e) {
					System.err.println("\n\tFile NOT Found!, " + e.getCause());

				} catch (IOException e) {
					System.err.println("Error Reading the File: " + e.getMessage());
				}
				break;
			case 2:
				System.out.println("bye!");
				System.exit(0);
				
			default:
				System.out.println("\nInvalid!");
			}

		}
	}

	public static void main(String[] args) throws IOException, TimeoutException {
		Enqueue enqueue = new Enqueue();

		if (enqueue.init()) {
			if (enqueue.declareMessageQueue(QUEUE_NAME)) {
				enqueue.processQueueInput();
			}
		}
	}
	*/
}
