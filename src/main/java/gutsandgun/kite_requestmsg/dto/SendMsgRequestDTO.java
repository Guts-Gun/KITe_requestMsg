package gutsandgun.kite_requestmsg.dto;

import gutsandgun.kite_requestmsg.entity.SendingRuleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMsgRequestDTO {

    List<Map<String,String>> receiverList;  // 수신자 리스트
        // name                                수신자 이름
        // receiver                            수신자 정보 (전화번호/이메일)
        // replace_receiver                    대체발송 수신자 정보 (전화번호/이메일)

    SendingDTO sendingDTO;                  // 발송 정보
        // ruleType                            중계사 규칙 타입
        // sendingType                         발송 타입
        // replaceYn                           대체발송 여부
        // totalSending                        총 발송 메세지 갯수
        // requestTime                         등록 시간
        // scheduleTime                        예약 시간
        // title                               제목
        // content                             메세지 내용
        // mediaLink                           미디어 링크

    String reservationYn;                   // 예약 여부
    String sender;                          // 발신자 정보 (전화번호)
    String replaceSender;                   // 대체발송 발신자 정보
    Integer contentLength;                  // 메세지 길이
    List<SendingRuleDTO> brokerList;        // 중계사 비율 리스트

}

