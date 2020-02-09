package lv.igors.lottery.lottery.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NewLotteryDTO {
    @NotEmpty(message = "Title may not be null or empty")
    String title;
    @NotNull(message = "Limit may not be null")
    int limit;
}
