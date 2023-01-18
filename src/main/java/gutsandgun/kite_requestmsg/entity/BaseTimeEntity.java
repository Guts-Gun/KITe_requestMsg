package gutsandgun.kite_requestmsg.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {


    @Comment("생성일자")
    @CreatedDate
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "reg_dt", nullable = false)
    private LocalDateTime regDt;

    @Comment("수정일자")
    @LastModifiedDate
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "mod_dt")
    private LocalDateTime modDt;


    public LocalDateTime getCreatedDate() {
        return regDt;
    }

    public LocalDateTime getModifiedDate() {
        return modDt;
    }
}