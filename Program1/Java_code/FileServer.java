import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class FileServer {
	static Thread server;
	static String path_Server;
	static String serverpath="C:\\Users\\LarrySu\\eclipse-workspace\\Program_Assignment1\\src\\Server\\";
	public static void main(String args[]) throws Exception {
				//the first one as port number
				//the second one as directory path
				// If port number is not present, default it to 8000
				// If directory path is not present, show error
				ServerSocket welcomeSocket=null;
				int id = 1;
				boolean state=false;
				if(args.length == 0) {
					System.out.println("Please enter the server directory address as first argument while running from command line.");
				}else if(args.length ==1) {
					if(args[0].equalsIgnoreCase("start")) {
						welcomeSocket = new ServerSocket(8000);
						state=true;
					}else {
						System.out.println("Wrong Type");
					}
				}else {
					if(args[0].equalsIgnoreCase("start")&&args.length>2) {
						welcomeSocket = new ServerSocket(Integer.parseInt(args[1]));
						serverpath=args[2];
						state=true;
					}
					else {
						welcomeSocket = new ServerSocket(Integer.parseInt(args[1]));
						state=true;
					}

				}
					while (state) {
						System.out.println("Server started...");
						System.out.println("Waiting for connections...");
						Socket connectionSocket = welcomeSocket.accept();
						System.out.println("Client with ID " + id + " connected from " + connectionSocket.getInetAddress().getHostName() + "...");
						server = new ServerThreads(connectionSocket, id,serverpath); 
						id++;
						server.start();
					}
				
	}
	
}

class ServerThreads extends Thread {
	private static final int buffersize = 8192;
	int n;
	int m;
	String name, f, ch, fileData,check;
	String filename;
	Socket connectionSocket;
	int counter;
	String dirName;
	Thread thread1;
	private  static boolean running = true;
	public ServerThreads(Socket socket, int count, String directory) {
		//Make a connection socket between Server and Client
		connectionSocket = socket;
		//counter for counting the number of clients
		counter = count;
		// set directory Name that's entered by the user
		dirName = directory;
	}

	public void run() {
		
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			InputStream inFromClient = connectionSocket.getInputStream();
			OutputStream output = connectionSocket.getOutputStream();
			ObjectOutputStream oout = new ObjectOutputStream(output);
			oout.writeObject("Server says Hi!");
			
			File ff = new File(dirName);
			ArrayList<String> names = new ArrayList<String>(Arrays.asList(ff.list()));
			oout.writeObject(String.valueOf(names.size()));
			boolean number=true;
			for(String name: names) {
				oout.writeObject(name);
			}
			//System.out.println(name);
			name = in.readLine();
			System.out.println(name);
			String ch1;
			ch1 = name.substring(2, 8);
			ch = name.substring(0, 1);
			
			//dis.close();
			


			if (ch.equals("*")) {
				n = name.lastIndexOf("*");
				filename = name.substring(1, n);
				FileInputStream file = null;
				BufferedInputStream bis = null;
				boolean fileExists = true;
				System.out.println("Request to download file " + filename + " recieved from " + connectionSocket.getInetAddress().getHostName() + "...");
				filename = dirName + filename;
				//System.out.println(filename);
				try {
					file = new FileInputStream(filename);
					bis = new BufferedInputStream(file);
				} 
				catch (FileNotFoundException excep) {
					fileExists = false;
					System.out.println("FileNotFoundException:" + excep.getMessage());
				}
				if (fileExists) {
					oout = new ObjectOutputStream(output);
					oout.writeObject("Success");
					//System.out.println("Download 50%");
					sendBytes(bis, output);
					System.out.println("Completed");
					bis.close();
					file.close();
					oout.close();
					output.close();
				}
				else {
					oout = new ObjectOutputStream(output);
					oout.writeObject("FileNotFound");
					bis.close();
					file.close();
					oout.close();
					output.close();
				}
				//////////////////////////////////////
			}
			else if(ch1.equals("closed")){
				System.out.println("Server Closed");
				running = false;
				System.exit(0);
			}else{
				try {
					boolean complete = true;
					System.out.println("Request to upload file " + name + " recieved from " + connectionSocket.getInetAddress().getHostName() + "...");
					File directory = new File(dirName);
					if (!directory.exists()) {
						System.out.println("Directory made");
						directory.mkdir();
					}
					long BytesReadSoFar = 0;
					int size = 8192;
					byte[] data = new byte[size];
					File fc = new File(directory, name);
					FileOutputStream fileOut = new FileOutputStream(fc);
					DataOutputStream dataOut = new DataOutputStream(fileOut);
					while ((m = inFromClient.read(data))!=-1) {
						System.out.println(m);
						fileOut.write(data, 0, m);
						//dataOut.write(data, 0, m);
						BytesReadSoFar += m;
						
						
					}
					//dataOut.flush();
					System.out.println("Completed");
					dataOut.close();
					fileOut.close();
				} catch (Exception exc) {
					System.out.println(exc.getMessage());
				}
			}
			
		} 
		catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

	




	private static void sendBytes(BufferedInputStream in , OutputStream out) throws Exception {

		byte[] buffer = new byte[buffersize];
		
		long BytesReadSoFar = 0;

		int bytesRead = -1;
		while ((bytesRead = in.read(buffer)) != -1) {
			
			out.write(buffer, 0, bytesRead);
			BytesReadSoFar += bytesRead;
			System.out.println(bytesRead);
		}
		out.flush();
	}
}

