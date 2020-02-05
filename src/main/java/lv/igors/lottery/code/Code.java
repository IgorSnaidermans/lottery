package lv.igors.lottery.code;

import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Code {
    private Long lotteryId;
    @Min(16)
    @Max(16)
    private String participatingCode;
    @Max(99)
    private String ownerEmail;
}
