package lv.igors.lottery.lottery;

import lombok.*;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class StatusResponse {
    private String status;
    private Long id;
    private String reason;
    private String winnerCode;
}
