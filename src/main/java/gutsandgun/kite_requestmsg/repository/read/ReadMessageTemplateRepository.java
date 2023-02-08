package gutsandgun.kite_requestmsg.repository.read;

import gutsandgun.kite_requestmsg.entity.read.MessageTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadMessageTemplateRepository extends JpaRepository<MessageTemplate, Long> {

    Page<MessageTemplate> findByUserId (String userId, Pageable pageable);

}
