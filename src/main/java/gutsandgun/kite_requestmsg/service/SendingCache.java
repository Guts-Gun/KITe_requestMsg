package gutsandgun.kite_requestmsg.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gutsandgun.kite_requestmsg.dto.SendingMsgDTO;
import gutsandgun.kite_requestmsg.entity.read.SendingMsg;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class SendingCache {

    ObjectMapper objectMapper = new ObjectMapper();

    @Cacheable(value = "sendingMsg", key = "#sendingId", cacheManager = "CacheManager")
    public List<String> insertSendingMsgList(Long sendingId,  List<SendingMsgDTO> sendingMsgDTOList) throws JsonProcessingException {
        List<String> list = new ArrayList<>();
        sendingMsgDTOList.forEach(sendingMsgDTO -> {
            try {
                String str = objectMapper.writeValueAsString(sendingMsgDTO);
                list.add(str);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        log.info("==================================================");
        log.info("Cacheable" + list );
        log.info("==================================================");
        return list;
    }

}