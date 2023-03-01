package com.dart.ssh;

import com.dart.ssh.Entity.SSH;
import com.dart.ssh.Service.ServiceCommand;
import com.dart.ssh.Service.ServiceShell;
import com.dart.ssh.config.SSHClientConfiguration;
import com.dart.ssh.repository.SshRepository;
import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@SpringBootTest
class SshApplicationTests {

	@Autowired
	private SshRepository repo;

	@Autowired
	SSHClientConfiguration configuration;

	@Autowired
	private ServiceShell serviceShell;

	@Test
	void contextLoads() {
		String host="192.168.1.244";
		String password="qwertywagwbc2";
		String user="app";

//		String command1="cd tes ; ls -ltr ; cat here.txt";
		String command1="nano event.conf";
//		String command1="top";

		String command2="touch tes2.txt";
//        session.setConfig("StrictHostKeyChecking", "no");
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		try{
			JSch jsch = new JSch();
			Session session=jsch.getSession(user, host, 22);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");

//            session.setConfig(config);
			session.connect();
			System.out.println("Connected");

			Channel channel=session.openChannel("exec");
			((ChannelExec)channel).setPty(true);

			// ps aux | grep jar
			((ChannelExec)channel).setCommand(command1);
//            ((ChannelExec)channel).setPty();
			channel.setInputStream(null);
			((ChannelExec)channel).setErrStream(System.err);

			InputStream in=channel.getInputStream();
			channel.connect();
			byte[] tmp=new byte[1024];
			while(true){
				while(in.available()>0){
					int i=in.read(tmp, 0, 1024);
					if(i<0)break;
					System.out.print(new String(tmp, 0, i));
				}
				if(channel.isClosed()){
					System.out.println("exit-status: "+channel.getExitStatus());
					break;
				}
				try{Thread.sleep(1000);}catch(Exception ee){}
			}
			channel.disconnect();
			session.disconnect();
			System.out.println("DONE");
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	@Test
	void tesFile() {
		String host="192.168.1.244";
		String password="qwertywagwbc2";
		String user="app";
		String remoteFile = ".env";
		String remoteDir = "tes/";
		String localDir = "File/";
		String localFile = "File/here.txt";

		String command1="cd ./tes";
		String command2="ls -ltr";

		try{
			JSch jsch = new JSch();
			Session session=jsch.getSession(user, host, 22);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");

//            session.setConfig(config);
			session.connect();
			System.out.println("Connected");

			Channel sftp  = session.openChannel("sftp");
			// 5 seconds timeout
			sftp.connect();

			ChannelSftp channelSftp = (ChannelSftp) sftp;
//			channelSftp.cd("./github");
			System.out.println(channelSftp.getHome()+" || "+ channelSftp.pwd());
			// transfer file from local to remote server
//			channelSftp.put(localFile, remoteDir+"here.txt");
//			serviceShell.putFile("File/.env","waAgentGithub/.env");
			serviceShell.putFile("File/event.conf","/etc/nginx/sites-enabled/event.conf");
//			serviceShell.putFile("File/event.conf","event.conf");
			// download file from remote server to local
//            channelSftp.get(remoteFile, localDir + "here.txt");

			channelSftp.exit();
			System.out.println("DONE");
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	@Test
	void tesCommand() throws JSchException, InterruptedException {
		List<SSH> findAll = repo.findAll();
		System.out.println(findAll.toString());

		int i = 1;
		for (SSH s : findAll){
			configuration.connect("app", s.getIp(), Integer.valueOf(s.getPort()), "qwertywagwbc2");
			configuration.startClient();
			String[] cmd = s.getCommand().split(",");
			for (String q : cmd) {
				System.out.println(i+ " || "+cmd.length);
				System.out.println(q);
				Thread.sleep(3000);
				if (i==cmd.length){
					SSH ssh = repo.findById(s.getId()).get();
					ssh.setStatus(1L);
					ssh.setResult(configuration.newCommand(q));
					repo.save(ssh);
				}else {
					configuration.newCommand(q);
				}
				i++;
			}
			configuration.stopClient();
			System.out.println("end");
		}
	}

	@Test
	void tesCommandFile() throws JSchException, InterruptedException {
		List<SSH> findAll = repo.findAll();
		System.out.println(findAll.toString());

		int i = 1;
		for (SSH s : findAll){
			configuration.connect("app", s.getIp(), Integer.valueOf(s.getPort()), "qwertywagwbc2");
			configuration.startClient();
			configuration.putFile("File/filename_1.txt","tes/");
			configuration.stopClient();
			System.out.println("end");
		}
	}


	void runSudoCommand(String user, String password, String host, String command) {
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		JSch jsch = new JSch();
		Session session;
		try {
			session = jsch.getSession(user, host, 22);
			session.setPassword(password);
			session.setConfig(config);
			session.connect();
			System.out.println("Connected to " + host);
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand("sudo -S -p '' " + command);
//			((ChannelExec) channel).setCommand("sudo -S -p '' " + "ping");
			channel.setInputStream(null);
			OutputStream out = channel.getOutputStream();
			((ChannelExec) channel).setErrStream(System.err);
			InputStream in = channel.getInputStream();
			((ChannelExec) channel).setPty(true);
			channel.connect();
			out.write((password + "\n").getBytes());
			out.flush();
			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0) break;
					System.out.print(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					System.out.println("Exit status: " + channel.getExitStatus());
					break;
				}
			}
			channel.disconnect();
			session.disconnect();
			System.out.println("DONE");
		} catch (JSchException | IOException e) {
			e.printStackTrace();
		}
	}
//	@Test
	void runSudoShellCommand(String user, String password, String host, String command) {
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		JSch jsch = new JSch();
		Session session;
		try {
			session = jsch.getSession(user, host, 22);
			session.setPassword(password);
			session.setConfig(config);
			session.connect();
			System.out.println("Connected to " + host);
			Channel channel = session.openChannel("shell");
//			((ChannelShell) channel).("sudo -S -p '' " + command);
			channel.setInputStream(null);
//			OutputStream out = channel.getOutputStream();
			PrintStream out = new PrintStream(channel.getOutputStream());
			out.println("sudo -S -p '' " +command);
//			((ChannelShell) channel).setErrStream(System.err);
			InputStream in = channel.getInputStream();
			((ChannelShell) channel).setPty(true);
			channel.connect();
			out.write((password + "\n").getBytes());
			out.flush();
			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0) break;
					System.out.print(new String(tmp, 0, i));
				}
				if (channel.isClosed()) {
					System.out.println("Exit status: " + channel.getExitStatus());
					break;
				}
			}
			channel.disconnect();
			session.disconnect();
			System.out.println("DONE");
		} catch (JSchException | IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	void tesSudo(){
		String host="192.168.1.244";
		String password="qwertywagwbc2";
		String user="app";

//		String command = "sudo touch /etc/nginx/sites-enabled/event.conf";
//		String command = "pm2 kill; sudo pm2 start api";
//		String command = "pm2 ping";
//		String command = "sudo service ssh status";
		String command = "sudo cat cmd.txt";

		runSudoCommand(user, password, host, command);
	}

	@Test
	void tesBash(){
		String host="192.168.1.244";
		String password="qwertywagwbc2";
		String user="app";

		String command1="cd github; cat .env ";
		try{

			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			JSch jsch = new JSch();
			Session session=jsch.getSession(user, host, 22);
			session.setPassword(password);
			session.setConfig(config);
			session.connect();
			System.out.println("Connected");

			Channel channel=session.openChannel("shell");
//			((ChannelExec)channel).setCommand(command1);
//			channel.setInputStream(null);
//			((ChannelExec)channel).setErrStream(System.err);
//
			channel.connect();
			PrintStream out = new PrintStream(channel.getOutputStream());
			out.println(command1);
			out.flush();
			InputStream in=channel.getInputStream();

			byte[] tmp=new byte[1024];
			while(true){
				while(in.available()>0){
					int i=in.read(tmp, 0, 1024);
					if(i<0)break;
					System.out.print(new String(tmp, 0, i));
				}
				if(channel.isClosed()){
					System.out.println("exit-status: "+channel.getExitStatus());
					break;
				}
				try{Thread.sleep(1000);}catch(Exception ee){}
			}
			channel.disconnect();
			session.disconnect();
			System.out.println("DONE");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Test
	void tesFileReplace(){
		File fileToBeModified = new File("File/event.conf");
		String oldContent = "";
		BufferedReader reader = null;
		FileWriter writer = null;

		try
		{
			reader = new BufferedReader(new FileReader(fileToBeModified));
			//Reading all the lines of input text file into oldContent
			String line = reader.readLine();
			while (line != null)
			{
				oldContent = oldContent + line + System.lineSeparator();
				line = reader.readLine();
			}

			//Replacing oldString with newString in the oldContent
			String newContent = oldContent.replaceAll("server_name2", "server_name");

			//Rewriting the input text file with newContent
			writer = new FileWriter(fileToBeModified);
			writer.write(newContent);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				//Closing the resources
				reader.close();
				writer.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Test
	void tesCommandCombine() throws IOException {
		serviceShell.username="app";
		serviceShell.hostname="192.168.1.244";
		serviceShell.password="qwertywagwbc2";
		serviceShell.port = 22;

//		serviceShell.newCommand(Collections.singletonList("cd ./github"), 11L,"");
		/*serviceShell.getFileEnv("event.conf.save","File/testing.txt");
		serviceShell.getFile("/tesSudoPut/testing.txt","File/testing.txt");
		serviceShell.putFile("File/event.conf","/etc/nginx/sites-enabled/event.conf");
		serviceShell.putFile("File/event.conf","/tesSudoPut/test.txt");*/
		serviceShell.putFile("File/event.conf6","/home/app/event.conf");

//		serviceShell.putFile("File/event.conf","waAgentGithub/event.conf");
//		serviceShell.newCommand("cat .env", 11L);
	}
	@Autowired
	ServiceCommand serviceCommand;
	@Test
	void tesRepo() throws JSchException, IOException, InterruptedException {
		SSH s = repo.findByIds(1L);
		SSH s2 = repo.findByIds(2L);
		ServiceShell shell = new ServiceShell();
		shell.setUsername("app");
		shell.setHostname("192.168.1.244");
		shell.setPassword("qwertywagwbc2");
		shell.setPort(22);
		CompletableFuture<String> task1 = serviceCommand.schedulerHari2(s,shell);
		CompletableFuture<String> task2 = serviceCommand.schedulerHari2(s2,shell);

		// Wait until they are all done
		CompletableFuture.allOf(task2).join();
//		System.out.println(find3Id.toString());
	}

	@Test
	void tesNweCommand() throws JSchException, IOException, InterruptedException {
		SSH s = repo.findByIds(1L);
		SSH s2 = repo.findByIds(3L);
		ServiceShell shell = new ServiceShell();
		shell.setUsername("app");
		shell.setHostname("192.168.1.244");
		shell.setPassword("qwertywagwbc2");
		shell.setPort(22);

		CompletableFuture<String> task1 = serviceCommand.schedulerHari2(s,shell);
		CompletableFuture<String> task2 = serviceCommand.schedulerHari2(s2,shell);

		Logger.getLogger(String.valueOf(task1));
		Logger.getLogger(String.valueOf(task2));
		//		String task1 = shell.newCommand(Collections.singletonList(s2.getCommand()),s.getId(),s.getIp());;
//		System.out.println(task1);
		// Wait until they are all done
		CompletableFuture.allOf(task1).join();
//		CompletableFuture.allOf(task1,task2).join();
//		System.out.println(find3Id.toString());
	}

}


