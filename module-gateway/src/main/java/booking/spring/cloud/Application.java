package booking.spring.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.server.adapter.ForwardedHeaderTransformer;

@SpringBootApplication
@EnableFeignClients
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

//  @Bean
//  public ForwardedHeaderTransformer transformer() {
//    ForwardedHeaderTransformer transformer = new ForwardedHeaderTransformer();
//    transformer.setRemoveOnly(true);
//    return transformer;
//  }
}
