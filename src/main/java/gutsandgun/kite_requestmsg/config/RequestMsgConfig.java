

package gutsandgun.kite_requestmsg.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RequestMsgConfig {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}
