package gutsandgun.kite_requestmsg.entity.read;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * group-address relation table
 */
@Entity
@Getter
@Setter
@Where(clause = "is_deleted = false")
@SQLDelete(sql= "UPDATE address_group SET is_deleted=true WHERE id = ?")
public class AddressGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /**
     * 주소록 ID
     */
    @Column(name = "fk_user_address_id")
    @Comment("주소록 ID")
    private Long userAddressId;

    /**
     * 그룹 ID
     */
    @Column(name = "fk_user_group_id")
    @Comment("그룹 ID")
    private Long userGroupId;

    @ColumnDefault("false")
    private Boolean isDeleted = false;
}
