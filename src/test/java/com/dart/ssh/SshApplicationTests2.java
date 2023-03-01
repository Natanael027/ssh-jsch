package com.dart.ssh;

import com.dart.ssh.Entity.SSH;
import com.dart.ssh.config.SSHClientConfiguration;
import com.dart.ssh.repository.SshRepository;
import com.jcraft.jsch.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@SpringBootTest
class SshApplicationTests2 {

	@Autowired
	private SshRepository repo;

	@Autowired
	SSHClientConfiguration configuration;


	private static Session session;
	private static ChannelShell channel;
	private static String username = "app";
	private static String password = "qwertywagwbc2";
	private static String hostname = "192.168.1.244";


	private static Session getSession(){
		if(session == null || !session.isConnected()){
			session = connect(hostname,username,password);
		}
		return session;
	}

	private static Channel getChannel(){
		if(channel == null || !channel.isConnected()){
			try{
				channel = (ChannelShell)getSession().openChannel("shell");
				((ChannelShell) channel).setPty(true);
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

	private static void executeCommands(List<String> commands){

		try{
			Channel channel=getChannel();
			((ChannelShell) channel).setPty(true);
			System.out.println("Sending commands...");
			sendCommands(channel, commands);

			readChannelOutput(channel);
			System.out.println("Finished sending commands!");

		}catch(Exception e){
			System.out.println("An error ocurred during executeCommands: "+e);
		}
	}

	private static void sendCommands(Channel channel, List<String> commands){

		try{
			PrintStream out = new PrintStream(channel.getOutputStream());

			out.println("#!/bin/bash");
			for(String command : commands){
				out.println(command);
			}
			out.write((password + "\n").getBytes());
			out.flush();
			out.println("exit");

			out.flush();
		}catch(Exception e){
			System.out.println("Error while sending commands: "+ e);
		}

	}

	private static void readChannelOutput(Channel channel){

		byte[] buffer = new byte[1024];

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
	public void main(){
		List<String> commands = new ArrayList<String>();
//		commands.add("sudo -S -p '' " +	"mkdir tesSudo");
//		commands.add("sudo -S -p '' " +	"sudo service ssh status");
		commands.add("sudo -S -p '' " +	"sudo cat cmd.txt");

		executeCommands(commands);
		close();
	}

}


