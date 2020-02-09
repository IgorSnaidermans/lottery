package lv.igors.lottery.lottery.dto;

import lombok.*;

import javax.validation.constraints.*;

import lv.igors.lottery.statusResponse.Responses;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class RegistrationDTO {
    @NotNull(message = "Lottery id may not be null")
    private Long lotteryId;
    @Email(message = "Email should be correct")
    @NotEmpty(message = "Email may no be empty")
    private String email;
    @NotNull(message = "Age may not be null")
    @Min(value = 21, message = "To participate, you should be not younger than 21 years old")
    private Byte age;
    @NotEmpty(message = "Code may not be empty")
    private String code;
}
