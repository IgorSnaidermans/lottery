package lv.igors.lottery.user;

import lombok.*;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class User {
    //todo email validation
    String email;
    //todo from 21 age
    int age;
}
