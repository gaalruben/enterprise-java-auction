package org.learning.auction.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuctionItemDto {

    @JsonProperty("endBidding")
    private LocalDateTime endBidding;
    @JsonProperty("startBidding")
    private LocalDateTime startBidding;
    @JsonProperty("minBidIncrement")
    private double minBidIncrement;
    @JsonProperty("upsetPrice")
    private double upsetPrice;
    @JsonProperty("deposit")
    private double deposit;
    @JsonProperty("state")
    private AuctionState state;
    @JsonProperty("propertyId")
    private long propertyId;
    @JsonProperty("id")
    private long id;
}
