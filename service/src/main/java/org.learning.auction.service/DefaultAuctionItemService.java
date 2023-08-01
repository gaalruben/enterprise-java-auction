package org.learning.auction.service;

import lombok.Getter;
import lombok.Setter;
import org.learning.auction.domain.AuctionItem;
import org.learning.auction.domain.AuctionState;
import org.learning.auction.domain.BidHistory;
import org.learning.auction.domain.Role;
import org.learning.auction.domain.User;
import org.learning.auction.persistence.AuctionItemRepository;
import org.learning.auction.persistence.BidHistoryRepository;
import org.learning.auction.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Getter
@Setter
@Service
@Transactional
public class DefaultAuctionItemService implements AuctionItemService{
    @Autowired
    AuctionItemRepository auctionItemRepository;
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    BidHistoryRepository bidHistoryRepository;

    @Override
    public void setAuctionItemStateById(Long id, AuctionState state) {
        Optional<AuctionItem> auctionItem = auctionItemRepository.findById(id);
        auctionItem.get().setState(state);

        if(state == AuctionState.FINISHED && !auctionItem.get().getHistory().isEmpty()){
            double deposit = auctionItem.get().getDeposit();
            double price = auctionItem.get().getPrice();
            double valueToSubstract = price - deposit;
            String winnerName = auctionItem.get().getHistory().stream().max(Comparator.comparing(bh -> bh.getPrice())).get().getUserName();
            User winner = userService.findUserByUsername(winnerName);
            double newBalance = winner.getAccount().getBalance() - valueToSubstract;

            winner.getAccount().setBalance(newBalance);
            auctionItem.get().setWinner(winner);

            for (User user: auctionItem.get().getParticipants()) {
                if (user.getCredentials().getUsername() != winnerName){
                    double newLoserBalance = user.getAccount().getBalance() + deposit;
                    user.getAccount().setBalance(newLoserBalance);
                    userRepository.save(user);
                }
            }
        }

        auctionItemRepository.save(auctionItem.get());
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
    public AuctionItem getAuctionItemById(Long id) {
        Optional<AuctionItem> auctionItem = auctionItemRepository.findById(id);
        return auctionItem.get();
    }

    @Override
    public LocalDateTime getLastBiddingDate(AuctionItem auctionItem) {
        if (auctionItem.getHistory().isEmpty())
            return null;

        return Collections.max(auctionItem.getHistory(), Comparator.comparing(BidHistory::getTime)).getTime();
    }

    @Override
    public List<AuctionItem> findAuctionItemsWhereUserIsNotInParticipants() {
        List<AuctionItem> auctionItemsWhereUserIsNotInParticipants = StreamSupport.stream(auctionItemRepository.findAll().spliterator(), false)
                .filter(auctionItem -> !auctionItem.getParticipants()
                        .contains(userService.getLoggedinUser())).toList();
        return auctionItemsWhereUserIsNotInParticipants;
    }

    @Override
    public void joinAuction(AuctionItem auctionItem) {
        User loggedInUser = userService.getLoggedinUser();
        if (loggedInUser.getAccount().getBalance() < auctionItem.getDeposit()){
            throw new NotEnoughBalanceException("Not enough balance");
        }
        loggedInUser.getAccount().setBalance(loggedInUser.getAccount().getBalance() - auctionItem.getDeposit());

        AuctionItem ai = auctionItemRepository.findById(auctionItem.getId()).get();
        ai.getParticipants().add(loggedInUser);
        loggedInUser.setAuction_item(auctionItem);
        loggedInUser.getAuctionItems().add(auctionItem);
        auctionItemRepository.save(auctionItem);
        userRepository.save(loggedInUser);
    }

    @Override
    public int numberOfAllAuctionItems() {
        return StreamSupport.stream(auctionItemRepository.findAll().spliterator(), false).toList().size();
    }

    @Override
    public void bid(int price) {
        AuctionItem auctionItem = userService.getLoggedinUser().getAuction_item();
        auctionItem.setPrice(price);
        BidHistory history = BidHistory.builder()
                .auction_item(auctionItem)
                .price(price)
                .userName(userService.getLoggedinUser().getCredentials().getUsername())
                .time(LocalDateTime.now())
                .build();
        auctionItem.getHistory().add(history);
        auctionItemRepository.save(auctionItem);
        bidHistoryRepository.save(history);
    }

    @Override
    public boolean validateBid(int price, int minBid, int ogPrice) {
        return price >= ogPrice + minBid;
    }

    private Boolean authorizeLoggedInUser(){
        if (userService.getLoggedinUser().getRole() != Role.ADMIN){
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
}
