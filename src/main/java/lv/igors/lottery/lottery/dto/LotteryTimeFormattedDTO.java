package lv.igors.lottery.lottery.dto;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LotteryTimeFormattedDTO {
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
