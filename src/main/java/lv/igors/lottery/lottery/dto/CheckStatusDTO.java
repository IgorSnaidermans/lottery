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
public class CheckStatusDTO {
    @NotNull(message = "Lottery id may not be null")
    private Long lotteryId;
    @Email(message = "Email should be correct")
    @NotEmpty(message = "Email may no be empty")
    private String email;
    @NotEmpty(message = "Code may not be empty")
    private String code;
}
