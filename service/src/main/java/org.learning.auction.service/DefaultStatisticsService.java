package org.learning.auction.service;

import lombok.Getter;
import lombok.Setter;
import org.learning.auction.domain.AuctionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.stream.StreamSupport;

@Getter
@Setter
@Service
@Transactional
public class DefaultStatisticsService implements StatisticsService{
    @Autowired
    AuctionItemService auctionItemService;

    @Autowired
    UserService userService;

    @Override
    public Statistics calculateStatistics() {
        Statistics statistics = new Statistics();

        int numberOfUsers = userService.numberOfAllUsers();
        int numberOfAllAuctionItems = auctionItemService.numberOfAllAuctionItems();
        int numberOfFinishedAuctionItems = getAuctionItemsCountByState(AuctionState.FINISHED);
        int numberOfInProgressAuctionItems = getAuctionItemsCountByState(AuctionState.IN_PROGRESS);
        int numberOfNotStartedAuctionItems = getAuctionItemsCountByState(AuctionState.NOT_STARTED);

        statistics.setNumberOfAllAuctionItems(numberOfAllAuctionItems);
        statistics.setNumberOfFinishedAuctionItems(numberOfFinishedAuctionItems);
        statistics.setNumberOfInProgressAuctionItems(numberOfInProgressAuctionItems);
        statistics.setNumberOfNotStartedAuctionItems(numberOfNotStartedAuctionItems);
        statistics.setNumberOfUsers(numberOfUsers);

        return statistics;
    }
    private int getAuctionItemsCountByState(AuctionState state){
        return (int)auctionItemService.getAuctionItems().stream().filter(item -> item.getState().equals(state)).count();
    }
}
