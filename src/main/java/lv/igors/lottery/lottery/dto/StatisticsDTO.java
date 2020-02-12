package lv.igors.lottery.lottery.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class StatisticsDTO {
    Long id;
    String title;
    String startTimestamp;
    String endTimestamp;
    int participants;
}
