package com.dart.ssh;

import com.dart.ssh.Entity.SSH;
import com.dart.ssh.Service.ServiceCommand;
import com.dart.ssh.Service.ServiceShell;
import com.dart.ssh.config.SSHClientConfiguration;
import com.dart.ssh.repository.SshRepository;
import com.jcraft.jsch.JSchException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@SpringBootTest
class SshApplicationTests {

	@Autowired
	private SshRepository repo;
	@Autowired
	ServiceCommand serviceCommand;

	@Test
	void testing() throws IOException, JSchException, InterruptedException {
			SSH s = repo.findByIds((long) 1);

			CompletableFuture<String> task1= serviceCommand.task(s);
			CompletableFuture.allOf(task1).join();

			System.out.println("DONE");

	}


}


