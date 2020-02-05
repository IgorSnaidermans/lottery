package lv.igors.lottery.lottery;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class CheckStatusDTO {
    @NotBlank
    Long lotteryId;
    @Email
    String email;
    @NotBlank
    String code;
}
