package org.learning.auction.service;

import lombok.Getter;
import lombok.Setter;
import org.learning.auction.domain.AuctionItem;
import org.learning.auction.domain.AuctionState;
import org.learning.auction.domain.Credentials;
import org.learning.auction.domain.PersonalProperty;
import org.learning.auction.domain.Property;
import org.learning.auction.domain.Role;
import org.learning.auction.domain.User;
import org.learning.auction.persistence.AuctionItemRepository;
import org.learning.auction.persistence.DataStore;
import org.learning.auction.persistence.PropertyRepository;
import org.learning.auction.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.PropertyPermission;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Getter @Setter
@Service
@Transactional
public class DefaultAuctionService implements AuctionService {

    User loggedInUser;

    @Autowired
    UserRepository userRepository;
    @Autowired
    PropertyRepository propertyRepository;
    @Autowired
    AuctionItemRepository auctionItemRepository;

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
    public Statistics calculateStatistics() {
        Statistics statistics = new Statistics();

        int numberOfUsers = StreamSupport.stream(userRepository.findAll().spliterator(), false).toList().size();
        int numberOfAllAuctionItems = StreamSupport.stream(auctionItemRepository.findAll().spliterator(), false).toList().size();
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

    @Override
    public void createProperty(Property property) {
        if (authorizeLoggedInUser() && validateProperty(property)){
            if(PersonalProperty.class.isAssignableFrom(property.getClass())){
                validatePersonalProperty((PersonalProperty) property);
            }
            propertyRepository.save(property);
        }
    }

    @Override
    public void createAuctionItem(AuctionItem auctionItem) {
        if(authorizeLoggedInUser() && validateAuctionItem(auctionItem)){
            auctionItemRepository.save(auctionItem);
        }
    }

    @Override
    public List<AuctionItem> getAuctionItems() {
        return StreamSupport.stream(auctionItemRepository.findAll().spliterator(), false).toList();
    }

    @Override
    public List<AuctionItem> findAuctionItemsWhereUserIsNotInParticipants() {
        List<AuctionItem> auctionItemsWhereUserIsNotInParticipants = StreamSupport.stream(auctionItemRepository.findAll().spliterator(), false)
                                                                                    .filter(auctionItem -> !auctionItem.getParticipants()
                                                                                    .contains(loggedInUser)).toList();
        return auctionItemsWhereUserIsNotInParticipants;
    }

    @Override
    public void joinAuction(AuctionItem auctionItem) {
        if (loggedInUser.getAccount().getBalance() < auctionItem.getDeposit()){
            throw new NotEnoughBalanceException("Not enough balance");
        }
        loggedInUser.getAccount().setBalance(loggedInUser.getAccount().getBalance() - auctionItem.getDeposit());
        auctionItem.getParticipants().add(loggedInUser);
        loggedInUser.getAuctionItems().add(auctionItem);
    }

    @Override
    public List<Property> findPropertyWithoutRelatedAuctionItem() {
        return StreamSupport.stream(propertyRepository.findAll().spliterator(), false)
                .filter(Predicate.not(property -> getAuctionItems()
                        .stream()
                        .anyMatch(i -> i.getProperty().getId().equals(property.getId()))))
                .collect(Collectors.toList());
    }

    private int getAuctionItemsCountByState(AuctionState state){
        return (int)getAuctionItems().stream().filter(item -> item.getState().equals(state)).count();
    }

    private Boolean authorizeLoggedInUser(){
        if (loggedInUser.getRole() != Role.ADMIN){
            throw new PermissionDeniedException("Permission denied");
        }
        return true;
    }

    private Boolean validateAuctionItem(AuctionItem auctionItem){
        if (auctionItem.getDeposit() <= 0 ||
            auctionItem.getUpsetPrice() <= 0 ||
            auctionItem.getMinBidIncrement() <= 0 ||
            auctionItem.getStartBidding().compareTo(auctionItem.getEndBidding()) >= 0){
            throw new ValidationException("Validating auction item failed.");
        }
        return true;
    }

    private Boolean validateProperty(Property property){
        if (property.getName().isBlank() ||
            property.getDescription().isBlank() ||
            property.getOwnerName().isBlank()){
            throw new ValidationException("Validating property failed");
        }
        return true;
    }
    private Boolean validatePersonalProperty(PersonalProperty pp){
        if (pp.getWeight() <= 0 ||
            pp.getSize().getDepth() <= 0 ||
            pp.getSize().getWidth() <= 0 ||
            pp.getSize().getHeight() <= 0){
            throw new ValidationException("Validating PersonalProperty failed");
        }
        return true;
    }
}
