package gutsandgun.kite_requestmsg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableFeignClients
@EnableJpaAuditing
@ImportAutoConfiguration({FeignAutoConfiguration.class})
public class KiTeRequestMsgApplication {

	public static void main(String[] args) {
		SpringApplication.run(KiTeRequestMsgApplication.class, args);
	}

}
