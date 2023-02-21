package gutsandgun.kite_requestmsg.entity.write;

import gutsandgun.kite_requestmsg.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
//@Where(clause = "is_deleted = false")
//@SQLDelete(sql= "UPDATE sending_msg SET is_deleted=true WHERE id = ?")
@Table(name="send_replace")
public class SendReplace extends BaseTimeEntity {

    @Id
    @Column(name = "fk_tx_id")
    private Long id;

    private String receiver;

    private String sender;

    @Comment("생성자")
    @Column(name = "reg_id", nullable = false, length = 20)
    private String regId;

    @Comment("수정자")
    @Column(name = "mod_id", length = 20)
    private String modId;

}
