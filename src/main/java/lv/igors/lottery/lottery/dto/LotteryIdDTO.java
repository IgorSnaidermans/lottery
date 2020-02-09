package lv.igors.lottery.lottery.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@Getter
@Setter
public class LotteryIdDTO {
    @NotNull(message = "Lottery id may not be null")
    Long lotteryId;
}
