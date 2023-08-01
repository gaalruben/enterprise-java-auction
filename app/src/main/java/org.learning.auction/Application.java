
package org.learning.auction;

import org.learning.auction.domain.AuctionItem;
import org.learning.auction.domain.AuctionState;
import org.learning.auction.domain.PersonalProperty;
import org.learning.auction.domain.Role;
import org.learning.auction.persistence.FileDataStore;
import org.learning.auction.service.DefaultAuctionService;
import org.learning.auction.service.Statistics;
import org.learning.auction.view.ConsoleView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Profile("!default")
public class Application implements CommandLineRunner {

    @Autowired
    private FileDataStore dataStore;
    @Autowired
    private DefaultAuctionService auctionService;
    @Autowired
    private ConsoleView consoleView;

    @Override
    public void run(String... args) throws Exception {
        dataStore.loadData();
        consoleView.welcomeMessage(auctionService.authenticateUser(consoleView.askForCredentials()));
        Statistics statistics = auctionService.calculateStatistics();
        consoleView.displayStatistics(statistics);

        displayMenu();

        // consoleView.printExitMessage(auctionService.getLoggedInUser());
    }

    private void displayMenu(){
        if(auctionService.getLoggedInUser().getRole() == Role.ADMIN){
            runAdminMenuItem(consoleView.askForMenuItemNumber(Role.ADMIN));
        }else{
            runUserMenuItem(consoleView.askForMenuItemNumber(Role.USER));
        }
    }
    private void runAdminMenuItem(int itemNumber){
        switch(itemNumber){
            case 1:
                PersonalProperty pp = consoleView.personalPropertyCreation();
                try{
                    auctionService.createProperty(pp);
                    consoleView.printPersonalPropertyCreatedSuccessfully();
                    displayMenu();
                }
                catch(Exception exception){
                    consoleView.printPersonalPropertyCreationFailed();
                    runAdminMenuItem(itemNumber);
                }
                break;
            case 2:
                AuctionItem auctionItem = consoleView.auctionItemCreation(auctionService.findPropertyWithoutRelatedAuctionItem());
                try{
                    auctionService.createAuctionItem(auctionItem);
                    consoleView.printAuctionItemCreatedSuccessfully();
                    displayMenu();
                }
                catch(Exception exception){
                    consoleView.printAuctionItemCreationFailed();
                    runAdminMenuItem(itemNumber);
                }
                break;
            case 3:
                consoleView.printExitApplication();
                break;
        }
    }
    private void runUserMenuItem(int itemNumber){
        switch(itemNumber){
            case 1:
                consoleView.displayAuctionItems(auctionService.getAuctionItems());
                displayMenu();
                break;
            case 2:
                List<AuctionItem> auctionItems = auctionService.findAuctionItemsWhereUserIsNotInParticipants()
                        .stream()
                        .filter(item -> item.getState() != AuctionState.FINISHED)
                        .collect(Collectors.toList());
                AuctionItem auctionItem = consoleView.displayJoinAuction(auctionItems, auctionService.getLoggedInUser());
                auctionService.joinAuction(auctionItem);
                consoleView.printSuccessfullyJoinedAuction();
                displayMenu();
                break;
            case 3:
                consoleView.printExitApplication();
                break;
        }
    }

}

