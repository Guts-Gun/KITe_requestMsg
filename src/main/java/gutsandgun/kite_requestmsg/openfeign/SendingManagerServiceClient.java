package gutsandgun.kite_requestmsg.openfeign;

import gutsandgun.kite_requestmsg.dto.SendMsgRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;


@FeignClient(name = "sending-manager-client", url="${feign.url.sending-manager}", configuration = FeignConfig.class)
public interface SendingManagerServiceClient {

    @PostMapping(value = "/sending/req")
    ResponseEntity<Long> insertSending(@RequestParam("userId") String userId, @RequestBody SendMsgRequestDTO sendMsgRequestDTO);

    @PostMapping(value = "/sending/start")
    ResponseEntity<Long> startSending(@RequestBody Map<String,Long> map);

}
