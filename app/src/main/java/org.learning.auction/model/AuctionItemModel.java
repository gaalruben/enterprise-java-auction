package org.learning.auction.model;

import lombok.*;
import org.learning.auction.domain.Property;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class AuctionItemModel {
    @NotNull
    @NotEmpty
    String propertyName;

    @NotNull
    @Min(1)
    int upsetPrice;

    @NotNull
    @Min(1)
    int deposit;

    @NotNull
    @Min(1)
    int minBidIncrement;

    @NotNull
    @NotEmpty
    String startBidding;

    @NotNull
    @NotEmpty
    String endBidding;

    @AssertTrue(message = "Start date must be before end date.")
    public boolean isStartDateBeforeEndDate(){
        if(startBidding.isEmpty() || endBidding.isEmpty())
            return false;
        return LocalDateTime.parse(startBidding).isBefore(LocalDateTime.parse(endBidding));
    }
}
