package gutsandgun.kite_requestmsg.repository.read;

import gutsandgun.kite_requestmsg.entity.read.AddressPhone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadAddressPhoneRepository extends JpaRepository<AddressPhone, Long> {
}
