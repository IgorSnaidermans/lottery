package lv.igors.lottery.lottery;

import lombok.*;
import lv.igors.lottery.code.Code;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public void addCode(Code code){
        codes.add(code);
    }

}
