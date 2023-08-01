package org.learning.auction.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.expression.ParseException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class AuctionItemRowDTO {
    private static final SimpleDateFormat dateFormat
            = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    Long id;
    String propertyName;
    int upsetPrice;
    int deposit;
    int minBidIncrement;
    String startBidding;
    String endBidding;
    String status;
    boolean userIsInParticipants;

    public Date getStartBiddingDateConverted(String timezone) throws java.text.ParseException {
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        return dateFormat.parse(this.startBidding);
    }

    public void setStartBiddingDate(Date date, String timezone) {
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        this.startBidding = dateFormat.format(date);
    }

    public Date getEndBiddingDateConverted(String timezone) throws java.text.ParseException {
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        return dateFormat.parse(this.endBidding);
    }

    public void setEndBiddingDate(Date date, String timezone) {
        dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
        this.endBidding = dateFormat.format(date);
    }
}
