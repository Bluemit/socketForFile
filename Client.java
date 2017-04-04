import java.io.*;
import java.net.*;
import java.util.*;
import sun.misc.*;

public class Client {
    public static void main(String []args) {
		if (args.length < 2) {
			System.out.println("you don't set host and port");
			return;
		}
		String host = args[0];
		int port = Integer.valueOf(args[1]);
		new WriteData(host, port).start();
    }
}

class WriteData extends Thread {
    String threadhost;
	int threadport;

	WriteData(String host, int port) {
		threadhost = host;
		threadport = port;
	}

	@Override
    public void run() {
        while (true) {
            Socket mSocket = new Socket();
            try {
                mSocket.setSoTimeout(50000);
				mSocket.connect(new InetSocketAddress(threadhost, threadport),1000);
                PrintWriter os = new PrintWriter(mSocket.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(System.in));


                String line = in.readLine();
                while (!line.equals("exit")) {
					if(line.equals("ask")){
						new ReadData(mSocket).start();
					}
					os = new PrintWriter(mSocket.getOutputStream());
                    os.println(line);
                    os.flush();
					sleep(5000);
	
					mSocket = new Socket();
					mSocket.setSoTimeout(50000);
					mSocket.connect(new InetSocketAddress(threadhost, threadport),1000);
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
                Thread.sleep(3000);
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
				FileOutputStream fos = new FileOutputStream(file);
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
				sleep(5000);
		}
		catch (Exception e) {
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

        }
		catch (IOException e) {
            e.printStackTrace();
        }
		finally{
            try {
                bin.close();
                fout.close();
                bout.close();
            }
			catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
