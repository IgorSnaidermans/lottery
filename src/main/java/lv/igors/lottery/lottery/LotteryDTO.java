package lv.igors.lottery.lottery;

import lombok.*;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class LotteryDTO {
    private Long id;
    private String title;
    private String startTimestamp;
    private String endTimestamp;
}
