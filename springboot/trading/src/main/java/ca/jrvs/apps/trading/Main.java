package ca.jrvs.apps.trading;

import ca.jrvs.apps.trading.dto.IexQuote;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@SpringBootApplication
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class Main {

    public static void main(String[] args) {

        // Press Shift+F10 or click the green arrow button in the gutter to run the code.
        System.out.println("Initializing Trading Application...");

        // Creates the servlet container and hosts the application there.
        SpringApplication.run(Main.class, args);
    }
}
