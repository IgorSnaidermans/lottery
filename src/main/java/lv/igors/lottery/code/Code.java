package lv.igors.lottery.code;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

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


    public boolean equalsWithoutDatabaseId(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Code code = (Code) o;
        return Objects.equals(lotteryId, code.lotteryId) &&
                Objects.equals(participatingCode, code.participatingCode) &&
                Objects.equals(ownerEmail, code.ownerEmail);
    }
}