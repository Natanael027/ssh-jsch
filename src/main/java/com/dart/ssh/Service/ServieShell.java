/*
package com.dart.ssh.Service;

import com.dart.ssh.Entity.SSH;
import com.dart.ssh.repository.SshRepository;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@Service
public class ServieShell {
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

}
*/
