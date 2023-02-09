package gutsandgun.kite_requestmsg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendingMsgDTO {
    private Long id;
    private Long sendingId;
    private String sender;
    private String receiver;
    private String name;
    private String regId;
    private String modId;
    private String var1;
    private String var2;
    private String var3;
}
