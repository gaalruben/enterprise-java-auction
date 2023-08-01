package org.learning.auction.domain;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
@Builder
@AllArgsConstructor
@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = "_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Embedded
    private Credentials credentials;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Embedded
    private Account account;

    @ManyToOne
    @JoinColumn(name = "auction_item_id")
    private AuctionItem auction_item;

    @Transient
    private List<AuctionItem> auctionItems = new ArrayList<>();

    @Override
    public String toString() {
        return credentials.getUsername();
    }
}
