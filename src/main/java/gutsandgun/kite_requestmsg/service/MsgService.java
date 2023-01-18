package gutsandgun.kite_requestmsg.service;

import gutsandgun.kite_requestmsg.dto.SendMsgRequestDTO;
import gutsandgun.kite_requestmsg.dto.SendingMsgDTO;
import gutsandgun.kite_requestmsg.entity.write.SendingMsg;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.Map;

public interface MsgService {

    @Transactional
    void insertSendingMsg(String userId, SendMsgRequestDTO sendMsgRequestDTO); // 문자발송 요청 저장

    void downloadSampleFile (HttpServletResponse response,  List<String> headerList); // 샘플파일 엑셀다운로드

}
