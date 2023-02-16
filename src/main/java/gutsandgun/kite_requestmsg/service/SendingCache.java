package gutsandgun.kite_requestmsg.service;

import gutsandgun.kite_requestmsg.entity.write.SendingEmail;
import gutsandgun.kite_requestmsg.entity.write.SendingMsg;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class SendingCache {


    @Cacheable(value="sendingMsg" , key = "#id" ,cacheManager = "redisCacheManager")
    public SendingMsg insertSendingMsg(Long id, SendingMsg sendingMsg){
        return sendingMsg;
    }

    @Cacheable(value="sendingEmail" , key = "#id" ,cacheManager = "redisCacheManager")
    public SendingEmail insertSendingEmail(Long id, SendingEmail sendingEmail){
        return sendingEmail;
    }

}

