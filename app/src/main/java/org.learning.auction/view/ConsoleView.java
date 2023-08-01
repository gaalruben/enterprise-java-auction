package org.learning.auction.view;

import lombok.Getter;
import org.learning.auction.domain.AuctionItem;
import org.learning.auction.domain.AuctionState;
import org.learning.auction.domain.Category;
import org.learning.auction.domain.Credentials;
import org.learning.auction.domain.Dimension;
import org.learning.auction.domain.PersonalProperty;
import org.learning.auction.domain.Property;
import org.learning.auction.domain.Role;
import org.learning.auction.domain.User;
import org.learning.auction.service.Statistics;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.stream.IntStream;

@Component
public class ConsoleView {
    Scanner scanner = new Scanner(System.in);
    private static final String SEPARATOR = System.lineSeparator();

    private static final String PATTERN = "yyyy-MM-dd HH:mm";
    private static final DateTimeFormatter FMT = new DateTimeFormatterBuilder()
            .appendPattern(PATTERN).toFormatter();

    @Getter
    Hashtable<Integer, String> menuItemDict;
    public ConsoleView(){
        menuItemDict = new Hashtable<>();
    }

    public void welcomeMessage(User loggedInUser){
            System.out.println("Login successful.");
            printEmptyLine();
            System.out.println("---Welcome---");
            System.out.println("Welcome " + loggedInUser.getFullName() + ". Your role: " + loggedInUser.getRole().toString());
            initMenuItems(loggedInUser.getRole());
    }

    public Credentials askForCredentials() {
        System.out.println("---Auction Application Login---");
        System.out.println("Please enter your credentials.");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        return new Credentials(username, password);
    }

    public void displayStatistics(Statistics statistics){
        System.out.println("---Application Statistics---");
        System.out.println("Number of all auction items: " + statistics.getNumberOfAllAuctionItems());
        System.out.println("Finished: " + statistics.getNumberOfFinishedAuctionItems() +
                            ", In progress: " + statistics.getNumberOfInProgressAuctionItems() +
                            ", Not started: " + statistics.getNumberOfNotStartedAuctionItems());
        System.out.println("Number of users: " + statistics.getNumberOfUsers());
    }

    public void printExitMessage(User user) {
        System.out.println("Press q to exit...");
        String enter;
        do {
            enter = scanner.nextLine();
        } while (!enter.equals("q"));
        System.out.println("See you next time " + user.getFullName() + "!");
    }

    public void printEmptyLine() {
        System.out.println();
    }

    public int askForMenuItemNumber(Role role){
        System.out.println("---Auction Application Main Menu---");

        for (int i = 1; i <= 3; i++) {
            System.out.println( i + ". " + menuItemDict.get(i));
        }

        List<String> validInputs = Arrays.asList("1", "2", "3");
        return askForMenuItemNumber(validInputs);
    }

    public PersonalProperty personalPropertyCreation(){
        try{
            System.out.println("---Personal Property Creation---");
            System.out.println("Please enter new personal property details.");
            System.out.print("Name: ");
            String name = scanner.nextLine();
            System.out.print("Description: ");
            String description = scanner.nextLine();
            System.out.print("Category: ");
            String categoryString = scanner.nextLine();
            Category category;

                category = Category.valueOf(categoryString.toUpperCase());

            System.out.print("Owner name: ");
            String ownerName = scanner.nextLine();
            System.out.print("Weight (g): ");
            String weight = scanner.nextLine();
            System.out.print("Width (cm): ");
            String width = scanner.nextLine();
            System.out.print("Height (cm): ");
            String height = scanner.nextLine();
            System.out.print("Depth (cm): ");
            String depth = scanner.nextLine();

            PersonalProperty personalProperty = PersonalProperty.builder()
                                                                .name(name)
                                                                .description(description)
                                                                .category(category)
                                                                .ownerName(ownerName)
                                                                .weight(Double.parseDouble(weight))
                                                                .size(Dimension.builder()
                                                                        .width(Double.parseDouble(width))
                                                                        .height(Double.parseDouble(height))
                                                                        .depth(Double.parseDouble(depth))
                                                                        .build())
                                                                .build();

                return personalProperty;
            }
        catch(Exception e){
            printPersonalPropertyCreationFailed();
            throw e;
        }
    }
    public void printPersonalPropertyCreatedSuccessfully(){
        System.out.println("Personal Property has been created and saved.");
    }

    public void printAuctionItemCreatedSuccessfully(){
        System.out.println("Auction Item has been created and saved.");
    }

    public void printPersonalPropertyCreationFailed(){
        System.out.println("Personal Property creation failed.");
    }
    public void printAuctionItemCreationFailed(){
        System.out.println("Auction Item creation failed.");
    }
    public void printExitApplication(){
        System.out.println("Auction application quit.");
        System.exit(0);
    }

    public AuctionItem auctionItemCreation(List<Property> propertiesWithoutAuctionItem){
        System.out.println("---Auction Item Creation---");
        System.out.println("List of all Personal properties that do not yet belong to an Auction Item:");

        List<String> validInputs = new ArrayList<>();
        IntStream.range(0, propertiesWithoutAuctionItem.size() - 1)
                .forEach(index -> {
                    System.out.println((index + 1) + ". " +
                            propertiesWithoutAuctionItem.get(index).getName() +
                            "(" + propertiesWithoutAuctionItem.get(index).getCategory() + ")");
                    validInputs.add(Integer.toString(index));
                });

        int menuItemNumber = askForMenuItemNumber(validInputs);
        Property pp = propertiesWithoutAuctionItem.get(menuItemNumber - 1);

        System.out.print("Enter Auction attributes:");
        System.out.print("Deposit (HUF): ");
        String depositString = scanner.nextLine();
        System.out.print("Upset Price (HUF): ");
        String upsetPriceString = scanner.nextLine();
        System.out.print("Bid Limit (HUF): ");
        String bidLimitString = scanner.nextLine();
        System.out.print("Bidding Start (YYYY-MM-DD hh:mm): ");
        String biddingStartString = scanner.nextLine();
        System.out.print("Bidding End (YYYY-MM-DD hh:mm): ");
        String biddingEndString = scanner.nextLine();

        try{
            double deposit = Double.parseDouble(depositString);
            double upsetPrice = Double.parseDouble(upsetPriceString);
            double minBidIncrement = Double.parseDouble(bidLimitString);
            LocalDateTime startBidding = LocalDateTime.parse(biddingStartString, FMT);
            LocalDateTime endBidding = LocalDateTime.parse(biddingEndString, FMT);

            AuctionItem auctionItem = AuctionItem.builder()
                    .deposit(deposit)
                    .upsetPrice(upsetPrice)
                    .minBidIncrement(minBidIncrement)
                    .startBidding(startBidding)
                    .endBidding(endBidding)
                    .price(0)
                    .property(pp)
                    .state(AuctionState.NOT_STARTED)
                    .build();

            return auctionItem;
        }
        catch(Exception e){
            printAuctionItemCreationFailed();
            throw e;
        }
    }

    public void displayAuctionItems(List<AuctionItem> auctionItems){
        System.out.println("---Auction Items---");
        for(AuctionItem auctionItem : auctionItems){
            System.out.println("- " + auctionItem.getProperty().getName());
            System.out.println("\tProperty category: " + auctionItem.getProperty().getCategory().toString());
            System.out.println("\tProperty description: " + auctionItem.getProperty().getDescription());
            System.out.println("\tStatus: " + auctionItem.getState().toString());
            System.out.println("\tBidding start: " + auctionItem.getStartBidding().toString());
            System.out.println("\tBidding end: " + auctionItem.getEndBidding().toString());
            System.out.println("\tDeposit: " + auctionItem.getDeposit());
        }
    }

    public AuctionItem displayJoinAuction(List<AuctionItem> auctionItems, User user){
        System.out.println("---Join Auction---");
        System.out.println("List of Auction Items:");
        List<String> validInputs = new ArrayList<>();
        IntStream.range(0, auctionItems.size() - 1)
                .forEach(index -> {
                    System.out.println((index + 1) + ". " +
                            auctionItems.get(index).getProperty().getName());
                    validInputs.add(Integer.toString(index + 1));
                });
        System.out.println("(Your current balance: " + user.getAccount().getBalance() + ")");
        int menuItemNumber = askForMenuItemNumber(validInputs);
        AuctionItem auctionItem = auctionItems.get(menuItemNumber - 1);
        return auctionItem;
    }

    public void printSuccessfullyJoinedAuction(){
        System.out.println("Successfully joined Auction.");
    }

    private void initMenuItems(Role role){
        menuItemDict.put(3, "Quit application");
        if (role == Role.ADMIN){
            menuItemDict.put(2, "Create Auction Item");
            menuItemDict.put(1, "Create Personal Property");
        }else{
            menuItemDict.put(2, "Join Auction");
            menuItemDict.put(1, "List Auction Items");
        }
    }

    private int askForMenuItemNumber(List<String> validInputs){
        String input;
        do {
            System.out.print("Please choose an item: ");
            input = scanner.nextLine();
            if(!validInputs.contains(input)){
                System.out.println("Invalid menu item id");
            }
        } while (!validInputs.contains(input));
        return Integer.parseInt(input);
    }
}
