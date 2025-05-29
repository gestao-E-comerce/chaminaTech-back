package chaminaTech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ChaminaTechApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChaminaTechApplication.class, args);
	}

}
