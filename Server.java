
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    public static void main(String args[]) {
        new AcceptThread().start();
    }
}

class AcceptThread extends Thread {
    private ServerSocket mServerSocket;
    private Socket mSocket;
    public AcceptThread() {
        try {
            mServerSocket = new ServerSocket(30000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        int i = 0;
        while (true) {
            mSocket = null;
            try {
                mSocket = mServerSocket.accept();
                System.out.println("Thread " + ++i);
                Runnable r = new ConnectThread(mSocket);
                new Thread(r).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class ConnectThread implements Runnable {
    private Socket mmSocket;

    ConnectThread(Socket i) {
        mmSocket = i;
    }

    public void run() {
        try {
            try {
                BufferedReader is = new BufferedReader(new InputStreamReader(mmSocket.getInputStream()));
                PrintWriter os = new PrintWriter(mmSocket.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

                System.out.println("Client: " + is.readLine());
                String line = in.readLine();
                while (!line.equals("bye")) {
                    os.println(line);
                    os.flush();
                    System.out.println("Server: " + line);
                    System.out.println("Client: " + is.readLine());
                    line = in.readLine();
                }
            } finally {
                mmSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Transfer extends Thread {

}