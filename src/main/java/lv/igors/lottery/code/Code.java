package lv.igors.lottery.code;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lv.igors.lottery.lottery.Lottery;

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
    @Column(name = "participatingcode")
    private String participatingCode;
    @Column(name = "owneremail")
    private String ownerEmail;
    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE,
            CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name="lotteryid")
    private Lottery lottery;

    @Override
    public String toString() {
        return "Code{" +
                "id=" + id +
                ", participatingCode='" + participatingCode + '\'' +
                ", ownerEmail='" + ownerEmail + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Code code = (Code) o;
        return Objects.equals(participatingCode, code.participatingCode) &&
                Objects.equals(ownerEmail, code.ownerEmail) &&
                Objects.equals(lottery, code.lottery);
    }

    @Override
    public int hashCode() {
        return Objects.hash(participatingCode, ownerEmail, lottery);
    }
}