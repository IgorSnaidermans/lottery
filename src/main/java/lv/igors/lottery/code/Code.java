package lv.igors.lottery.code;

import lombok.*;

import javax.validation.constraints.Max;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Code {
    private Long lotteryId;
    private String participatingCode;
    @Max(99)
    private String ownerEmail;
}
