package gutsandgun.kite_requestmsg.service;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

public interface SmsService {

    void downloadSampleFile (HttpServletResponse response,  List<String> headerList);

}
