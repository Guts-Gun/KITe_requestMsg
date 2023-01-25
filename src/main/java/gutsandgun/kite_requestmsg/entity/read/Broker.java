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
@SQLDelete(sql = "UPDATE broker SET is_deleted=true WHERE id = ?")
public class Broker {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/**
	 * 중계사 이름
	 */
	@Comment("중계사 이름")
	private String name;

	/**
	 * 중계사 ip
	 */
	@Comment("중계사 ip")
	private String ip;

	/**
	 * 중계사 가격
	 */
	@Comment("중계사 가격")
	private Float price;

	/**
	 * 중계사 속도
	 */
	@Comment("중계사 속도")
	private Float speed;

	/**
	 * 중계사 실패율
	 */
	@Comment("중계사 실패율")
	private Float failureRate;

	@ColumnDefault("false")
	private Boolean isDeleted = false;
}
