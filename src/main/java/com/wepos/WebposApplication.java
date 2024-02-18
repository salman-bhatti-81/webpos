package com.wepos;

import com.wepos.authentication.AuthenticationService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WebposApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebposApplication.class, args);
		AuthenticationService.getCurrentTime();
	}

}
