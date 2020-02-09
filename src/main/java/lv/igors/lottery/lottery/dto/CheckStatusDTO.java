package lv.igors.lottery.lottery.dto;

import lombok.*;

import javax.validation.constraints.*;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class CheckStatusDTO {
    @NotNull(message = "Lottery id may not be null")
    Long lotteryId;
    @NotEmpty(message = "Email may not be empty")
    @Email(message = "Email is incorrect")
    String email;
    @NotBlank(message = "Code may not be empty")
    String code;
    @NotNull(message = "Age may not be null")
    @Min(value = 21, message = "To participate, you should be not younger than 21 years old")
    private Byte age;
}
