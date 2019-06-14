package com.mastermind.codebreaker;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

@SpringBootApplication
public class MastermindCodebreakerApplication implements CommandLineRunner {

	@Autowired
	@Qualifier("breaking.command")
	private BreakingCommand breakingCommand;

	public static void main(String[] args) {
		SpringApplication.run(MastermindCodebreakerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Scanner sc = new Scanner(System.in);
		String line = sc.nextLine();
		String argArr[] = line.split(" ");
		if(argArr.length == 4 ){
			int D = Integer.parseInt(argArr[0]); //range
			int N = Integer.parseInt(argArr[1]); //digit number
			int maxTurn = Integer.parseInt(argArr[2]);
			long maxTime = Long.parseLong(argArr[3]);

			//final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
			//scheduler.scheduleAtFixedRate(this::startBreaking, maxTime, maxTime, TimeUnit.MILLISECONDS);
			breakingCommand.startBreaking(D, N, maxTurn, maxTime);
		}
	}

}