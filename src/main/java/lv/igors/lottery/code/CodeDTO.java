package lv.igors.lottery.code;


import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class CodeDTO {
    private Long lotteryId;
    private LocalDateTime lotteryStartTimestamp;
    @Email
    private String email;
    @Min(16)
    @Max(16)
    private String code;
}
