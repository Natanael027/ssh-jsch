package com.dart.ssh.config;

import com.dart.ssh.Entity.SSH;
import com.dart.ssh.Service.ServiceCommand;
import com.dart.ssh.Service.ServiceShell;
import com.dart.ssh.repository.SshRepository;
import com.google.common.io.Files;
import com.jcraft.jsch.JSchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.query.Param;
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
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Slf4j
public class Scheduler {
//    static final Logger logg = Logger.getLogger(Configuration.class.getName());
    @Autowired
    private SshRepository repo;
    @Autowired
    ServiceCommand serviceCommand;

//    @Scheduled(cron = "0 */1 * * * *")
//    @Scheduled(fixedDelay = 60000)
//    @Scheduled(cron = "${app.scheduler.config}")
    public void Command() throws JSchException, IOException, InterruptedException {
        List<SSH> findAll = repo.findAll();
        log.info("\nTotal : "+findAll.size());

        for (int i = 1; i<findAll.size();i=i+3) {
            System.out.println("loop :: "+i);
            SSH s = repo.findByIds((long) i);
            SSH s2 = repo.findByIds((long) i + 1);
            SSH s3 = repo.findByIds((long) i + 2);

//            System.out.println(s.getId()+"\n"+s2.getId()+"\n"+s3.getId()+"\n"+"\n");
//            CompletableFuture<String> command = new CompletableFuture<>();
           /*
            CompletableFuture<String> cars1=serviceCommand.onProgress();
            CompletableFuture<String> cars2=serviceCommand.onProgress();
            CompletableFuture<String> cars3=serviceCommand.onProgress();

            CompletableFuture.allOf(cars1, cars2, cars3).join();
            */

            CompletableFuture<String> task1= serviceCommand.task(s);
            CompletableFuture<String> task2= serviceCommand.task(s2);
            CompletableFuture<String> task3= serviceCommand.task(s3);

            CompletableFuture.allOf(task1, task2, task3).join();

//            Thread.sleep(2000);
            }
        }

}
