package lv.igors.lottery.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class User {
    @Email
    @NotBlank
    String email;

    @Min(21)
    @NotBlank
    int age;
}
