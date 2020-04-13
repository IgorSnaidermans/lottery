package lv.igors.lottery.lottery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lv.igors.lottery.code.Code;

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
    private Code winnerCode;
    private String title;
}
