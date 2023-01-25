package gutsandgun.kite_requestmsg.entity.read;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * 발신 전화번호 저장 테이블
 */
@Entity
@Getter
@Setter
@Where(clause = "is_deleted = false")
@SQLDelete(sql = "UPDATE user_phone SET is_deleted=true WHERE id = ?")
@Table(name = "user_phone")
public class UserPhone {

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
	 * 발신 전화번호
	 */
	@Comment("발신 전화번호")
	private String phone;

	@ColumnDefault("false")
	private Boolean isDeleted = false;
}
