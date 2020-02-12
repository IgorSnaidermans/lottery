package lv.igors.lottery.lottery.dto;

import lombok.*;

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
