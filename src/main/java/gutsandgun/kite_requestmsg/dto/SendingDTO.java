package gutsandgun.kite_requestmsg.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import gutsandgun.kite_requestmsg.type.SendingRuleType;
import gutsandgun.kite_requestmsg.type.SendingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendingDTO {

    private Long id;

    private String userId;

    private SendingRuleType sendingRuleType; // 중계사 규칙 타입

    private SendingType sendingType;        // 발송 타입

    private String replaceYn;               // 대체발송 여부

    private Long totalSending;              // 메세지 갯수

    private Long inputTime;                 // 등록 시간

    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reservationTime;  // 예약 시간

    private Long scheduleTime;              // 예약 시간

    private String title;                   // 제목

    private String mediaLink;               // 미디어 링크

    private String content;                 // 메세지 내용

    private String regId;

    private String modId;
}