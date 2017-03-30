import java.text.SimpleDateFormat;
import java.io.*;
import java.net.*;
import java.util.*;
import sun.misc.*;

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
                BufferedReader is = new BufferedReader(new InputStreamReader(mmSocket.getInputStream()));

                String line = is.readLine();
                while (!line.equals("exit")) {
                    System.out.println("Client: " + line);
                    line = is.readLine();
                }
            } catch (Exception e) {
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
        while (true) {
            try {
                PrintWriter os = new PrintWriter(mmSocket.getOutputStream());
                // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                // os.println(df.format(new Date()));
                // os.flush();

                File file = new File("1.pdf");
                String result = getPDFBinary(file);
                System.out.println(result);
                os.println(result);
                os.flush();
                Thread.sleep(10000);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        System.out.println("Exit Server WriteData");
    }

    static String getPDFBinary(File file) {
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


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                fin.close();
                bin.close();

                //baos.close();
                bout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }






}
