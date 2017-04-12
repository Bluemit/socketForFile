import java.text.SimpleDateFormat;
import java.io.*;
import java.net.*;
import java.util.*;
import sun.misc.*;

public class Server {
    public static void main(String args[]) {
        int port = 18900;
		if (args.length < 1){
			System.out.println("Notice: you don't set port");
			System.out.println("we will use default value");
			System.out.println("port: 18900");
		}
		else {
			port = Integer.valueOf(args[0]);
		}
		new AcceptThread(port).start();
		System.out.println("server starts at port "+ port);
    }
}

class AcceptThread extends Thread {
    private ServerSocket mServerSocket;
    private Socket mSocket;
    public AcceptThread(int port) {
        try {
            mServerSocket = new ServerSocket(port);
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
                new ReadThread(mSocket).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

class ReadThread extends Thread {
    private Socket mmSocket;

    ReadThread(Socket i) {
        mmSocket = i;
    }

    public void run() {

            try {
                BufferedReader is = new BufferedReader(new InputStreamReader(mmSocket.getInputStream()));

                String line = is.readLine();
                while (true){
					if (line.equals("ask")) {
						System.out.println("Client: " + line);
						new WriteThread(mmSocket).start();
					}
					else if(line.equals("exit")){
						break;
					}
					line = is.readLine();
					while(line == null){
						sleep(1000);
						line = is.readLine();
					}
				}
            }
			catch (Exception e) {
				e.printStackTrace();
            }
            finally {
				System.out.println("Exit Server ReadData");
                try {
					mmSocket.close();
				}
                catch (IOException e) {
					e.printStackTrace();
				}
            }
    }
}

class WriteThread extends Thread {
    private Socket mmSocket;
    static BASE64Encoder encoder = new sun.misc.BASE64Encoder();

    WriteThread(Socket i) {
        mmSocket = i;
    }

    public void run() {
            try {
                File file = new File("source.pdf");
                String result = getBinary(file);
                System.out.println("start send file...");
				PrintWriter os = new PrintWriter(mmSocket.getOutputStream());
                os.println(result);
                os.flush();
                System.out.println("finish send file!");
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    static String getBinary(File file) {
        FileInputStream fin =null;
        BufferedInputStream bin =null;
        ByteArrayOutputStream baos = null;
        BufferedOutputStream bout =null;
        try {

            fin = new FileInputStream(file);

            bin = new BufferedInputStream(fin);
            
            baos = new ByteArrayOutputStream();

            bout = new BufferedOutputStream(baos);
            byte[] buffer = new byte[1024];
            int len = bin.read(buffer);
            while(len != -1){
                bout.write(buffer, 0, len);
                len = bin.read(buffer);
            }

            bout.flush();
            byte[] bytes = baos.toByteArray();

            return encoder.encodeBuffer(bytes).trim();


        }
		catch (FileNotFoundException e) {
            e.printStackTrace();
        }
		catch (IOException e) {
            e.printStackTrace();
        }
		finally{
            try {
                fin.close();
                bin.close();

                baos.close();
                bout.close();
            } 
			catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
