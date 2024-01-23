package com.dart.ssh.Service;

import com.jcraft.jsch.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
//@AllArgsConstructor
//@NoArgsConstructor
@Configuration
@Data
//@Async
public class ServiceShell {
    public Session session;
    public Session session2;
    public ChannelShell channel;
    public ChannelExec channel2;
    public String password;
    public String username;
    public String hostname;
    public Integer port;

    public ServiceShell(String username, String hostname, String password, Integer port) {
        this.username = username;
        this.password = password;
        this.hostname = hostname;
        this.port = port;
    }

    public ServiceShell() {
    }

    public String finalResult;

    // The host of the second target
    public String tunnelRemoteHost;
    public String secondPassword;

    public Session getSession(){
        if(session == null || !session.isConnected()){
            session = connect(hostname,username,password, port);
        }
        return session;
    }

    public Session getSession2(){
        if(session2 == null || !session2.isConnected()){
            session2 = connectForwarding(hostname,username,password,port, tunnelRemoteHost,secondPassword);
        }
        return session2;
    }

    public Channel getChannel(){
//        if(channel == null || !channel.isConnected()){
            try{
                channel = (ChannelShell)getSession().openChannel("shell");
                channel.setPty(true);
                channel.setInputStream(null);
                channel.connect();

            }catch(Exception e){
                System.out.println("Error while opening channel: "+ e);
            }
//        }
        return channel;
    }

    public Channel getChannelForwarding(){
//        if(channel == null || !channel.isConnected()){
            try{
                 channel2 = (ChannelExec) getSession2().openChannel("shell");
                 channel2.setPty(true);
                 channel2.setInputStream(null);
                channel2.connect();

            }catch(Exception e){
                System.out.println("Error while opening channel: "+ e);
            }
        return channel2;
    }

    private Session connect(String hostname, String username, String password, Integer port){
        JSch jSch = new JSch();
        try {
            session = jSch.getSession(username, hostname, port);
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

    private Session connectForwarding(String hostname, String username, String password, Integer port, String tunnelRemoteHost, String secondPassword){
        JSch jSch = new JSch();
        try {
            session = jSch.getSession(username, hostname, port);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setPassword(password);
            localUserInfo lui=new localUserInfo();
            session.setUserInfo(lui);
            session.setPortForwardingL(2233, tunnelRemoteHost, 22);

            System.out.println("Connecting SSH to " + hostname + " - Please wait for few seconds... ");
            session.connect();
            session.openChannel("direct-tcpip");
           
            // create a session connected to port 2233 on the local host.
            session2 = jSch.getSession(username, "localhost", 2233);
            session2.setPassword(secondPassword);
            session2.setUserInfo(lui);
            session2.setConfig("StrictHostKeyChecking", "no");

            session2.connect(); // now we're connected to the secondary system
            System.out.println("Connected!");
        }catch(Exception e){
            System.out.println("An error occurred while connecting to "+hostname+": "+e);
        }
        return session2;
    }

    public String executeCommands(List<String> commands){
        try{
            Channel channel=getChannel();

            System.out.println("Sending commands...");
            sendCommands(channel, commands);

            readChannelOutput(channel);
            System.out.println("Finished sending commands!");

        }catch(Exception e){
            System.out.println("An error ocurred during executeCommands: "+e);
        }
        return null;
    }
    @Async
    public String sudoCommand(String command, Long id, Channel channel) throws IOException, InterruptedException {
        try {
//            Channel channel = getSession().openChannel("exec");
            FileWriter myWriter = new FileWriter("File/filename_"+id+".txt", true);
            System.out.println("sudo command :"+command);
            ((ChannelExec) channel).setCommand("sudo -S -p ''" + command);
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
                    String line = (new String(tmp, 0, i));
                    finalResult = line;
                    System.out.println(line);
//                        return line;
                    myWriter.write("\n\nSUDO COMMAND\n");
                    myWriter.write(line);
                    Thread.sleep(500);
                }
                if (channel.isClosed()) {
                    System.out.println("Exit status: " + channel.getExitStatus()+"\n");
                    myWriter.write("exit-status: "+channel.getExitStatus()+"\n");
                    break;
                }
            }
            myWriter.close();
            System.out.println("SUDO DONE");
        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
        return finalResult;
    }
   public String sudoCommandFile(Long id) throws IOException, InterruptedException {

        try {
            Channel channel = getSession().openChannel("exec");
            FileWriter myWriter = new FileWriter("File/filename_"+id+".txt", true);
            System.out.println("sudo event.conf command ");
//            ((ChannelExec) channel).setCommand("sudo -S -p ''" + "");
//            ((ChannelExec) channel).setCommand("sudo -S -p '' mv event.conf /etc/nginx/sites-enabled/");
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
                    String line = (new String(tmp, 0, i));
                    finalResult = line;
                    System.out.println(line);
//                        return line;
                    myWriter.write(line);
                    Thread.sleep(500);
                }
                if (channel.isClosed()) {
                    System.out.println("Exit status: " + channel.getExitStatus()+"\n");
                    myWriter.write("exit-status: "+channel.getExitStatus()+"\n");
                    break;
                }
            }
            myWriter.close();
            System.out.println("SUDO event.conf DONE");
        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
        return finalResult;
    }
   public String sudoCommandFileForwarding(Long id) throws IOException, InterruptedException {

        try {
            Channel channel = getSession2().openChannel("exec");
            FileWriter myWriter = new FileWriter("File/filename_"+id+".txt", true);
            System.out.println("sudo event.conf command ");
//            ((ChannelExec) channel).setCommand("sudo -S -p ''" + "");
            ((ChannelExec) channel).setCommand("sudo -S -p '' mv event.conf /etc/nginx/sites-enabled/");
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
                    String line = (new String(tmp, 0, i));
                    finalResult = line;
                    System.out.println(line);
//                        return line;
                    myWriter.write(line);
                    Thread.sleep(500);
                }
                if (channel.isClosed()) {
                    System.out.println("Exit status: " + channel.getExitStatus()+"\n");
                    myWriter.write("exit-status: "+channel.getExitStatus()+"\n");
                    break;
                }
            }
            myWriter.close();
            System.out.println("SUDO event.conf DONE");
        } catch (JSchException | IOException e) {
            e.printStackTrace();
        }
        return finalResult;
    }
    public void tesFileReplace(String path, String oldString, String newString){
        File fileToBeModified = new File(path);
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
            String newContent = oldContent.replaceAll(oldString, newString);

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
  /*  public String newCommand(List<String> commands, Long id, String IP) throws IOException {
        Channel channel=getChannel();
        FileWriter myWriter = new FileWriter("File/filename_"+id+".txt");
        String localDir = "File/.env"+id;
        System.out.println("tostring "+commands.toString());
        System.out.println("size "+commands.size());
        try{
            PrintStream out = new PrintStream(channel.getOutputStream());

            out.println("#!/bin/bash");
            for(String command : commands){
//                System.out.println(">>> "+command);
                if (command.startsWith(" sudo")){
                    System.out.println("sudo command");
//                    sudoCommand(command, id);
                }
                else if (command.equalsIgnoreCase("whatsappWorker nano")) {
                    System.out.println("\n nano \n");
                    getFileGit(".env", localDir);
                    tesFileReplace(localDir, "REDIS_HOST=172.104.58.42","REDIS_HOST="+IP);
                    putFile(localDir,"whatsappWorker/.env");
                }else   if (command.startsWith(" sudo")){
                    System.out.println("sudo command >> "+ command);
                    command = command.replaceAll("sudo ","");
                   out.write(("sudo -S -p ''"+command+ "\n").getBytes());
                    channel.setInputStream(null);
                    out.write(("qwertywagwnc2" + "\n").getBytes());
                    out.flush();

//                    sudoCommand(command,id);
                }
                else {
                    out.println(command);
                }
            }
            out.println("exit");
            out.flush();
        }catch(Exception e){
            System.out.println("Error while sending commands: "+ e);
        }
            try{
                byte[] buffer = new byte[1024];
                InputStream in = channel.getInputStream();
//                String line = "";
                while (true){
                    while (in.available() > 0) {
                        int i = in.read(buffer, 0, 1024);
                        if (i < 0) {
                            break;
                        }
                        String line = new String(buffer, 0, i);
                        finalResult = line;
                        System.out.println(line);
                        if (line.equalsIgnoreCase("\u001B")){
                            myWriter.write("");
                        }else {
                            myWriter.write(line);
                        }
//                        return line;
                        Thread.sleep(500);
                    }
//                    if(line.contains("logout")){
//                        break;
//                    }
                    if (channel.isClosed()){
                        System.out.println("exit-status: "+channel.getExitStatus());
                        myWriter.write("exit-status: "+channel.getExitStatus());
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ee){}
                }
                myWriter.close();
            }catch(Exception e){
                return ("Error while reading channel output: "+ e);
            }
        return finalResult;
    }
 */
    @Async
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
    @Async
  public String newCommand(List<String> commands, Long id, String IP, Channel channel) throws IOException {
        FileWriter myWriter = new FileWriter("File/filename_"+id+".txt");
        System.out.println("tostring "+commands.toString());
//        System.out.println("size "+commands.size());
        try{
            OutputStream out = channel.getOutputStream();
            for(String command : commands){
                if (command.contains("sudo")){

                }else {
                    out.write((command + "\n").getBytes());
                }
            }
            out.write(("exit"+ "\n").getBytes());
            out.flush();
        }catch(Exception e){
            System.out.println("Error while sending commands: "+ e);
        }
        try{
            OutputStream out = channel.getOutputStream();
            byte[] buffer = new byte[1024];
            InputStream in = channel.getInputStream();
            while (true){
                while (in.available() > 0) {
                    int i = in.read(buffer, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    String line = new String(buffer, 0, i);
                    finalResult = line;
                    //off
                    log.info(line);

                    if (line.equalsIgnoreCase("\u001B")){
                        myWriter.write("");
                    }else {
                        myWriter.write(line);
                    }
                    Thread.sleep(500);
                }

                if (channel.isClosed()){
                    System.out.println("exit-status: "+channel.getExitStatus());
                    myWriter.write("exit-status: "+channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee){}
            }
            myWriter.close();
        }catch(Exception e){
            return ("Error while reading channel output: "+ e);
        }
        return finalResult;
    }

    public String putFile2(String localFile, String remoteDirFile, Channel sftp){
        try{
            System.out.println("Putting file");
//            Channel sftp  = getSession().openChannel("sftp");
            // 2 seconds timeout
            sftp.connect(2000);
            ChannelSftp channelSftp = (ChannelSftp) sftp;
/*
            channelSftp.chmod(Integer.parseInt("777",8), "/etc/nginx/sites-enabled/event.conf.save");
            channelSftp.chmod(0770, "/etc/nginx/sites-enabled/event.conf.save");
            channelSftp.chown(777, "/etc/nginx/sites-enabled/event.conf");
            channelSftp.chgrp(777, "/etc/nginx/sites-enabled/event.conf");
*/
            // transfer file from local to remote server
            channelSftp.put(localFile, remoteDirFile);

            // download file from remote server to local
//            channelSftp.get(remoteFile, localDir + "here.txt");

            channelSftp.exit();
            System.out.println("Done");
            return "DONE";
        }catch (Exception e){
            return e.toString();
        }
    }
    public String putFile(String localFile, String remoteDirFile){
        try{
            System.out.println("Putting file");
            Channel sftp  = getSession().openChannel("sftp");
            // 2 seconds timeout
            sftp.connect(2000);

            ChannelSftp channelSftp = (ChannelSftp) sftp;

            // transfer file from local to remote server
            channelSftp.put(localFile, remoteDirFile);

            channelSftp.exit();
            System.out.println("Done");
            return "DONE";
        }catch (Exception e){
            return e.toString();
        }
    }
    public String getFileGit(String remoteFile, String localDirFile){
        try{
            Channel sftp  = getSession().openChannel("sftp");
            // 2 seconds timeout
            sftp.connect(2000);

            ChannelSftp channelSftp = (ChannelSftp) sftp;
            channelSftp.cd("./whatsappWorker");
            // transfer file from local to remote server
//            channelSftp.put(localFile, remoteDirFile);

            // download file from remote server to local
            channelSftp.get(remoteFile, localDirFile );

            channelSftp.exit();
            return "DONE";
        }catch (Exception e){
            return e.toString();
        }

    }
    public String getFile(String remoteFile, String localDirFile){
        try{
            Channel sftp  = getSession().openChannel("sftp");
            // 2 seconds timeout
            sftp.connect(2000);
            ChannelSftp channelSftp = (ChannelSftp) sftp;
//            channelSftp.cd("./whatsappWorker");
            // transfer file from local to remote server
//            channelSftp.put(localFile, remoteDirFile);

            // download file from remote server to local
            channelSftp.get(remoteFile, localDirFile );

            channelSftp.exit();
            return "DONE";
        }catch (Exception e){
            return e.toString();
        }

    }
    public String getFileEnv(String remoteFile, String localDirFile){
        try{
            Channel sftp  = getSession().openChannel("sftp");
            // 2 seconds timeout
            sftp.connect(2000);

            ChannelSftp channelSftp = (ChannelSftp) sftp;
            channelSftp.cd("./etc/nginx/sites-enabled");
            // transfer file from local to remote server
//            channelSftp.put(localFile, remoteDirFile);

            // download file from remote server to local
            channelSftp.get(remoteFile, localDirFile );

            channelSftp.exit();
            return "DONE";
        }catch (Exception e){
            return e.toString();
        }

    }
    private static void sendCommands(Channel channel, List<String> commands){

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

    private String readChannelOutput(Channel channel){
        try{
            byte[] buffer = new byte[1024];

            InputStream in = channel.getInputStream();
            String line = "";
            while (true){
                while (in.available() > 0) {
                    int i = in.read(buffer, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    line = new String(buffer, 0, i);
                    finalResult = line;
                System.out.println(line);
                }

                if(line.contains("logout")){
                    break;
                }

                if (channel.isClosed()){
                    break;
                }
                try {
//                    Thread.sleep(1000);
                } catch (Exception ee){}
            }
        }catch(Exception e){
            return ("Error while reading channel output: "+ e);
        }
        return finalResult;
    }

    public void close(){
        channel.disconnect();
        session.disconnect();
        System.out.println("Disconnected channel and session");
    }


    public void shellCommand(String command){
        List<String> commands = new ArrayList<String>();
        commands.add(command);

        executeCommands(commands);
        close();
    }
}

class localUserInfo implements UserInfo{
    String passwd;
    public String getPassword(){ return passwd; }
    public boolean promptYesNo(String str){return true;}
    public String getPassphrase(){ return null; }
    public boolean promptPassphrase(String message){return true; }
    public boolean promptPassword(String message){return true;}
    public void showMessage(String message){}
}