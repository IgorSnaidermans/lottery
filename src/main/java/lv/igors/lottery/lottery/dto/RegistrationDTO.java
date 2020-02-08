package lv.igors.lottery.lottery.dto;

import lombok.*;

import javax.validation.constraints.*;


@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class RegistrationDTO {
    @NotNull
    private Long lotteryId;
    @Email
    @NotBlank
    private String email;
    @Min(21)
    @NotNull
    private Byte age;
    @Min(16)
    @Max(16)
    @NotBlank
    private String code;
}
