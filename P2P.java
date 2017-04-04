import java.io.*;
import java.net.*;
import java.util.*;
import sun.misc.*;

public class P2P {
    public static void main(String []args) {
		if (args.length < 1) {
			System.out.println("you don't set your port");
			return;
		}
		int port = Integer.valueOf(args[0]);
		new AcceptThread(port).start();
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
						new ReceiveData(mSocket, lineargs[3]).start();
						os = new PrintWriter(mSocket.getOutputStream());
						os.println(lineargs[0]);
						os.flush();
					}
					sleep(1000);
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

class ReceiveData extends Thread {
	private Socket mSocket;
	private String mFilepath;
	static BASE64Decoder decoder = new sun.misc.BASE64Decoder();

	ReceiveData(Socket i, String s) {
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
				PrintWriter os = new PrintWriter(mmSocket.getOutputStream());

                String line = is.readLine();
                while (true){
					if (line.equals("ask")) {
						System.out.println("Client: " + line);
						new SendThread(mmSocket, os).start();
					}
					line = is.readLine();
					while(line == null){
						sleep(1000);
						System.out.println("line is null");
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

class SendThread extends Thread {
    private Socket mmSocket;
	private PrintWriter mos;
    static BASE64Encoder encoder = new sun.misc.BASE64Encoder();

    SendThread(Socket i, PrintWriter os) {
        mmSocket = i;
		mos = os;    }

    public void run() {
            try {
                // SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                // os.println(df.format(new Date()));
                // os.flush();

                File file = new File("1.pdf");
                String result = getBinary(file);
                System.out.println("start sending file!");
                mos.println(result);
                System.out.println("finish sending file!");
                mos.flush();
                Thread.sleep(5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        System.out.println("Exit Server WriteData");
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
