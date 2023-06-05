package com.dart.ssh.config;

import com.dart.ssh.Entity.SSH;
import com.dart.ssh.Service.ServiceShell;
import com.dart.ssh.repository.SshRepository;
import com.google.common.io.Files;
import com.jcraft.jsch.JSchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@Slf4j
@Async
public class ScheduleCopy {
    //    static final Logger logg = Logger.getLogger(Configuration.class.getName());
    @Autowired
    private SshRepository repo;
    @Autowired
    SSHClientConfiguration configuration;
    @Autowired
    ServiceShell serviceShell;

    //        @Scheduled(cron = "0 */1 * * * *")
//        @Scheduled(fixedDelay = 1000000)
    public void schedulerHari() throws JSchException, InterruptedException, IOException {
        List<SSH> findAll = repo.findAll();
        log.info(findAll.toString());

        int i = 1;
        for (SSH s : findAll) {
            File myObj = new File("filename_"+s.getId()+".txt");
            if (s.getStatus()== 0){
                configuration.connect("app", s.getIp(), Integer.valueOf(s.getPort()), "qwertywagwbc2");
                configuration.startClient();
                configuration.session.setTimeout(1000);
                String[] cmd = s.getCommand().split(";");

                FileWriter myWriter = new FileWriter("File/filename_"+s.getId()+".txt");
                myWriter.write(configuration.newCommand(s.getCommand()));
                myWriter.close();
                System.out.println("Successfully wrote to the file.");

                String fol = configuration.finalResult;
                SSH ssh = repo.findById(s.getId()).get();
                ssh.setStatus(1L);
                ssh.setResult(myObj.getPath());
                repo.save(ssh);

                configuration.putFile("File/"+myObj.getPath(),"tes/");
                configuration.stopClient();
                log.info("end");
                Thread.sleep(3000);
            }else {
                log.info("skip");
            }
        }
    }

    @Scheduled(cron = "0 */5 * * * *")
    public void schedulerHari2() throws JSchException, InterruptedException, IOException {
        List<SSH> findAll = repo.findAll();
        log.info(findAll.toString());

        int i = 1;
        for (SSH s : findAll) {
            if (s.getStatus()== 0){

                File myObj = new File("filename_"+s.getId()+".txt");
                serviceShell.username="app";
                serviceShell.hostname=s.getIp();
                serviceShell.password="qwertywagwbc2";
                serviceShell.port = Integer.valueOf(s.getPort());
                List<String> commands = new ArrayList<>();
                List<String> sudoCommands = new ArrayList<>();
                String[] split = s.getCommand().split(";");
                for (String q : split) {
                    if (q.startsWith(" sudo")) {
                        sudoCommands.add(q);
                    } else {
                        commands.add(q);
                    }
                }
//                serviceShell.newCommand(commands,s.getId(),s.getIp());

        /*        serviceShell.getFileGit(".env", "File/.env"+s.getId());
                serviceShell.tesFileReplace("File/.env"+s.getId(), "REDIS_HOST=172.104.58.42","REDIS_HOST="+s.getIp());
                serviceShell.putFile("File/.env"+s.getId(),"whatsappWorker/.env");
                serviceShell.putFile("File/.env","waAgentGithub/.env");

                File copied = new File("File/event.conf"+s.getId());
                Path ori =  Paths.get("File/event.conf");
                Files.copy(ori.toFile(), copied);

//                serviceShell.getFileGit("/etc/nginx/sites-enabled/event.conf", "File/event.conf_"+s.getId());
                serviceShell.tesFileReplace("File/event.conf"+s.getId(), "server_name 103.173.75.66","server_name "+s.getIp());
                serviceShell.putFile("File/event.conf"+s.getId(),"/etc/nginx/sites-enabled/event.conf");

                String sudoCommand = String.join(";", sudoCommands);

                System.out.println(sudoCommand);
//                serviceShell.sudoCommand(sudoCommand,s.getId());
                serviceShell.putFile("File/"+myObj.getPath(),"output/");
                serviceShell.close();
                System.out.println("Successfully wrote to the file.");
*/
                SSH ssh = repo.findById(s.getId()).get();
                ssh.setStatus(1L);
                ssh.setResult(myObj.getPath());
//                    repo.save(ssh);

//                configuration.stopClient();
                log.info("end");
                Thread.sleep(3000);

            }else {
                log.info("skip");
            }
        }
    }

    //    @Scheduled(fixedDelay = 100000000)
    public void schedulerHari3() throws JSchException, InterruptedException, IOException {
        List<SSH> findAll = repo.findAll();
        log.info(findAll.toString());

        int i = 1;
        for (SSH s : findAll) {
            if (s.getStatus()== 0){
                File myObj = new File("filename_"+s.getId()+".txt");
                serviceShell.username="app";
                serviceShell.hostname=s.getIp();
                serviceShell.password="qwertywagwbc2";
                serviceShell.port = Integer.valueOf(s.getPort());
                List<String> commands = new ArrayList<>();
                List<String> sudoCommands = new ArrayList<>();
                String[] split = s.getCommand().split(";");
                for (String q : split) {
                    System.out.println(q);
                    if (q.startsWith("sudo")){
                        sudoCommands.add(q);
                    }else {
                        commands.add(q);
                    }
                }
//                    serviceShell.newCommand(commands,s.getId(), s.getIp());
                for (String sudo : sudoCommands){
//                    serviceShell.sudoCommand(sudo,s.getId());
                }
//                    serviceShell.newCommand(Collections.singletonList(s.getCommand()), s.getId(), s.getIp());
//                    System.out.println("\n nano \n");
            /*    serviceShell.getFileGit(".env", "File/.env"+s.getId());
                serviceShell.tesFileReplace("File/.env"+s.getId(), "REDIS_HOST=172.104.58.42","REDIS_HOST="+s.getIp());
                serviceShell.putFile("File/.env"+s.getId(),"whatsappWorker/.env");
                serviceShell.putFile("File/.env","waAgentGithub/.env");


                serviceShell.putFile("File/"+myObj.getPath(),"output/");
*/
                serviceShell.close();
                System.out.println("Successfully wrote to the file.");

                SSH ssh = repo.findById(s.getId()).get();
                ssh.setStatus(1L);
                ssh.setResult(myObj.getPath());
//                    repo.save(ssh);

//                configuration.stopClient();
                log.info("end");
                Thread.sleep(3000);
            }else {
                log.info("skip");
            }
        }
    }
}
