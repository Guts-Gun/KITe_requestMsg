package gutsandgun.kite_requestmsg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendingRuleDTO {

    private Long userId;
    private Long sendingId;
    private Long brokerId;
    private Long weight;

}