package org.learning.auction.service;

import org.learning.auction.domain.Credentials;
import org.learning.auction.domain.User;

import java.util.List;

public interface UserService {
    User authenticateUser(Credentials credentials);
    User findUserByUsername(String username);
    User getLoggedinUser();
    int numberOfAllUsers();
    List<User> getUsers();
}
