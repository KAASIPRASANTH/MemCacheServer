import java.io.*;

class Main {
  public static void main(String[] args) throws IOException {
    System.out.println("main started");

    Server server = new Server();
    server.start();
  }
}