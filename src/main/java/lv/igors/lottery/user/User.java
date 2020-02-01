package lv.igors.lottery.user;

import lombok.*;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class User {
    String email;
    //todo from 21 age
    int age;
}
