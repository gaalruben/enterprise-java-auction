package org.learning.auction.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class BiddingDTO {
    Long id;
    String propertyName;
    String propertyDescription;
    int upsetPrice;
    int price;
    int deposit;
    int minBidIncrement;
    String startBidding;
    String endBidding;
    String status;
    String lastBidding;
}
