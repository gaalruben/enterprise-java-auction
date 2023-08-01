package org.learning.auction.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class StatisticsDTO {
    int numberOfAllAuctionItems;
    int finished;
    int inProgress;
    int notStarted;
    int numberOfUsers;
}
