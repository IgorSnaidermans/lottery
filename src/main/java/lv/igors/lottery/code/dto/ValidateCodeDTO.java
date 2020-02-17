package lv.igors.lottery.code.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidateCodeDTO {
    String code;
    Long lotteryId;
    String email;
}
