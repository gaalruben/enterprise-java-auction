package org.learning.auction.persistence;

import org.learning.auction.domain.AuctionItem;
import org.springframework.data.repository.CrudRepository;

public interface AuctionItemRepository extends CrudRepository<AuctionItem, Long> {
}
