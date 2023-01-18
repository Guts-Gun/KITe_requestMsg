package gutsandgun.kite_requestmsg.repository.write;

import gutsandgun.kite_requestmsg.entity.write.SendingEmail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WriteSendingEmailRepository extends JpaRepository<SendingEmail, Long> {
}
