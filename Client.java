import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
        public static void main(String []args) {
                new WriteData().start();
        }
}

class WriteData extends Thread {            
    @Override
    public void run() {
        while (true) {
            Socket mSocket = new Socket();
            try {
                mSocket.connect(new InetSocketAddress("localhost",30000),1000);
                PrintWriter os = new PrintWriter(mSocket.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

                new ReadData(mSocket).start();

                String line = in.readLine();
                while (!line.equals("exit")) {
                    os.println(line);
                    os.flush();
                    line = in.readLine();
                }
                os.close();
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(10000);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        } 
    } 
}

class ReadData extends Thread {
	private Socket mSocket;

	ReadData(Socket i) {
		mSocket = i;
	}

	public void run() {
		try {
			BufferedReader is = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
			String line = is.readLine();
			while (!line.equals("exit")) {
				System.out.println("Server: " + line);
				line = is.readLine();
			}
			is.close();
		} catch (IOException e) {
            e.printStackTrace();
        }
	}
}