package org.learning.auction.domain;

import lombok.*;

import javax.persistence.Embeddable;

@ToString
@EqualsAndHashCode
@Getter @Setter @NoArgsConstructor
@Embeddable
public class Account {
    private double balance;
}
