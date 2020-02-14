package lv.igors.lottery.lottery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LotteryAdminDTO {
    private String startTimestampFormatted;
    private String endTimestampFormatted;
    private Long id;
    private boolean active;
    private int participantsLimit;
    private int participants;
    private String winnerCode;
    private String title;
    private String winnerEmail;
}
