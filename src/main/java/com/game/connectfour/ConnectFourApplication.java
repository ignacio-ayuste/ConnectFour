package com.game.connectfour;

import com.game.connectfour.config.SwaggerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(SwaggerConfig.class)
@ComponentScan(basePackages = "com.game")
public class ConnectFourApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConnectFourApplication.class, args);
	}

}