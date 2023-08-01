package org.learning.auction.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
@Getter @Setter
@Entity
@Builder
@AllArgsConstructor
@Table(name = "auction_item")
public class AuctionItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double deposit;
    private double upsetPrice;
    private double minBidIncrement;
    private double price;
    private LocalDateTime startBidding;
    private LocalDateTime endBidding;

    @OneToOne(mappedBy = "auction_item")
    private User winner;

    @OneToMany(mappedBy = "auction_item")
    private List<User> participants;
    @Enumerated(EnumType.STRING)
    private AuctionState state;

    @OneToOne
    @JoinColumn(name = "property_id", referencedColumnName = "id")
    private Property property;

    @OneToMany(mappedBy = "auction_item")
    private List<BidHistory> history;

    public AuctionItem(){
        participants = new ArrayList<>();
        history = new ArrayList<>();
    }

    @Override
    public String toString() {
        return property.name;
    }
}
