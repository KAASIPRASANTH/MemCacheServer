import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.time.*;


public class Server {

    private static Map<String, Value> HashTable;
	private static final Lock lock = new ReentrantLock();
	
    public Server() {
        HashTable = new HashMap<>();
    }

    public static void setOperation(String[] data, Socket client) throws IOException {
        int len = data.length;
        String key = data[1];
        if (len == 6) {
            Value value = new Value(data[1], Integer.parseInt(data[2]), Integer.parseInt(data[3]), Integer.parseInt(data[4]), data[5]);
            
			while(lock.tryLock()){
				HashTable.put(key, value);
				lock.unlock();
				break;
			}

            PrintWriter pr = new PrintWriter(client.getOutputStream());
            String response = "STORED";
            pr.println(response);
            pr.flush();

            pr.close();
        } else {
            Value value = new Value(data[1], Integer.parseInt(data[2]), Integer.parseInt(data[3]), Integer.parseInt(data[4]), data[6]);
            while(lock.tryLock()){
				HashTable.put(key, value);
				lock.unlock();
				break;
			}
        }

        client.close();
    }
	
	public static boolean hasExpired(String key) {
		Value value = HashTable.get(key);
		
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - value.creationTime;
		
		System.out.println("currentTime = "+ currentTime);
		System.out.println("CreationTime = "+ value.creationTime);
		System.out.println("elapsedTime = "+elapsedTime);
        return elapsedTime > value.exptime;
    }
	
	
    public static void getOperation(String[] data, Socket client) throws IOException {
        if (HashTable.get(data[1]) == null) {
            PrintWriter pr = new PrintWriter(client.getOutputStream());
            String response = "END";
            pr.println(response);
            pr.flush();

            pr.close();
        } else if(hasExpired(data[1])){
			System.out.println("Expired");
			HashTable.remove(data[1]);
			PrintWriter pr = new PrintWriter(client.getOutputStream());
            String response = "END";
            pr.println(response);
            pr.flush();
		} else {
			Value value = null;
			while(lock.tryLock()){
				value = HashTable.get(data[1]);
				lock.unlock();
				break;
			}
            PrintWriter pr = new PrintWriter(client.getOutputStream());
            String response = "VALUE " + (value.data) + " " + (value.flag) + " " + (value.byte_count);
            pr.println(response);
            pr.flush();

            pr.close();
        }

        client.close();
    }

    static class ClientHandler implements Runnable {
        private final Socket client;

        public ClientHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                handleClient();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void handleClient() throws IOException {
            InputStreamReader in = new InputStreamReader(client.getInputStream());
            BufferedReader bf = new BufferedReader(in);

            String data = bf.readLine();
            String[] arr = data.split("[\\s]+");

            if (arr[0].equals("set")) {
                setOperation(arr, client);
            } else if (arr[0].equals("get")) {
                getOperation(arr, client);
            }

            // Close resources
            bf.close();
            in.close();
            client.close();
        }
    }

    public static void start() throws IOException {
        final int PORT = 11211;
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Server listening on Port = " + PORT);

        while (true) {
            Socket client = server.accept();
            System.out.println("Client Connected");

            // Handle each client in a separate thread
            new Thread(new ClientHandler(client)).start();
        }
    }
}