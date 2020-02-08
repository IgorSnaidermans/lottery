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
    @NotEmpty
    private String email;
    @NotNull
    @Min(21)
    private Byte age;
    @NotBlank
    private String code;
}
