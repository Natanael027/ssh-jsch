package com.dart.ssh.config;

import com.dart.ssh.Entity.SSH;
import com.dart.ssh.Service.ServiceCommand;
import com.dart.ssh.repository.SshRepository;
import com.jcraft.jsch.JSchException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class Scheduler {
//    static final Logger logg = Logger.getLogger(Configuration.class.getName());
    @Autowired
    private SshRepository repo;
    @Autowired
    ServiceCommand serviceCommand;

    /*
    *  Scheduler untuk menjalankan command yang ada di db
    * */
//    @Scheduled(cron = "0 */1 * * * *")
//    @Scheduled(fixedDelay = 60000)
//    @Scheduled(cron = "${app.scheduler.config}")
    public void Command() throws JSchException, IOException, InterruptedException {
        List<SSH> findAll = repo.findAll();
        log.info("\nTotal : "+findAll.size());

        // Menjalankan 3 command sekaligus secara async
        for (int i = 1; i<findAll.size();i=i+3) {
            System.out.println("loop :: "+i);
            SSH s = repo.findByIds((long) i);
            SSH s2 = repo.findByIds((long) i + 1);
            SSH s3 = repo.findByIds((long) i + 2);


            CompletableFuture<String> task1= serviceCommand.task(s);
            CompletableFuture<String> task2= serviceCommand.task(s2);
            CompletableFuture<String> task3= serviceCommand.task(s3);

            CompletableFuture.allOf(task1, task2, task3).join();

            }
        }


}
