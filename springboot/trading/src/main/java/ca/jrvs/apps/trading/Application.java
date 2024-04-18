package ca.jrvs.apps.trading;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// @EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class Application {

    public static void main(String[] args) {

        System.out.println("Initializing Trading Application...");

        // Creates the servlet container and hosts the application on Tomcat.
        SpringApplication.run(Application.class, args);
    }
}