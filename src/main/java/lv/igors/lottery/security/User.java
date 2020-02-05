package lv.igors.lottery.security;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="users")
public class User {

    @Id
    private Long id;
    @Column
    private String password;
    @Column
    private String name;

}
