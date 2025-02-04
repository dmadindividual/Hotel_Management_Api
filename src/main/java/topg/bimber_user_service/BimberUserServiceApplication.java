package topg.bimber_user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
public class  BimberUserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BimberUserServiceApplication.class, args);
	}

}
