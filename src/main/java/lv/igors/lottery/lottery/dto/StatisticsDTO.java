package lv.igors.lottery.lottery.dto;

import lombok.*;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
public class StatisticsDTO {
    Long id;
    String title;
    String startTimestamp;
    String endTimestamp;
    int participants;
}
