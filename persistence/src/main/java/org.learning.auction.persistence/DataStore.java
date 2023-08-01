package org.learning.auction.persistence;

import org.learning.auction.domain.AuctionItem;
import org.learning.auction.domain.Property;
import org.learning.auction.domain.User;

import java.util.List;

public interface DataStore {
    void loadData();
    List<Property> getProperties();
    List<User> getUsers();
    List<AuctionItem> getAuctionItems();
    void addProperty(Property property);
    void addAuctionItem(AuctionItem auctionItem);
    void saveProperties();
    void saveAuctionItems();
}
