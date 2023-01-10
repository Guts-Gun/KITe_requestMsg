package gutsandgun.kite_requestmsg.repository.write;

import gutsandgun.kite_requestmsg.entity.write.SendingMsg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WriteSendingMsgRepository extends JpaRepository<SendingMsg, Long> {
}
