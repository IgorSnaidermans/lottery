package lv.igors.lottery.lottery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegistrationDTO {
    @NotNull(message = "Lottery id may not be null")
    private Long lotteryId;
    @Email(message = "Email should be correct")
    @NotEmpty(message = "Email may no be empty")
    private String email;
    @NotNull(message = "Age may not be null")
    @Min(value = 21, message = "To participate, you should be not younger than 21 years old")
    @Max(value = 127, message = "Age cannot be more than 127")
    private Byte age;
    @NotEmpty(message = "Code may not be empty")
    private String code;
}
