package com.dart.ssh;

import com.jcraft.jsch.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
/*
*  Test Port Forwarding using JSCH
* */
@SpringBootTest
class SshApplicationTests4 {
	private static Session session;
	private static ChannelShell channel;
	public String output = "";
	private static String username = "app";
	private static String password = "qwertywagwbc2";
	private static String hostname = "192.168.1.244";


	private static Session getSession() throws JSchException {
		if(session == null || !session.isConnected()){
			session = connect(hostname,username,password);
		}
		int forwardedPort = 0;
		JSch jSch = new JSch();

		forwardedPort = session.setPortForwardingL(0, "hostB", 22);
		Session sessionB = jSch.getSession("usernameB", "localhost", forwardedPort);

		sessionB.connect();
		return sessionB;
	}

	private static Channel getChannel(){
		if(channel == null || !channel.isConnected()){
			try{
				channel = (ChannelShell)getSession().openChannel("shell");
				channel.setPty(true);
				channel.connect();

			}catch(Exception e){
				System.out.println("Error while opening channel: "+ e);
			}
		}
		return channel;
	}

	private static Session connect(String hostname, String username, String password){

		JSch jSch = new JSch();
		try {
			session = jSch.getSession(username, hostname, 22);
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(password);

			System.out.println("Connecting SSH to " + hostname + " - Please wait for few seconds... ");
			session.connect();
			System.out.println("Connected!");
		}catch(Exception e){
			System.out.println("An error occurred while connecting to "+hostname+": "+e);
		}

		return session;

	}

	private static Session connect2(String hostname, String username, String password){
		JSch jSch = new JSch();
		try {
			session = jSch.getSession(username, hostname, 22);
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(password);

			System.out.println("Connecting SSH to " + hostname + " - Please wait for few seconds... ");
			session.connect();
			System.out.println("Connected!");
		}catch(Exception e){
			System.out.println("An error occurred while connecting to "+hostname+": "+e);
		}

		return session;

	}

	private void executeCommands(List<String> commands){

		try{
			Channel channel=getChannel();

			System.out.println("Sending commands...");
			sendCommands(channel, commands);

			readChannelOutput(channel);
			System.out.println("Finished sending commands!");

		}catch(Exception e){
			System.out.println("An error ocurred during executeCommands: "+e);
		}
	}
	private static void sendCommandsori(Channel channel, List<String> commands){
		try{
			PrintStream out = new PrintStream(channel.getOutputStream());

			out.println("#!/bin/bash");
			for(String command : commands){
				out.println(command);
			}
			out.println("exit");

			out.flush();
		}catch(Exception e){
			System.out.println("Error while sending commands: "+ e);
		}

	}
	private static void sendCommands(Channel channel, List<String> commands){
		try{
//			PrintStream out = new PrintStream(channel.getOutputStream());
			OutputStream out = channel.getOutputStream();

//			out.write(("#!/bin/bash"+ "\n").getBytes());
			out.write(("ssh pi@192.168.1.191" + "\n").getBytes());
//			out.println("ssh pi@192.168.1.191");
//			out.println("raspberry" );

			out.write(("ls -l" + "\n").getBytes());


			for(String command : commands){
				out.write((command+ "\n").getBytes());
//				out.write((password + "\n").getBytes());
//				out.flush();
			}

			//cuman satu command sudo

			out.write(("exit" + "\n").getBytes());
//			out.println("exit");
			out.flush();
		}catch(Exception e){
			System.out.println("Error while sending commands: "+ e);
		}

	}

	public String runCommand(String command) throws JSchException, IOException {

		String ret = "";

		if (!session.isConnected())
			throw new RuntimeException("Not connected to an open session.  Call open() first!");

		ChannelExec channel = null;
		channel = (ChannelExec) session.openChannel("exec");

		channel.setCommand(command);
		channel.setInputStream(null);

		PrintStream out = new PrintStream(channel.getOutputStream());
		InputStream in = channel.getInputStream(); // channel.getInputStream();

		channel.connect();

		// you can also send input to your running process like so:
		// String someInputToProcess = "something";
		// out.println(someInputToProcess);
		// out.flush();

		ret = getChannelOutput(channel, in);

		channel.disconnect();

		System.out.println("Finished sending commands!");

		return ret;
	}
	private String getChannelOutput(Channel channel, InputStream in) throws IOException{

		byte[] buffer = new byte[1024];
		StringBuilder strBuilder = new StringBuilder();

		String line = "";
		while (true){
			while (in.available() > 0) {
				int i = in.read(buffer, 0, 1024);
				if (i < 0) {
					break;
				}
				strBuilder.append(new String(buffer, 0, i));
				System.out.println(line);
			}

			if(line.contains("logout")){
				break;
			}

			if (channel.isClosed()){
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (Exception ee){}
		}

		return strBuilder.toString();
	}


	private void readChannelOutput(Channel channel) throws IOException {

		byte[] buffer = new byte[1024];
		OutputStream out = channel.getOutputStream();

		try{
			InputStream in = channel.getInputStream();
			String line = "";
			while (true){
				while (in.available() > 0) {
					int i = in.read(buffer, 0, 1024);
					if (i < 0) {
						break;
					}
					line = new String(buffer, 0, i);
					output = line;
					System.out.println(line);
//					if (line.contains("[sudo] password for app:")){
//						out.write(("qwertywagwbc2" + "\n").getBytes());
//						out.flush();
//					}

/*
					if (output.contains("password:")){
						out.write(("raspberry" + "\n").getBytes());
						out.flush();
					}else if (output.contains("pi@raspberrypi:~$")){
						List<String> commands = new ArrayList<String>();
						commands.add("ls -l");
						commands.add("touch testing.txt");
						commands.add("sudo touch testing2.txt");
						commands.add("ping google.com");
//						commands.add("sudo service ssh status");
						sendCommandsori(channel, commands);

//						out.write(("ls -ltr" + "\n").getBytes());
//						out.flush();
					}
*/
				}
//				System.out.println("out>>"+output);
				if(line.contains("logout")){
					break;
				}

				if (channel.isClosed()){
					break;
				}

				try {
					Thread.sleep(1000);
				} catch (Exception ee){}
			}
			System.out.println("out2>>"+output);

		}catch(Exception e){
			System.out.println("Error while reading channel output: "+ e);
		}

	}

	public static void close(){
		channel.disconnect();
		session.disconnect();
		System.out.println("Disconnected channel and session");
	}

	@Test
	public void testing(){
		List<String> commands = new ArrayList<String>();
//		commands.add("sudo -S -p '' " +	"sudo service ssh status");
		commands.add("sudo cat cmd.txt");
		commands.add("sudo service ssh status");
		commands.add("ls -l");

		executeCommands(commands);
		close();
	}



}