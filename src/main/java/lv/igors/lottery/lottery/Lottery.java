package lv.igors.lottery.lottery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lv.igors.lottery.code.Code;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @OneToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "winnerCodeId")
    private Code winnerCode;
    @Column(name = "starttimestamp")
    private LocalDateTime startTimestamp;
    @Column(name = "endtimestamp")
    private LocalDateTime endTimestamp;
    @Column(name = "title")
    private String title;
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH}, mappedBy = "lottery")
    private List<Code> registeredCodes;

    @Override
    public String toString() {
        return "Lottery{" +
                "id=" + id +
                ", active=" + active +
                ", participantsLimit=" + participantsLimit +
                ", participants=" + participants +
                ", winnerCode=" + winnerCode +
                ", startTimestamp=" + startTimestamp +
                ", endTimestamp=" + endTimestamp +
                ", title='" + title + '\'' +
                '}';
    }

    public void addCode(Code code) {
        if (registeredCodes == null) {
            registeredCodes = new ArrayList<>();
        }
        registeredCodes.add(code);

        code.setLottery(this);
    }
}

