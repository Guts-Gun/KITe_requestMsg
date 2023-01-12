package gutsandgun.kite_requestmsg.controller;

import gutsandgun.kite_requestmsg.service.SmsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value="v1/sms")
@Log4j2
public class SmsController {

    @Autowired
    SmsService smsService;


    //엑셀 다운로드
    @GetMapping(value = "/download/excel", produces = "application/vnd.ms-excel")
    public void excelDownload(HttpServletResponse res) throws UnsupportedEncodingException, ParseException {

        List<String> headerList = new ArrayList<>();
        headerList.add("NAME");
        headerList.add("PHONE");
        headerList.add("EMAIL");

        smsService.downloadSampleFile(res, headerList);
    }



}
