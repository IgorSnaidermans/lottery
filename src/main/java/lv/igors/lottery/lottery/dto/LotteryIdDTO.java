package lv.igors.lottery.lottery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LotteryIdDTO {
    @NotNull(message = "Lottery id may not be null")
    Long lotteryId;
}
