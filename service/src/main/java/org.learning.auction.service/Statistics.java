package org.learning.auction.service;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Statistics {
    int numberOfAllAuctionItems;
    int numberOfNotStartedAuctionItems;
    int numberOfInProgressAuctionItems;
    int numberOfFinishedAuctionItems;
    int numberOfUsers;
}
