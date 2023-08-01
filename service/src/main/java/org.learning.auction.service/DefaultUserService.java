package org.learning.auction.service;

import lombok.Getter;
import lombok.Setter;
import org.learning.auction.domain.Credentials;
import org.learning.auction.domain.User;
import org.learning.auction.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.StreamSupport;

@Getter
@Setter
@Component
@Transactional
public class DefaultUserService implements UserService{

    User loggedInUser;
    @Autowired
    UserRepository userRepository;

    @Override
    public User authenticateUser(Credentials credentials) {
        List<User> allUsers = StreamSupport
                .stream(userRepository.findAll().spliterator(), false)
                .toList();
        User authenticatedUser = allUsers.stream()
                .filter(user -> user.getCredentials().equals(credentials))
                .findFirst().orElseThrow(() -> new AuthenticationException("Username or password is wrong."));
        loggedInUser = authenticatedUser;
        return authenticatedUser;
    }

    @Override
    public User findUserByUsername(String username) {
        List<User> allUsers = StreamSupport
                .stream(userRepository.findAll().spliterator(), false)
                .toList();
        User result = allUsers.stream()
                .filter(user -> user.getCredentials().getUsername().equals(username))
                .findFirst().orElseThrow(() -> new AuthenticationException("This username does not exist."));
        return result;
    }

    @Override
    public User getLoggedinUser() {
        List<User> allUsers = StreamSupport
                .stream(userRepository.findAll().spliterator(), false)
                .toList();
        loggedInUser = allUsers.stream()
                .filter(user -> user.getCredentials().getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName()))
                .findFirst().orElseThrow(() -> new AuthenticationException("This username does not exist."));

        return loggedInUser;
    }

    @Override
    public int numberOfAllUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false).toList().size();
    }

    @Override
    public List<User> getUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false).toList();
    }
}
