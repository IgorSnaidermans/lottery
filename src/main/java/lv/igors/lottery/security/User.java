package lv.igors.lottery.security;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity(name = "users")
public class User {

    @Id
    private Long id;
    @Column
    private String password;
    @Column
    private String name;

}
