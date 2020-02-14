package lv.igors.lottery.lottery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NewLotteryDTO {
    @NotEmpty(message = "Title may not be null or empty")
    String title;
    @NotNull(message = "Limit may not be null")
    int limit;
}
