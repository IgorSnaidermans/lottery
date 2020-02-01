package lv.igors.lottery.code;

import lombok.*;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Code {
    Long lotteryId;
    String participatingCode;
    String ownerEmail;
}
