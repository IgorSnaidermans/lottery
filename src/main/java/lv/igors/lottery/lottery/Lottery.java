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
    @Column(name = "active")
    private boolean active;
    @Column(name = "participantslimit")
    private int participantsLimit;
    @Column(name = "participants")
    private int participants;
    @Column(name = "winnercode")
    private String winnerCode;
    @Column(name = "starttimestamp")
    private LocalDateTime startTimestamp;
    @Column(name = "endtimestamp")
    private LocalDateTime endTimestamp;
    @Column(name = "title")
    private String title;
}
