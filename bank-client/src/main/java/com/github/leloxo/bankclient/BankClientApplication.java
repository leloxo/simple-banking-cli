package com.github.leloxo.bankclient;

import com.github.leloxo.bankclient.cli.CliHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class BankClientApplication {
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(BankClientApplication.class, args);
		CliHandler cliHandler = context.getBean(CliHandler.class);
		cliHandler.run();
	}
}
