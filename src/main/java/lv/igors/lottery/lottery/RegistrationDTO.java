package lv.igors.lottery.lottery;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;


@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class RegistrationDTO {
    private Long lotteryId;
    @Email
    private String email;
    @Min(21)
    private Byte age;
    @Min(16)
    @Max(16)
    private String code;
}
