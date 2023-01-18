package gutsandgun.kite_requestmsg.service;

import gutsandgun.kite_requestmsg.dto.SendMsgRequestDTO;
import gutsandgun.kite_requestmsg.dto.SendReplaceDTO;
import gutsandgun.kite_requestmsg.dto.SendingMsgDTO;
import gutsandgun.kite_requestmsg.entity.write.SendReplace;
import gutsandgun.kite_requestmsg.entity.write.SendingMsg;
import gutsandgun.kite_requestmsg.openfeign.SendingManagerServiceClient;
import gutsandgun.kite_requestmsg.repository.write.WriteSendReplaceRepository;
import gutsandgun.kite_requestmsg.repository.write.WriteSendingMsgRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MsgServiceImpl implements MsgService {

    @Autowired
    private final WriteSendingMsgRepository writeSendingMsgRepository;

    @Autowired
    private final WriteSendReplaceRepository writeSendReplaceRepository;

    @Autowired
    private final SendingManagerServiceClient sendingManagerServiceClient;

    @Autowired
    private final ModelMapper mapper;


    @Override
    public void insertSendingMsg(String userId, SendMsgRequestDTO sendMsgRequestDTO){

        // send manager insert sending
        ResponseEntity<Long> response = sendingManagerServiceClient.insertSending(userId, sendMsgRequestDTO);
        int statusCode = response.getStatusCode().value();
        response.getStatusCode().value();

        Long sendingId = response.getBody();

        // TX 입력
        sendMsgRequestDTO.getReceiverList().forEach(receiver -> {
            SendingMsgDTO sendingMsgDTO = new SendingMsgDTO();
            sendingMsgDTO.setSendingId(sendingId);
            sendingMsgDTO.setSender(sendMsgRequestDTO.getSender());
            sendingMsgDTO.setReceiver(receiver.get("receiver"));
            sendingMsgDTO.setName(receiver.get("name"));
            sendingMsgDTO.setRegId(userId);

            SendingMsg sendingMsg = writeSendingMsgRepository.save(mapper.map(sendingMsgDTO, SendingMsg.class));

            // 대체발송
            if(sendMsgRequestDTO.getSendingDTO().getReplaceYn().equals("Y")){
                receiver.put("replace_sender", sendMsgRequestDTO.getReplaceSender());
                insertSendingReplace(userId, sendingMsg.getId(), receiver);
            }
        });

        // TX 입력 완료 시 send manager start sending
        sendingManagerServiceClient.startSending(sendingId);

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
