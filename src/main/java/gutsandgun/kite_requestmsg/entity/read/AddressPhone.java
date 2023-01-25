package gutsandgun.kite_requestmsg.entity.read;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@Where(clause = "is_deleted = false")
@SQLDelete(sql= "UPDATE address_phone SET is_deleted=true WHERE id = ?")
public class AddressPhone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 주소록 ID
     */
    @Column(name = "fk_user_address_id")
    @Comment("주소록 ID")
    private String userAddressId;

    /**
     * 전화번호
     */
    @Comment("전화번호")
    private String phone;

    @ColumnDefault("false")
    private Boolean isDeleted = false;
}
