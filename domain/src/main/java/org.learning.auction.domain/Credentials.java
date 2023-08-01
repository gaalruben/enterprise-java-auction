package org.learning.auction.domain;
import lombok.*;

import javax.persistence.Embeddable;

@ToString
@EqualsAndHashCode
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Credentials {
    private String username;
    private String password;
}
