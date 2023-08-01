package org.learning.auction.service;

import org.learning.auction.domain.AuctionItem;
import org.learning.auction.domain.AuctionState;

import java.time.LocalDateTime;
import java.util.List;

public interface AuctionItemService {
    void setAuctionItemStateById(Long id, AuctionState state);
    void createAuctionItem(AuctionItem auctionItem);
    List<AuctionItem> getAuctionItems();
    AuctionItem getAuctionItemById(Long id);
    LocalDateTime getLastBiddingDate(AuctionItem auctionItem);
    List<AuctionItem> findAuctionItemsWhereUserIsNotInParticipants();
    void joinAuction(AuctionItem auctionItem);
    int numberOfAllAuctionItems();
    void bid(int price);
    boolean validateBid(int price, int minBid, int ogPrice);
}
