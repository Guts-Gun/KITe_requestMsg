package gutsandgun.kite_requestmsg.entity.write;

import gutsandgun.kite_requestmsg.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@Where(clause = "is_deleted = false")
@SQLDelete(sql= "UPDATE sending_email SET is_deleted=true WHERE id = ?")
@Table(name = "sending_email",
        indexes = {
                @Index(name = "idx_sending_email_sending_id", columnList = "fk_sending_id")
        })
public class SendingEmail extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "fk_sending_id")
    private Long sendingId;

    private String sender;

    private String receiver;

    private String title;

    private String contents;

    @Comment("생성자")
    @Column(name = "reg_id", nullable = false, length = 20)
    private String regId;


    private String var1;
    private String var2;
    private String var3;

    private Boolean isDeleted = false;

    @Comment("수정자")
    @Column(name = "mod_id", length = 20)
    private String modId;

}
