package booking.spring.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer  // включает функциональность сервера конфигураций
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}