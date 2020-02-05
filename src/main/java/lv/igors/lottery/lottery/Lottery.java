package lv.igors.lottery.lottery;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="lotteries")
public class Lottery {

    @Id
    private Long id;
    @Column
    private boolean active;
    @Column
    private int participantsLimit;
    @Column
    private int participants;
    @Column
    private String winnerCode;
    @Column
    private LocalDateTime startTimestamp;
    @Column
    private LocalDateTime endTimestamp;
    @Column
    private String title;
}
