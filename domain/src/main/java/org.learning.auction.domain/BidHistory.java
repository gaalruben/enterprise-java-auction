package org.learning.auction.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@ToString
@EqualsAndHashCode
@Getter @Setter @NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
public class BidHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double price;
    private LocalDateTime time;
    private String userName;

    @ManyToOne
    @JoinColumn(name = "auction_item_id")
    private AuctionItem auction_item;
}
