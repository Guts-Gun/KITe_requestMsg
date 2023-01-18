package gutsandgun.kite_requestmsg.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendReplaceDTO {

    private Long id;
    private String sender;
    private String receiver;
    private String regId;
    private String modId;
}
