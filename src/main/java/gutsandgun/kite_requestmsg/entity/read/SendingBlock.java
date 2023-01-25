package gutsandgun.kite_requestmsg.entity.read;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * 수신 차단 테이블
 */
@Entity
@Getter
@Setter
@Where(clause = "is_deleted = false")
@SQLDelete(sql = "UPDATE sending_block SET is_deleted=true WHERE id = ?")
public class SendingBlock {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	/**
	 * 수신 차단할 발신자 주소
	 */
	@Comment("수신 차단할 발신자 주소")
	private String sender;

	/**
	 * 수신 차단 할 수신자 주소
	 */
	@Comment("수신 차단할 수신자 주소")
	private String receiver;

	/**
	 * 차단 시간
	 */
	@Comment("차단 시간")
	private Long blockTime;

	    @ColumnDefault("false")
	private Boolean isDeleted = false;
}
