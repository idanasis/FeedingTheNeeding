package Project.Final.FeedingTheNeeding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class FeedingTheNeedingApplication {
	public static void main(String[] args) {
		SpringApplication.run(FeedingTheNeedingApplication.class, args);
		if(args.length > 0 && args[0].equals("--spring.profiles.active=test")) {
			System.out.println("Running in test mode");
		} else {
			System.out.println("Running in normal mode");
		}
	}

}
