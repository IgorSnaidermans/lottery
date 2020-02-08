package lv.igors.lottery.lottery.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class NewLotteryDTO {
    @NotBlank
    String title;
    @NotNull
    int limit;
}
