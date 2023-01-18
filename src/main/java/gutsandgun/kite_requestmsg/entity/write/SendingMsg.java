package gutsandgun.kite_requestmsg.entity.write;

import gutsandgun.kite_requestmsg.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Where(clause = "is_deleted = false")
@SQLDelete(sql= "UPDATE sending_msg SET is_deleted=true WHERE id = ?")
@Table(name="sending_msg")
@Builder
public class SendingMsg extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "fk_sending_id")
    private Long sendingId;

    private String sender;

    private String receiver;

    private String name;

    @Comment("생성자")
    @Column(name = "reg_id", nullable = false, length = 20)
    private String regId;

    @Comment("수정자")
    @Column(name = "mod_id", length = 20)
    private String ModId;

    private String var1;
    private String var2;
    private String var3;

    private Boolean isDeleted = false;

}
