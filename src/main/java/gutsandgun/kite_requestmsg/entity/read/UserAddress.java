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
@SQLDelete(sql= "UPDATE user_address SET is_deleted=true WHERE id = ?")
@Table(name="user_address")
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

	/**
	 * 주소록 주인 user id
	 */
	@Column(name = "fk_user_id")
	@Comment("주소록 주인 user id")
	private String userId;

    /**
     * 주소록 저장된 이름
     */
    @Comment("주소록 저장된 이름")
    private String name;

	@ColumnDefault("false")
    private Boolean isDeleted = false;
}
