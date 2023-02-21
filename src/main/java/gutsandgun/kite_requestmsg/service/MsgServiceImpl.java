package gutsandgun.kite_requestmsg.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import gutsandgun.kite_requestmsg.dto.SendMsgRequestDTO;
import gutsandgun.kite_requestmsg.dto.SendReplaceDTO;
import gutsandgun.kite_requestmsg.dto.SendingDTO;
import gutsandgun.kite_requestmsg.dto.SendingMsgDTO;
import gutsandgun.kite_requestmsg.entity.write.SendReplace;
import gutsandgun.kite_requestmsg.entity.write.SendingEmail;
import gutsandgun.kite_requestmsg.entity.write.SendingMsg;
import gutsandgun.kite_requestmsg.openfeign.SendingManagerServiceClient;
import gutsandgun.kite_requestmsg.publisher.RabbitMQProducer;
import gutsandgun.kite_requestmsg.repository.read.ReadSendingMsgRepository;
import gutsandgun.kite_requestmsg.repository.write.WriteSendReplaceRepository;
import gutsandgun.kite_requestmsg.repository.write.WriteSendingEmailRepository;
import gutsandgun.kite_requestmsg.repository.write.WriteSendingMsgRepository;
import gutsandgun.kite_requestmsg.type.SendingType;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private final RabbitMQProducer rabbitMQProducer;


    @Override
    @Transactional
    public void insertSendingMsg(String userId, SendMsgRequestDTO sendMsgRequestDTO) throws JsonProcessingException {

        SendingDTO sendingDTO = sendMsgRequestDTO.getSendingDTO();
        sendingDTO.setInputTime(new Date().getTime());
        sendMsgRequestDTO.setSendingDTO(sendingDTO);

        // send manager insert sending
        ResponseEntity<Long> response = sendingManagerServiceClient.insertSending(userId, sendMsgRequestDTO);
        int statusCode = response.getStatusCode().value();

        Long sendingId = response.getBody();
        SendingType sendingType = sendingDTO.getSendingType();

        rabbitMQProducer.logSendQueue("Service: request, type: genSendingId, sendingId: " + sendingId +
                ", sendingType: " + sendingDTO.getSendingType() + ", ruleType: " + sendingDTO.getSendingRuleType() +
                ", total: " + sendingDTO.getTotalMessage() + ", replace: " + (sendingDTO.getReplaceYn()=="Y"? true : false) +
                ", title: " + sendingDTO.getTitle() + ", content: " + sendingDTO.getContent() + ", mediaLink: " + sendingDTO.getMediaLink() +
                ", sender: " + sendMsgRequestDTO.getSender() + ", userId: " + userId +
                ", inputTime: "+sendingDTO.getInputTime() + ", scheduleTime: " + sendingDTO.getScheduleTime()+"@"
        );

        List<Map<String, String>> receiverList =  sendMsgRequestDTO.getReceiverList();
        List<SendingMsgDTO> sendingMsgDTOList = new ArrayList<>();

        // TX 입력

        if(sendingType.equals(SendingType.SMS) || sendingType.equals(SendingType.MMS)) {

            List<SendingMsg> sendingMsgList = new ArrayList<>();
            receiverList.forEach(receiver -> {
                SendingMsg sendingMsg = new SendingMsg();
                sendingMsg.setSendingId(sendingId);
                sendingMsg.setSender(sendMsgRequestDTO.getSender());
                sendingMsg.setReceiver(receiver.get("receiver"));
                sendingMsg.setName(receiver.get("name"));
                sendingMsg.setRegId(userId);
                sendingMsg.setVar1(null);
                sendingMsg.setVar2(null);
                sendingMsg.setVar3(null);
                sendingMsgList.add(sendingMsg);
            });
            // tx 저장
            List<SendingMsg> savedSendingMsgList = writeSendingMsgRepository.saveAll(sendingMsgList);
            savedSendingMsgList.forEach(sendingMsg -> {
                sendingMsgDTOList.add(mapper.map(sendingMsg, SendingMsgDTO.class));
            });

        }else if(sendingType.equals(SendingType.EMAIL)){
            // TX 입력
            List<SendingEmail> sendingEmailList = new ArrayList<>();
            receiverList.forEach(receiver -> {
                SendingEmail sendingEmail = new SendingEmail();
                sendingEmail.setSendingId(sendingId);
                sendingEmail.setSender(sendMsgRequestDTO.getSender());
                sendingEmail.setReceiver(receiver.get("receiver"));
                sendingEmail.setRegId(userId);
                sendingEmail.setVar1(null);
                sendingEmail.setVar2(null);
                sendingEmail.setVar3(null);
                sendingEmailList.add(sendingEmail);
            });

            // tx 저장
            List<SendingEmail> savedSendingMsgList = writeSendingEmailRepository.saveAll(sendingEmailList);
            savedSendingMsgList.forEach(sendingEmail -> {
                sendingMsgDTOList.add(mapper.map(sendingEmail, SendingMsgDTO.class));
            });
        }


        // tx redis 저장
        Thread thread1 = new Thread(() -> {
            try {
                sendingCache.insertSendingMsgList(sendingId, sendingMsgDTOList);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        // 로그
        Thread thread2 = new Thread(() -> {
            sendingMsgDTOList.forEach(sendingMsgDTO -> {
                if(sendingType.equals(SendingType.SMS) || sendingType.equals(SendingType.MMS)){
                    rabbitMQProducer.logSendQueue("Service: request, type: input, sendingId: " + sendingId + ", TXId: "+ sendingMsgDTO.getId() + ", sender: " + sendingMsgDTO.getSender() + ", receiver: " + sendingMsgDTO.getReceiver()+"@");
                }else if(sendingType.equals(SendingType.EMAIL)){
                    rabbitMQProducer.logSendQueue("Service: request, type: input, sendingId: " + sendingId +", TXId: "+ sendingMsgDTO.getId() + ", sender: " + sendingMsgDTO.getSender() + ", receiver: " + sendingMsgDTO.getReceiver()+"@");
                }
            });
        });

        // 대체발송 정보 저장
        Thread thread3 = new Thread(() -> {
            if(sendingDTO.getReplaceYn().equals("Y")){
                sendingMsgDTOList.forEach(sendingMsgDTO -> {
                    List<Map<String,String>> filter = receiverList.stream().filter(receiver -> (receiver.get("receiver") == sendingMsgDTO.getReceiver())).collect(Collectors.toList());
                    insertSendingReplace(userId, sendingMsgDTO.getId(), sendingMsgDTO.getSender(),  filter.get(0).get("replace_receiver"));
                });
            }
        });

        thread1.start();
        thread2.start();
        thread3.start();


        if(sendMsgRequestDTO.getReservationYn().equals("N")) {
            // TX 입력 완료 시 send manager start sending
            Map<String, Long> map = new HashMap<>();
            map.put("sendingId", sendingId);
            sendingManagerServiceClient.startSending(map);
        }
    }

    public Long insertSendingReplace(String userId, Long txId, String replaceSender, String replaceReceiver){

        SendReplaceDTO sendReplaceDTO = new SendReplaceDTO();
        sendReplaceDTO.setId(txId);
        sendReplaceDTO.setRegId(userId);
        sendReplaceDTO.setReceiver(userId);
        sendReplaceDTO.setReceiver(replaceReceiver);
        sendReplaceDTO.setSender(replaceSender);

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
