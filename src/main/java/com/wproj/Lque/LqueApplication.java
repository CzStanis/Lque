package com.wproj.Lque;

import com.wproj.Lque.service.RiotService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;


@SpringBootApplication
@EnableFeignClients
public class LqueApplication {

	public static void main(String[] args) {
		SpringApplication.run(LqueApplication.class, args);
	}
	@Bean
	public CommandLineRunner commandLineRunner(RiotService riotService) {
		return args -> {
			Scanner scanner = new Scanner(System.in);

			System.out.println("Enter gameName:");
			String gameName = scanner.nextLine();

			System.out.println("Enter tagLine:");
			String tagLine = scanner.nextLine();

			scanner.close();

			String puuid = riotService.getPuuidByName(gameName, tagLine);
			String matchId = riotService.getMatchId(puuid);
			riotService.getStats(matchId, puuid);
		};
	}

}
