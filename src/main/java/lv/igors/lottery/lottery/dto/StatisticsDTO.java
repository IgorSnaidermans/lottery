package lv.igors.lottery.lottery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
