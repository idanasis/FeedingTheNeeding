package Project.Final.FeedingTheNeeding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "Project.Final.FeedingTheNeeding")
public class FeedingTheNeedingApplication {
	public static void main(String[] args) {
		SpringApplication.run(FeedingTheNeedingApplication.class, args);
	}

}
