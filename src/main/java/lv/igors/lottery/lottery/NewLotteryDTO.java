package lv.igors.lottery.lottery;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class NewLotteryDTO {
    @NotBlank
    String title;
    @NotBlank
    int limit;
}
