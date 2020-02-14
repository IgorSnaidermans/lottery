package lv.igors.lottery.lottery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LotteryDTO {
    private Long id;
    private Boolean active;
    private String title;
    private String startTimestamp;
    private String endTimestamp;
}
