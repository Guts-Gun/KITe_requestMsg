package gutsandgun.kite_requestmsg.repository.read;

import gutsandgun.kite_requestmsg.entity.read.UserPhone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadUserPhoneRepository extends JpaRepository<UserPhone, Long> {
}
