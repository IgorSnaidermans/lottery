package lv.igors.lottery.code;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Entity;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="codes")
public class Code {

    @Id
    private Long id;
    @Column
    private Long lotteryId;
    @Column
    private String participatingCode;
    @Column
    private String ownerEmail;
}