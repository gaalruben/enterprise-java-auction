package org.learning.auction.persistence;

import org.learning.auction.domain.User;
import org.springframework.data.repository.CrudRepository;


public interface UserRepository extends CrudRepository<User, Long> {
}
