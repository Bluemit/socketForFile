import java.io.*;
import java.net.*;
import java.util.*;
import sun.misc.*;

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
                mSocket.setSoTimeout(5000);
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
                System.out.println("Exit Client WriteData");
                break;
            } catch (Exception e) {
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
  static BASE64Decoder decoder = new sun.misc.BASE64Decoder();

	ReadData(Socket i) {
		mSocket = i;
	}

	public void run() {
		try {
			BufferedReader is = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
			// String line = is.readLine();
      File file = new File("2.pdf");
      FileOutputStream fos =
                  new FileOutputStream(file);
      char [] allChar = new char[20000000];
      int len = is.read(allChar);
      System.out.println(len + " chars!");
      String allLine = new String (allChar, 0, len);
      System.out.println(allLine.length() + " long strings!");
      int cnt = 0;

      base64StringToPDF(allLine, file);
      System.out.println("write ok");
      fos.close();
			is.close();
		} catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Exit Client ReadData");
        }
	}

  static void base64StringToPDF(String base64sString, File file){
        BufferedInputStream bin = null;
        FileOutputStream fout = null;
        BufferedOutputStream bout = null;
        try {

            byte[] bytes = decoder.decodeBuffer(base64sString);

            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);

            bin = new BufferedInputStream(bais);

            fout  = new FileOutputStream(file);

            bout = new BufferedOutputStream(fout);

            byte[] buffers = new byte[1024];
            int len = bin.read(buffers);
            while(len != -1){
                bout.write(buffers, 0, len);
                len = bin.read(buffers);
            }

            bout.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                bin.close();
                fout.close();
                bout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
