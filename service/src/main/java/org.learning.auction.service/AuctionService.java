package org.learning.auction.service;

import org.learning.auction.domain.AuctionItem;
import org.learning.auction.domain.Credentials;
import org.learning.auction.domain.Property;
import org.learning.auction.domain.User;

import java.util.List;

public interface AuctionService {
    User authenticateUser(Credentials credentials);
    Statistics calculateStatistics();
    void createProperty(Property property);
    void createAuctionItem(AuctionItem auctionItem);
    List<AuctionItem> getAuctionItems();
    List<AuctionItem> findAuctionItemsWhereUserIsNotInParticipants();
    void joinAuction(AuctionItem auctionItem);
    List<Property> findPropertyWithoutRelatedAuctionItem();
}
