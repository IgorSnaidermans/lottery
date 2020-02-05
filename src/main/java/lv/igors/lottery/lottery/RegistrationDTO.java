package lv.igors.lottery.lottery;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;


@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class RegistrationDTO {
    @NotBlank
    private Long lotteryId;
    @Email
    @NotBlank
    private String email;
    @Min(21)
    @NotBlank
    private Byte age;
    @Min(16)
    @Max(16)
    @NotBlank
    private String code;
}
