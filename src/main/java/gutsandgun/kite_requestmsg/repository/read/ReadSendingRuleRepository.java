package gutsandgun.kite_requestmsg.repository.read;

import gutsandgun.kite_requestmsg.entity.read.SendingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadSendingRuleRepository extends JpaRepository<SendingRule, Long> {
}
