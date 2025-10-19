package booking.spring.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayApplication {

  public static void main(String[] args) {
    SpringApplication.run(GatewayApplication.class, args);
  }

//  @Bean
//  public ForwardedHeaderTransformer transformer() {
//    ForwardedHeaderTransformer transformer = new ForwardedHeaderTransformer();
//    transformer.setRemoveOnly(true);
//    return transformer;
//  }
}
