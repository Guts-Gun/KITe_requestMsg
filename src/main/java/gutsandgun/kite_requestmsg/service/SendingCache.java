package gutsandgun.kite_requestmsg.service;

import gutsandgun.kite_requestmsg.entity.write.SendingEmail;
import gutsandgun.kite_requestmsg.entity.write.SendingMsg;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SendingCache {


    @Cacheable(value = "sendingMsg", key = "#id", cacheManager = "CacheManager")
    public SendingMsg insertSendingMsg(Long id, SendingMsg sendingMsg) {
        return sendingMsg;
    }

    @Cacheable(value = "sendingMsg", key = "#sendingId", cacheManager = "CacheManager")
    public List<SendingMsg> insertSendingMsgList(Long sendingId, List<SendingMsg> sendingMsgList) {
        return sendingMsgList;
    }

    @Cacheable(value = "sendingEmail", key = "#id", cacheManager = "CacheManager")
    public SendingEmail insertSendingEmail(Long id, SendingEmail sendingEmail) {
        return sendingEmail;
    }

    @Cacheable(value = "sendingEmail", key = "#sendingId", cacheManager = "CacheManager")
    public List<SendingEmail> insertSendingEmailList(Long sendingId, List<SendingEmail> SendingEmailList) {
        return SendingEmailList;
    }
}