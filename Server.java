import java.text.SimpleDateFormat;
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
                Runnable r = new ReadThread(mSocket);
                new Thread(r).start();
                new WriteThread(mSocket).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class ReadThread implements Runnable {
    private Socket mmSocket;

    ReadThread(Socket i) {
        mmSocket = i;
    }

    public void run() {
        try {
            try {
                BufferedReader is = new BufferedReader(new InputStreamReader(mmSocket.getInputStream()));
                
                String line = is.readLine();
                while (!line.equals("exit")) {
                    System.out.println("Client: " + line);
                    line = is.readLine();
                }
            } finally {
                mmSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class WriteThread extends Thread {
    private Socket mmSocket;

    WriteThread(Socket i) {
        mmSocket = i;
    }

    public void run() {
        while (true) {
            try {
                PrintWriter os = new PrintWriter(mmSocket.getOutputStream());
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                os.println(df.format(new Date()));
                os.flush();
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}

