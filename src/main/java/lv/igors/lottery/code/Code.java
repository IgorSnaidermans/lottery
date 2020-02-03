package lv.igors.lottery.code;

import lombok.*;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Code {
    private Long lotteryId;
    private String participatingCode;
    private String ownerEmail;
}
