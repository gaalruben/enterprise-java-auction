package org.learning.auction.persistence;

import org.learning.auction.domain.BidHistory;
import org.springframework.data.repository.CrudRepository;

public interface BidHistoryRepository extends CrudRepository<BidHistory, Long> {
}
