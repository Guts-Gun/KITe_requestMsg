package gutsandgun.kite_requestmsg.controller;

import gutsandgun.kite_requestmsg.dto.SendMsgRequestDTO;
import gutsandgun.kite_requestmsg.dto.SendingDTO;
import gutsandgun.kite_requestmsg.service.MsgService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value="request")
@Log4j2
@RequiredArgsConstructor
public class RequestController {
    private final MsgService msgService;

    /**
     * 발송 시작
     *
     * @author solbiko
     * @param principal 로그인 객체
     * @param sendMsgRequestDTO 발송 요청 정보
     * @return long sendingId
     */

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/sendReq")
    public ResponseEntity<String> requestMsg(Principal principal, @RequestBody SendMsgRequestDTO sendMsgRequestDTO) {

        JwtAuthenticationToken token = (JwtAuthenticationToken) principal;
        String userId = token.getTokenAttributes().get("preferred_username").toString();

        SendingDTO sendingDTO = sendMsgRequestDTO.getSendingDTO();
        String sendingType = sendingDTO.getSendingType();

        if(sendingType.equals("SMS")){
            msgService.insertSendingMsg(userId, sendMsgRequestDTO);
        } else if (sendingType.equals("EMAIL")){

        }

        return new ResponseEntity<>("Success", HttpStatus.OK);
    }


    /**
     * 발송 요청 엑셀 업로드 샘플파일 다운로드
     *
     * @author solbiko
     */
    @GetMapping(value = "/download/excel", produces = "application/vnd.ms-excel")
    public void excelDownload(HttpServletResponse res) throws UnsupportedEncodingException, ParseException {

        List<String> headerList = new ArrayList<>();
        headerList.add("NAME");
        headerList.add("PHONE");
        headerList.add("EMAIL");

        msgService.downloadSampleFile(res, headerList);
    }

}
