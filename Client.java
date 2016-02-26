import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
        public static void main(String []args) {
                new AcceptData().start();
        }
}

class AcceptData extends Thread{            
    @Override
    public void run() {
        Socket mSocket = new Socket();
        try {
            mSocket.connect(new InetSocketAddress("localhost",30000),1000);
        }   catch (IOException e) {
            e.printStackTrace();
        }
        try {
            PrintWriter os = new PrintWriter(mSocket.getOutputStream());
            BufferedReader is = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            String line = in.readLine();
            while (!line.equals("bye")) {
            	os.println(line);
            	os.flush();
            	System.out.println("Client: " + line);
            	System.out.println("Server: " + is.readLine());
            	line = in.readLine();
            }
            os.close();
            is.close();
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    } 
}