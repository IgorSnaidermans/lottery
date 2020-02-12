package lv.igors.lottery.code;

import lombok.*;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "codes")
public class Code {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "lotteryid")
    private Long lotteryId;
    @Column(name = "participatingcode")
    private String participatingCode;
    @Column(name = "owneremail")
    private String ownerEmail;
}