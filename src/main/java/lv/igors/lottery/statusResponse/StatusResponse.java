package lv.igors.lottery.statusResponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusResponse {
    private String status;
    private String reason;
    private Long id;
    private String winnerCode;
}
