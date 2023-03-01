package com.dart.ssh;

import com.dart.ssh.config.Scheduler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaRepositories
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
public class SshApplication {

	public static void main(String[] args) {
		SpringApplication.run(SshApplication.class, args);
	}
	@Bean
	@ConditionalOnProperty(value = "jobs.enabled", matchIfMissing = true, havingValue = "true")
	public Scheduler scheduledJob() {
		return new Scheduler();
	}
}
