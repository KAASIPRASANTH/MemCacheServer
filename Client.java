import java.io.*;
import java.net.*;
import java.util.*;


class Client {
	
  public static void setOperation(Scanner sc, Socket client)  throws IOException {
	
	System.out.println("Enter an key:");
	String key = sc.next();
	
	System.out.println("Enter an data");
	String value = sc.next();
	
	//sending request to server
	System.out.println("Need reply? type 1) yes or 2) no :");
	String reply = sc.next();
	
	
	PrintWriter pr = new PrintWriter(client.getOutputStream());
	String data = "set " + key + " 100 100 400 ";
	if(reply.equals("no")){
		data += "noreply ";
    }
	data+=value;
	pr.println(data);
	pr.flush();
	
	
	if(reply.equals("yes")){
		//receing response from server
	    InputStreamReader in = new InputStreamReader(client.getInputStream());
	    BufferedReader bf = new BufferedReader(in);
	
	    data = bf.readLine();
	
	    System.out.println("Server sent (send): "+data);
		
		
		in.close();
		bf.close();
	}else{
		System.out.println("sent with no response");
	}
	pr.close();
  }
  
  public static void getOperation(Scanner sc, Socket client)  throws IOException {
	  
	System.out.println("Enter an key");
	String key = sc.next();
	
	//sending request
	PrintWriter pr = new PrintWriter(client.getOutputStream());
	String data = "get " + key;
	pr.println(data);
	pr.flush();
	
	//getting response
	InputStreamReader in = new InputStreamReader(client.getInputStream());
	BufferedReader bf = new BufferedReader(in);
	data = bf.readLine();
	
	System.out.println("Server sent (get): "+data);
	
	pr.close();
	in.close();
	bf.close();
  }
  
  public static void start() throws IOException {
	final int PORT = 11211; 
	Scanner sc = new Scanner(System.in);
	int count = 2;
	
	while(count-->0){
		Socket client = new Socket("localhost",PORT);
		System.out.println("Do you want to 1)set an data or 2)get an data?");
	    String option = sc.next();
	
	    if(option.equals("set")){
			setOperation(sc, client);
	    }else{
		    getOperation(sc, client);
	    }
		client.close();
	}
  }
}

/*

	//sending data to server
	-------------------------
	
	PrintWriter pr = new PrintWriter(client.getOutputStream());
	String data = "Hello Server I'm client";
	pr.println(data);
	pr.flush();
	
	
	//receiving data from the Server	
	---------------------------------
	
	InputStreamReader in = new InputStreamReader(client.getInputStream());
	BufferedReader bf = new BufferedReader(in);
	
	data = bf.readLine();
	
	System.out.println("Server sent : "+data);

*/
