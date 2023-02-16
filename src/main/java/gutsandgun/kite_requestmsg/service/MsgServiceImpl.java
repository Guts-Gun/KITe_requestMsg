package gutsandgun.kite_requestmsg.service;

import gutsandgun.kite_requestmsg.dto.SendMsgRequestDTO;
import gutsandgun.kite_requestmsg.dto.SendReplaceDTO;
import gutsandgun.kite_requestmsg.dto.SendingDTO;
import gutsandgun.kite_requestmsg.dto.SendingMsgDTO;
import gutsandgun.kite_requestmsg.entity.write.SendReplace;
import gutsandgun.kite_requestmsg.entity.write.SendingEmail;
import gutsandgun.kite_requestmsg.entity.write.SendingMsg;
import gutsandgun.kite_requestmsg.openfeign.SendingManagerServiceClient;
import gutsandgun.kite_requestmsg.repository.read.ReadSendingMsgRepository;
import gutsandgun.kite_requestmsg.repository.write.WriteSendReplaceRepository;
import gutsandgun.kite_requestmsg.repository.write.WriteSendingEmailRepository;
import gutsandgun.kite_requestmsg.repository.write.WriteSendingMsgRepository;
import gutsandgun.kite_requestmsg.type.SendingType;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class MsgServiceImpl implements MsgService {

    @Autowired
    private final WriteSendingMsgRepository writeSendingMsgRepository;

    @Autowired
    private final WriteSendingEmailRepository writeSendingEmailRepository;

    @Autowired
    private final WriteSendReplaceRepository writeSendReplaceRepository;

    @Autowired
    private final SendingManagerServiceClient sendingManagerServiceClient;

    @Autowired
    private final ModelMapper mapper;

    @Autowired
    private final SendingCache sendingCache;


    @Override
    public void insertSendingMsg(String userId, SendMsgRequestDTO sendMsgRequestDTO){

        SendingDTO sendingDTO = sendMsgRequestDTO.getSendingDTO();
        sendingDTO.setInputTime(new Date().getTime());
        sendMsgRequestDTO.setSendingDTO(sendingDTO);

        // send manager insert sending
        ResponseEntity<Long> response = sendingManagerServiceClient.insertSending(userId, sendMsgRequestDTO);
        int statusCode = response.getStatusCode().value();

        Long sendingId = response.getBody();

        System.out.println("Service: request, type: genSendingId, sendingId: " + sendingId +
                ", sendingType: " + sendingDTO.getSendingRuleType() + ", ruleType: " + sendingDTO.getSendingRuleType() +
                ", total: " + sendingDTO.getTotalMessage() + ", replace: " + (sendingDTO.getReplaceYn()=="Y"? true : false) +
                ", title: " + sendingDTO.getTitle() + ", content: " + sendingDTO.getContent() + ", mediaLink: " + sendingDTO.getMediaLink() +
                ", sender: " + sendMsgRequestDTO.getSender() + ", userId: " + userId +
                ", inputTime: "+sendingDTO.getInputTime() + ", scheduleTime: " + sendingDTO.getScheduleTime()+"@"
        );

        List<SendingMsg> sendingMsgList = new ArrayList<>();
        List<SendingEmail> sendingEmailList = new ArrayList<>();


        // TX 입력
        sendMsgRequestDTO.getReceiverList().forEach(receiver -> {
            SendingMsgDTO sendingMsgDTO = new SendingMsgDTO();
            sendingMsgDTO.setSendingId(sendingId);
            sendingMsgDTO.setSender(sendMsgRequestDTO.getSender());
            sendingMsgDTO.setReceiver(receiver.get("receiver"));
            sendingMsgDTO.setName(receiver.get("name"));
            sendingMsgDTO.setRegId(userId);
            sendingMsgDTO.setVar1(null);
            sendingMsgDTO.setVar2(null);
            sendingMsgDTO.setVar3(null);

            Long id = null;
            if(sendingDTO.getSendingType().equals(SendingType.SMS) || sendingDTO.getSendingType().equals(SendingType.MMS)){
                SendingMsg sendingMsg = writeSendingMsgRepository.save(mapper.map(sendingMsgDTO, SendingMsg.class));
                id = sendingMsg.getId();
                sendingMsgList.add(sendingMsg);
//                sendingCache.insertSendingMsg(id, sendingMsg);
                System.out.println("Service: request, type: input, sendingId: " + sendingId +
                        ", TXId: "+ id + ", sender: " + sendingMsg.getSender() + ", receiver: " + sendingMsg.getReceiver()+"@");

            }else if(sendingDTO.getSendingType().equals(SendingType.EMAIL)){
                SendingEmail sendingEmail = writeSendingEmailRepository.save(mapper.map(sendingMsgDTO, SendingEmail.class));
                id = sendingEmail.getId();
                sendingEmailList.add(sendingEmail);
//                sendingCache.insertSendingEmail(id, sendingEmail);
                System.out.println("Service: request, type: input, sendingId: " + sendingId +
                        ", TXId: "+ id + ", sender: " + sendingEmail.getSender() + ", receiver: " + sendingEmail.getReceiver()+"@");
            }

            // 대체발송
            if(sendMsgRequestDTO.getSendingDTO().getReplaceYn().equals("Y")){
                receiver.put("replace_sender", sendMsgRequestDTO.getReplaceSender());
                insertSendingReplace(userId, id, receiver);
            }
        });

        if(sendingDTO.getSendingType().equals(SendingType.SMS) || sendingDTO.getSendingType().equals(SendingType.MMS)) {
            sendingCache.insertSendingMsgList(sendingId, sendingMsgList);
        }else if(sendingDTO.getSendingType().equals(SendingType.EMAIL)){
            sendingCache.insertSendingEmailList(sendingId, sendingEmailList);
        }

        // TX 입력 완료 시 send manager start sending
        Map<String, Long> map = new HashMap<>();
        map.put("sendingId", sendingId);

        if(sendMsgRequestDTO.getReservationYn().equals("N")) {
            sendingManagerServiceClient.startSending(map);
        }
    }

    public Long insertSendingReplace(String userId, Long txId, Map<String, String> receiver){

        SendReplaceDTO sendReplaceDTO = new SendReplaceDTO();
        sendReplaceDTO.setId(txId);
        sendReplaceDTO.setRegId(userId);
        sendReplaceDTO.setReceiver(userId);
        sendReplaceDTO.setReceiver(receiver.get("replace_receiver"));
        sendReplaceDTO.setSender(receiver.get("replace_sender"));

        writeSendReplaceRepository.save(mapper.map(sendReplaceDTO, SendReplace.class));

        return txId;
    }


    @Override
    public void downloadSampleFile(HttpServletResponse response, List<String> headerList) {

        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("sample");

            CellStyle numberCellStyle = workbook.createCellStyle();
            numberCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            // 파일명
            final String fileName = "sample_file";

            // 헤더
            Row row = sheet.createRow(0);
            for (int i = 0; i < headerList.size(); i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(headerList.get(i));
            }

            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

            workbook.write(response.getOutputStream());
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
