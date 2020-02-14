package lv.igors.lottery.lottery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "lotteries")
public class Lottery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

