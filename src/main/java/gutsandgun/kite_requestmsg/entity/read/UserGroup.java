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
@SQLDelete(sql= "UPDATE user_group SET is_deleted=true WHERE id = ?")
@Table(name="user_group")
public class UserGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

	/**
	 * user id
	 */
	@Column(name = "fk_user_id")
	@Comment("user id")
	private String userId;

    /**
     * 그룹 이름
     */
    @Comment("그룹 이름")
    private String groupName;

	@ColumnDefault("false")
    private Boolean isDeleted = false;
}
