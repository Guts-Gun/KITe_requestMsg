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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    private final RedisTemplate<String, SendingMsgDTO> redisTemplate;

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
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

        SetOperations<String, SendingMsgDTO> setOperations = redisTemplate.opsForSet();

        // TX ??????
        sendMsgRequestDTO.getReceiverList().forEach(receiver -> executorService.submit(() ->{
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
                setOperations.add("sendingMsgDTO".concat(String.valueOf(sendingId)),mapper.map(sendingMsg, SendingMsgDTO.class));
//                sendingCache.insertSendingMsg(id, sendingMsg);
                rabbitMQProducer.logSendQueue("Service: request, type: input, sendingId: " + sendingId +
                        ", TXId: "+ id + ", sender: " + sendingMsg.getSender() + ", receiver: " + sendingMsg.getReceiver()+"@");
            }else if(sendingDTO.getSendingType().equals(SendingType.EMAIL)){
                SendingEmail sendingEmail = writeSendingEmailRepository.save(mapper.map(sendingMsgDTO, SendingEmail.class));
                id = sendingEmail.getId();
                setOperations.add("sendingMsgDTO".concat(String.valueOf(sendingId)),mapper.map(sendingEmail, SendingMsgDTO.class));
//                sendingCache.insertSendingEmail(id, sendingEmail);
                rabbitMQProducer.logSendQueue("Service: request, type: input, sendingId: " + sendingId +
                        ", TXId: "+ id + ", sender: " + sendingEmail.getSender() + ", receiver: " + sendingEmail.getReceiver()+"@");
            }

            // ????????????
            if(sendMsgRequestDTO.getSendingDTO().getReplaceYn().equals("Y")){
                receiver.put("replace_sender", sendMsgRequestDTO.getReplaceSender());
                insertSendingReplace(userId, id, receiver);
            }
        }));

        log.info("Waiting threads... sendingId: "+sendingId+", total: "+sendMsgRequestDTO.getSendingDTO().getTotalMessage());
        while(!Objects.equals(setOperations.size("sendingMsgDTO".concat(String.valueOf(sendingId))), sendMsgRequestDTO.getSendingDTO().getTotalMessage())){}
        log.info("Threads are done. sendingId: "+sendingId+", total: "+setOperations.size("sendingMsgDTO".concat(String.valueOf(sendingId))));
        Set<SendingMsgDTO> SendingMsgSet=setOperations.members("sendingMsgDTO".concat(String.valueOf(sendingId)));
        List<SendingMsgDTO> sendingMsgDTOList = new ArrayList<>(SendingMsgSet);

        sendingCache.insertSendingMsgList(sendingId, sendingMsgDTOList);

        if(sendMsgRequestDTO.getReservationYn().equals("N")) {
            // TX ?????? ?????? ??? send manager start sending
            Map<String, Long> map = new HashMap<>();
            map.put("sendingId", sendingId);
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

        sendingCache.insertSendReplaceInfo(txId,sendReplaceDTO);

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

            // ?????????
            final String fileName = "sample_file";

            // ??????
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
