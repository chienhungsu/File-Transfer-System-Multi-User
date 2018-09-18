import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.ProgressMonitorInputStream;


public class Client {
	private static final int buffersize = 8192;
	private OutputStream outputStream;
	String serverpath1;
	Socket clientSocket;
	InputStream inFromServer;
	OutputStream outToServer;
	BufferedInputStream bis;
	PrintWriter pw;
	String name, file1, path,clientpath;
	String hostAddr="127.0.0.1",file;
	static String upload="upload";
	static String download="download";
	String remove="remove";
	static String options1;
	int portNumber=8000;
	int c;
	private int percentCompleted = 0;
	int size = 8192;
	String[] names = new String[10000];
	int len; // number of files on the server got

	public Client(String options, String host, int port, String dir, String cpath, String fname) {
		// set serverpath1 that's default setting in main
		serverpath1 = dir;
		// set hostAddr that's passed by the user
		hostAddr = host;
		// set portNumber that's passed by the user
		portNumber = port;
		// set option that is upload, download, dir, mkdir, rmdir, remove, shutdown
		options1 = options;
		// set serverpath1 that's default directory or input from user in main
		clientpath = cpath;
		//set file1 that filename or new directory inputed from user
		file1 = fname;
	 
		try {
			
			clientSocket = new Socket(hostAddr, portNumber);
			inFromServer = clientSocket.getInputStream();
			pw = new PrintWriter(clientSocket.getOutputStream(), true);
			outToServer = clientSocket.getOutputStream();
			ObjectInputStream oin = new ObjectInputStream(inFromServer);
			String s = (String) oin.readObject();
			if(options1.equals("shutdown")) {
			}else {
				System.out.println(s);

			}
			len = Integer.parseInt((String) oin.readObject());
			if(options1.equals("dir")||options1.equals("download")||options1.equals("upload")) {
				System.out.println("There are "+len+" files in the serverfile directory.");
			}
			if(options1.equals("dir")) {
				System.out.println("Files in the Server Directory :");
			}
			String[] temp_names = new String[len];
			for(int i = 0; i < len; i++) {
				String filename = (String) oin.readObject();
				if(options1.equals("dir")) {
					//System.out.println("Files in the Server Directory :");
				}else {
					//System.out.println(filename);
				}
				names[i] = filename;
				temp_names[i] = filename;
			}

			if(options1.toString().equals(upload)) {
				//client path, server path for file
				upload(file1,clientpath);
			}else if(options1.toString().equals(download)) {
				// server path for file, client path
				download(file1,clientpath);
			}else if (options1.toString().equals(remove)) {
				remove(serverpath1+file1);
			}else if (options1.toString().equals("dir")) {
				dir(serverpath1);
			}else if (options1.toString().equals("mkdir")) {
				mkdir(serverpath1+file1);
			}else if (options1.toString().equals("rmdir")) {
				rmdir(serverpath1+file1);
			}else if(options1.toString().equals("shutdown")){
				shutdown();
			}else {
				System.out.println("wrong options");
			}
			
				
		} 
		catch (Exception exc) {
			System.out.println("Exception: " + exc.getMessage());
		}
	}	
	private void upload(String file1, String Path1) throws IOException, Exception {
		// TODO Auto-generated method stub
		String filepath = null;
		filepath = Path1+file1;
		File ff= new File(filepath);
		FileInputStream file = null;
		BufferedInputStream bis = null;
		boolean fileExists = true;

		try {
			file = new FileInputStream(filepath);
			bis = new BufferedInputStream(file);
		} catch (FileNotFoundException excep) {
			fileExists = false;
			System.out.println("FileNotFoundException:" + excep.getMessage());
		}
			byte[] buffer = new byte[buffersize];
			
			long BytesReadSoFar = 0;

			int bytesRead = -1;
			long fileSize = filepath.length();
			int ss=(int) ff.length();
			pw.println(file1);
			System.out.println("Upload file");
			while ((bytesRead = bis.read(buffer)) != -1) {
				
				outToServer.write(buffer, 0, bytesRead);
				BytesReadSoFar += bytesRead;
				percentCompleted = (int) (BytesReadSoFar * 100 / fileSize);
				System.out.println(bytesRead);
				if(bytesRead==8192) {
				System.out.println("Progress: "+(BytesReadSoFar)*100/ss+"%");
				}
				else {
					System.out.println("Progress: "+"100%");

				}
			}
			
	
	}
	
	private void download(String filenameonserver, String path2) throws IOException, ClassNotFoundException{
		// TODO Auto-generated method stub
		
		File directory = new File(path2);

		if (!directory.exists()) {
			directory.mkdir();
		}
		boolean complete = true;
		byte[] data = new byte[size];
		//////////////////////
		filenameonserver = file1;
		file = new String("*" + file1 + "*");
		//lets the server know file is to be downloaded
		pw.println(file); 

		ObjectInputStream oin = new ObjectInputStream(inFromServer);
		String s = (String) oin.readObject();
		//System.out.println(s);

		if(s.equals("Success")) {
			long BytesReadSoFar = 0;
			File f = new File(directory, file1);
			long filelength=f.length();
			System.out.println(filelength);
			FileOutputStream fileOut = new FileOutputStream(f);
			DataOutputStream dataOut = new DataOutputStream(fileOut);
			//System.out.println("transfer 20%");
			//empty file case
			while ((c = inFromServer.read(data))!=-1) {
				dataOut.write(data, 0, c);
				BytesReadSoFar += c;	
				if(c==8192) {
					System.out.println("Progress: "+(BytesReadSoFar)*100/filelength+"%");
					}
					else {
						System.out.println("Progress: "+"100%");

					}
			}
			System.out.println("Completed");
			dataOut.flush();
			fileOut.close();
		}
		else {
			System.out.println("Requested file not found on the server.");
		}
	}

	private void rmdir(String path2) {
		File directory = new File(path2);
		if (directory.exists()) {
			directory.delete();
		}	
		System.out.println("Directory deleted");
	}

	private void mkdir(String path2) {
		File directory = new File(path2);
		if (!directory.exists()) {
			directory.mkdir();
		}
		System.out.println("Directory created");
	}

	private void dir(String path2) {
		File directory = new File(path2);
		ArrayList<String> names = new ArrayList<String>(Arrays.asList(directory.list()));
		for(String name1:names) {
			System.out.println(name1);
		}
	}

	private void remove(String path2) {
		File directory = new File(path2);
		if (directory.exists()) {
			directory.delete();
		}	
		System.out.println("File Remove");
	}

	private void shutdown()throws IOException,Exception {
		OutputStream os = clientSocket.getOutputStream();
		DataOutputStream dos = new DataOutputStream(os);
		System.out.println("Server Closed");
		dos.writeUTF("closed");
		dos.flush();
		dos.close();
		clientSocket.close();

	}

	public static void main(String args[]) {
		// the first one as function,
		// the second one as host address and the third one as port number
		// If host address is not present, default it to "localhost"
		// If port number is not present, default it to 8000
		// If directory path is not present, show error
		//input arguments: options, address or localhost, port number, server directory, clientpath, filename
		//default1: directory for server
		String defaultserver="C:\\Users\\LarrySu\\eclipse-workspace\\Program_Assignment1\\src\\Server\\";
		//default2: directory for client
		String defaultclient="C:\\Users\\LarrySu\\eclipse-workspace\\Program_Assignment1\\src\\";
		String defaultfile="abc.txt";
		String PA1_Server = System.getenv("PA1_Server");
		String[] words = PA1_Server.split(":");

		//for (String word: words) {
		  //      System.out.println(word);
		//}

		if(args.length == 1){
			if(args[0].equals("shutdown")) {
				Client ftp = new Client(args[0], words[0], Integer.parseInt(words[1]),defaultserver,defaultclient,defaultfile);
			}	
			else {
				System.out.println("Wrong Type");
			}
		}
		else if(args.length == 2){
			if(args[0].equals("remove")||args[0].equals("mkdir")||args[0].equals("rmdir")) {
			Client ftp = new Client(args[0], words[0], Integer.parseInt(words[1]),args[1],defaultclient,"");
			}
			if(args[0].equals("dir")) {
			Client ftp = new Client(args[0], words[0], Integer.parseInt(words[1]),args[1],defaultclient,defaultfile);
			}
		}else if(args.length ==3) {
			if(args[0].equals("upload")||args[0].equals("download")) {
			Client ftp = new Client(args[0], words[0], Integer.parseInt(words[1]),defaultserver,args[1],args[2]);
			}
			if(args[0].equals("remove")||args[0].equals("mkdir")||args[0].equals("rmdir")) {
			Client ftp = new Client(args[0], words[0], Integer.parseInt(words[1]),args[1],defaultclient,args[2]);
			}
		}
		else {
			System.out.println("Please enter the function as first varible and server address as second variable in command line.");
		}
		
	}
	

}
