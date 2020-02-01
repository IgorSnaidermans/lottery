package lv.igors.lottery.code;

import lombok.*;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Code {
    String code;
    String ownerEmail;
    String lotteryId;
}
