package gutsandgun.kite_requestmsg.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    BAD_REQUEST(400, "S000", "잘못된 요청입니다."),
    INVALID_PHONE(400, "ERR404", "잘못된 전화번호입니다.");

    private int status;
    private String code;
    private String message;

}
