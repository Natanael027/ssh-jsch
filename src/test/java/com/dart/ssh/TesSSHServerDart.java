package com.dart.ssh;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import com.dart.ssh.Service.ServiceShell;
import com.dart.ssh.config.SSHClientConfiguration;
import com.dart.ssh.repository.SshRepository;
import com.jcraft.jsch.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.*;

@SpringBootTest
class TesSSHServerDart {

    @Autowired
    private SshRepository repo;

    @Autowired
    SSHClientConfiguration configuration;

    @Autowired
    private ServiceShell serviceShell;
    String ip1 = "103.39.68.205";
    String ip2 = "103.39.68.162";
//	String ip3 = "103.39.68.155";
//	String ip4 = "103.39.68.158";

    /*
        103.39.68.159 MP179
        103.39.68.162 MP183
    */
    List<String> soundCommand = Arrays.asList("cd sound_recognition", "pm2 delete all", "pip install git+https://github.com/openai/whisper.git", "pm2 start app --interpreter venv/bin/python3.8 --name sound-recognition", "pm2 ls");
    List<String> soundCommand2 = Arrays.asList("pm2 ls", "pm2 delete sound-recognition" ,"pm2 ls", "cd sound_recognition", "pm2 start app --interpreter venv/bin/python3.8 --name sound-recognition", "pm2 ls");
    List<String> soundCommand3 = Arrays.asList("pm2 ls", "pm2 restart all" ,"pm2 ls");
    List<String> cmd = Arrays.asList("cd whatsappWorker/.wwebjs_auth", "rm -rf *", "cd ../", "git fetch --tags", "git reset --hard tags/v1.2.5.9", "yarn install", "yarn upgrade");
    List<String> cmd2 = Arrays.asList("cd waWorkerAgent", "pm2 start agent.js --name wa-agent");

    List<String> listIp = Arrays.asList(
            "103.39.68.205",
            "103.39.68.206",
            "103.39.68.207",
            "103.39.68.208",
            "103.39.68.131",
            "103.39.68.132",
            "103.39.68.133",
            "103.39.68.134",
            "103.39.68.136",
            "103.39.68.137",
            "103.39.68.138",
            "103.39.68.139",
            "103.39.68.142",
            "103.39.68.143",
            "103.39.68.144",
            "103.39.68.145",
            "103.39.68.146",
            "103.39.68.147",
            "103.39.68.150",
            "103.39.68.151",
            "103.39.68.152",
            "103.39.68.153",
            "103.39.68.154",
            "103.39.68.155",
            "103.39.68.156",
            "103.39.68.157",
            "103.39.68.158",
            "103.39.68.159",
            "103.39.68.160",
            "103.39.68.161",
            "103.39.68.162",
            "103.39.68.163",
            "103.39.68.164",
            "103.39.68.165",
            "103.39.68.166",
            "103.39.68.168",
            "103.39.68.169",
            "103.39.68.172",
            "103.39.68.173",
            "103.39.68.174",
            "103.39.68.175",
            "103.39.68.176",
            "103.39.68.177",
            "103.39.68.178",
            "103.39.68.179",
            "103.39.68.180",
            "103.39.68.181",
            "103.39.68.182",
            "103.39.68.183"
            );

    List<String> listIp2 = Arrays.asList(
            "103.39.68.132",
            "103.39.68.133",
            "103.39.68.134",
            "103.39.68.136",
            "103.39.68.137"
            );

    List<String> listIp3 = Arrays.asList(
            "103.39.68.139",
            "103.39.68.142",
            "103.39.68.143",
            "103.39.68.144",
            "103.39.68.145"
    );

    @Test
    void testing() throws IOException {

        for (String ip : listIp2){
//            String ip = ip1;
            ServiceShell shell = new ServiceShell();
            int id = 2;
            shell.setUsername("app");
            shell.setHostname(ip);
            shell.setPassword("qwertywagwbc2");
            shell.setChannel((ChannelShell) shell.getChannel());
            shell.setPort(22);

            try {
                commandText(shell, soundCommand, ip);
                Thread.sleep(2000);
/*
                editFileConfig(ip);

                Thread.sleep(2000);
                commandFile(shell);

                Thread.sleep(2000);
                commandText(shell, soundCommand2, ip);

*/
                System.out.println(ip + " isdone");
            }catch (Exception e){
                System.out.println(e.getMessage());
            }

        }
    }

    @Test
    void testing2() throws IOException {

        for (String ip : listIp2){
//            String ip = ip1;
            ServiceShell shell = new ServiceShell();
            int id = 2;
            shell.setUsername("app");
            shell.setHostname(ip);
            shell.setPassword("qwertywagwbc2");
            shell.setChannel((ChannelShell) shell.getChannel());
            shell.setPort(22);

            try {
                commandText(shell, soundCommand, ip);
                Thread.sleep(2000);
/*
                editFileConfig(ip);

                Thread.sleep(2000);
                commandFile(shell);

                Thread.sleep(2000);
                commandText(shell, soundCommand2, ip);

*/
                System.out.println(ip + " isdone");
            }catch (Exception e){
                System.out.println(e.getMessage());
            }

        }
    }

/*    @Test
    void testing2() throws IOException {

        for (String ip : listIp2){
//            String ip = ip1;
            ServiceShell shell = new ServiceShell();
            int id = 2;
            shell.setUsername("app");
            shell.setHostname(ip);
            shell.setPassword("qwertywagwbc2");
            shell.setChannel((ChannelShell) shell.getChannel());
            shell.setPort(22);

            try {

                Thread.sleep(2000);
                commandFileModel(shell);

                Thread.sleep(2000);
                commandText(shell, soundCommand3, ip);

                System.out.println(ip + " done");
            }catch (Exception e){
                System.out.println(e.getMessage());
            }

        }
    }*/


    /*

        @Test
        void testing3() throws IOException {
            List<String> cmd = Arrays.asList("cd whatsappWorker/.wwebjs_auth", "rm -rf *", "cd ../", "git fetch --tags", "git reset --hard tags/v1.2.5.9", "yarn install", "yarn upgrade");
            List<String> cmd2 = Arrays.asList("cd waWorkerAgent", "pm2 start agent.js --name wa-agent");

            ServiceShell shell = new ServiceShell();
            int id = 2;
            shell.setUsername("app");
            shell.setHostname(ip3);
            shell.setPassword("qwertywagwbc2");
            shell.setChannel((ChannelShell) shell.getChannel());
            shell.setPort(22);

            try {
                command(shell, cmd);
                Thread.sleep(2000);
                command(shell, cmd2);
                System.out.println(ip3 + " done");
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }

        @Test
        void testing4() throws IOException {
            List<String> cmd = Arrays.asList("cd whatsappWorker/.wwebjs_auth", "rm -rf *", "cd ../", "git fetch --tags", "git reset --hard tags/v1.2.5.9", "yarn install", "yarn upgrade");
            List<String> cmd2 = Arrays.asList("cd waWorkerAgent", "pm2 start agent.js --name wa-agent");

            ServiceShell shell = new ServiceShell();
            int id = 2;
            shell.setUsername("app");
            shell.setHostname(ip4);
            shell.setPassword("qwertywagwbc2");
            shell.setChannel((ChannelShell) shell.getChannel());
            shell.setPort(22);

            try {
                command(shell, cmd);
                Thread.sleep(2000);
                command(shell, cmd2);
                System.out.println(ip4 + " done");
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }

    */
    void command(ServiceShell shell, List<String> cmd, String ip) throws IOException {
        try {
            Channel channel = shell.getChannel();
            shell.newCommand(cmd,2L, ip, channel);
            shell.putFile("File/Ref/conf","whatsappWorker/.env");
            shell.close();

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    void commandText(ServiceShell shell, List<String> cmd, String ip) throws  IOException{
        try {
            Channel channel = shell.getChannel();
            shell.newCommand(cmd,2L, ip, channel);
            shell.close();

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    void commandFile(ServiceShell shell) throws  IOException{
        try {
            Channel channel = shell.getChannel();
            shell.putFile("File/Ref/.envs","sound_recognition/.env");
            shell.close();

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    void commandFileModel(ServiceShell shell) throws  IOException{
        try {
            Channel channel = shell.getChannel();
            shell.putFile("File/Ref/ringing_1680688942.8656914.h5","sound_recognition/ringing_detection/model/ringing_1680688942.8656914.h5");
            shell.close();

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    void editFileConfig(String pcCode){
        // Specify the file path
        String filePath = "File/Ref/.envs";


        // Specify the new first line content
        String firstLine = "PC_CODE=" + pcCode;
        String newContent = "\nIP_API=108.136.134.160\nPORT_API=30001\nIP_UPLOAD=103.39.69.167\nPORT_UPLOAD=30001\nTIME_DELETE=86400\nCOOLDOWN=10";

        firstLine = firstLine + newContent;

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(firstLine);
            writer.close();

            System.out.println("File content updated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


/*
	@Test
	void contextLoads() {
		String host="192.168.1.244";
		String password="qwertywagwbc2";
		String user="app";

//		String command1="cd tes ; ls -ltr ; cat here.txt";
		String command1="glances";
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
//			serviceShell.putFile("File/event.conf","/etc/nginx/sites-enabled/event.conf");
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

		String command1="htop";
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
	void tesCommandCombine() throws IOException, JSchException {
		ServiceShell shell = new ServiceShell();
		shell.setUsername("app");
		shell.setHostname("192.168.1.244");
		shell.setPassword("qwertywagwbc2");
		shell.setPort(22);
		Channel channelSftp = new ServiceShell("app","192.168.1.244","qwertywagwbc2",22).getSession().openChannel("sftp");
//		serviceShell.newCommand(Collections.singletonList("cd ./github"), 11L,"");
*/
/*
		serviceShell.getFileEnv("event.conf.save","File/testing.txt");
		serviceShell.getFile("/tesSudoPut/testing.txt","File/testing.txt");
		serviceShell.putFile("File/event.conf","/etc/nginx/sites-enabled/event.conf");
*//*

		shell.getFileGit(".env", "File/tes.env");
		shell.putFile("File/filename_1.txt","tesSudoPut/test.txt");
//		shell.putFile2("File/filename_1.txt","test.txt");
//		serviceShell.putFile("File/event.conf6","/home/app/event.conf", channelSftp);

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

	@Test
	void testingexpoert(){
		//measuring elapsed time using Spring StopWatch
		StopWatch watch = new StopWatch();
		watch.start();
		for(int i=0; i< 1000000; i++){
			DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd_HH-mm-ss");
			Date date1 = new Date();
			String timestamp = dateFormat.format(new Date());
//			System.out.println(date1 + "||" + timestamp);

			DateFormat dateFormat1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Calendar cal = Calendar.getInstance();
//			System.out.println("Current Date Time : " + dateFormat1.format(cal.getTime()));

			//Add one minute to current date time
			cal.add(Calendar.MINUTE, 1);
//			System.out.println("After Date Time : " + dateFormat1.format(cal.getTime()));

			Object obj = new Object();
//			System.out.println("StopWatch in millis: "+ watch.getTotalTimeMillis());
		}
		watch.stop();
		System.out.println("Total execution time to create 1000K objects in Java using StopWatch in millis: "
				+ watch.getTotalTimeMillis());
	}


	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public String start() throws SshException{
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar cal2 = Calendar.getInstance();
		cal2.add(Calendar.MINUTE, 1);

		scheduler.scheduleAtFixedRate(() -> {
			Calendar cal = Calendar.getInstance();

			long yourmilliseconds = System.currentTimeMillis();
			SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
			Date resultdate = new Date(yourmilliseconds);
			// Do something every 5 seconds
			System.out.println("\n\nTask executed at " + sdf.format(resultdate));

			*/
/*
				1 : true
				0 : equal
			   -1 : false
			*//*

			System.out.println(cal.getTime()+" || "+ cal2.getTime());
			if (cal.compareTo(cal2)==1){
				System.out.println("true");
				throw new RejectedExecutionException();
			}

		}, 0, 5, TimeUnit.SECONDS);
		return "";
	}

	public void stop() {
		scheduler.shutdown();
	}

	@Test
	void tesInterval() throws SshException {
		Calendar cal = Calendar.getInstance();
		System.out.println(cal);
		start();
		tesBash();
		stop();
	}
*/

}

