package lv.igors.lottery.lottery;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class CheckStatusDTO {
    @NotNull
    Long lotteryId;
    @Email
    String email;
    @NotBlank
    String code;
}
