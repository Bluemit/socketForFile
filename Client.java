import java.io.*;
import java.net.*;
import java.util.*;
import sun.misc.*;

public class Client {
    public static void main(String []args) {
		System.out.println("Client starts.");
		new WriteData().start();
    }
}

class WriteData extends Thread {
    String threadhost;
	int threadport;

	WriteData() {
	}

	@Override
    public void run() {
        while (true) {
            Socket mSocket = null;
            try {
                PrintWriter os = null;
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

                String line = in.readLine();
				String[] lineargs = line.split(" ");
                while (!lineargs[0].equals("exit")) {
					if(lineargs[0].equals("ask")){
						if(lineargs.length < 4) {
							System.out.println("args are not enough!");
							line = in.readLine();
							continue;
						}
						mSocket = new Socket();
						mSocket.setSoTimeout(50000);
						mSocket.connect(new InetSocketAddress(lineargs[1], Integer.valueOf(lineargs[2])), 1000);
						new ReadData(mSocket, lineargs[3]).start();
						os = new PrintWriter(mSocket.getOutputStream());
						os.println(lineargs[0]);
						os.flush();
					}
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
	private String mFilepath;
	static BASE64Decoder decoder = new sun.misc.BASE64Decoder();

	ReadData(Socket i, String s) {
		mSocket = i;
		mFilepath = s;
	}

	public void run() {
		
		try {
				BufferedReader is = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
				
				File file = new File(mFilepath);
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
