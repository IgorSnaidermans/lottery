package lv.igors.lottery.lottery;

import lombok.*;

import java.time.LocalDateTime;


@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Lottery {
    private Long id;
    private boolean active;
    private int limit;
    private int participants;
    private String winnerCodeId;
    private LocalDateTime startTimeStamp;
    private LocalDateTime endTimeStamp;
    private String title;
}
