package org.learning.auction.service;

import org.learning.auction.domain.PersonalProperty;
import org.learning.auction.domain.Property;
import org.learning.auction.domain.RealProperty;

import java.util.List;

public interface PropertyService {
    void createProperty(Property property);
    List<Property> findPropertyWithoutRelatedAuctionItem();

    List<Property> getPersonalProperties();
    List<Property> getRealProperties();
    Property findPropertyByName(String name);
}
