package com.dart.ssh.Service;

import com.dart.ssh.Entity.SSH;
import com.dart.ssh.config.SSHClientConfiguration;
import com.dart.ssh.repository.SshRepository;
import com.google.common.io.Files;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class ServiceCommand {
    @Autowired
    private SshRepository repo;
    @Autowired
    SSHClientConfiguration configuration;

    public List<SSH> testFind(Long id1, Long id2, Long id3){
        List<SSH> tes = repo.findStatus0(id1,id2,id3);
        return tes;
    }

    @Async
    public CompletableFuture<String> task(SSH s) throws JSchException, IOException, InterruptedException {
        CompletableFuture<String> command = new CompletableFuture<>();
        log.info("id future"+String.valueOf(s.getId()) );
        if (s.getStatus()== 0) {
            log.info("Start");
            ServiceShell shell = new ServiceShell();
            shell.setUsername("app");
            shell.setHostname(s.getIp());
            shell.setPassword("qwertywagwbc2");
            shell.setChannel((ChannelShell) shell.getChannel());
            shell.setPort(Integer.valueOf(s.getPort()));

            command = schedulerHari2(s,shell);
            log.info("Finished");
        } else if (s.getStatus()==1) {
            log.info("On Progress");
            command = onProgress();
        }
        else {
            log.info("Done");
            command = Done();
        }

        return command;
    }

    @Async
    public CompletableFuture<String> schedulerHari2(SSH s, ServiceShell shell) throws JSchException, InterruptedException, IOException {
        //Generate File for result detail
            File myObj = new File("filename_"+s.getId()+".txt");
            log.info("ID >>"+String.valueOf(s.getId()));
            List<String> commands = new ArrayList<>();
            List<String> sudoCommands = new ArrayList<>();
            String[] split = s.getCommand().split(";");
            for (String q : split) {
                commands.add(q);
            }
/*
                if (q.startsWith(" sudo")) {
                    sudoCommands.add(q);
                } else {
                    commands.add(q);
                }
*/

            System.out.println(commands.toString());
            Channel channel = shell.getChannel();
            Channel channelSudo = shell.getSession().openChannel("exec");

            shell.newCommand(commands,s.getId(),s.getIp(), channel);
            shell.sudoCommandFile(s.getId());
            System.out.println("sudoCommand :: "+sudoCommands.toString());
            String sudoCommand = String.join(";", sudoCommands);
//            cmd sudo-cmd
            log.info(sudoCommand);
            if (!sudoCommands.isEmpty()){
                shell.sudoCommand(sudoCommand,s.getId(),channelSudo);
            }

            shell.close();
            log.info("Successfully wrote to the file.");

            SSH ssh = repo.findById(s.getId()).get();
//            ssh.setStatus(1L);
            ssh.setResult(myObj.getPath());
            repo.save(ssh);
            log.info("end");

            return CompletableFuture.completedFuture("ended");
        }


//    @Async
    public CompletableFuture<String> onProgress(){
        log.info("test");
        return CompletableFuture.completedFuture("OnProgress ended");
    }
//    @Async
    public CompletableFuture<String> Done(){
        return CompletableFuture.completedFuture("Done ended");
    }
}
