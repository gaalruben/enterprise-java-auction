package org.learning.auction.persistence;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.learning.auction.domain.AuctionItem;
import org.learning.auction.domain.AuctionItemDto;
import org.learning.auction.domain.PersonalProperty;
import org.learning.auction.domain.Property;
import org.learning.auction.domain.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

@Component
public class FileDataStore implements DataStore {

    private static final String PERSONAL_PROPERTIES_FILE_NAME = "personal-properties.json";
    private static final String AUCTION_ITEMS_FILE_NAME = "auction-items.json";
    private static final String USERS_FILE_NAME = "users.json";
    private final String basePath;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ModelMapper modelMapper = new ModelMapper();
    private ArrayList<Property> properties;
    private ArrayList<User> users;
    private ArrayList<AuctionItem> auctionItems;

    public FileDataStore(@Value("${basePath}") String basePath) {
        this.basePath = basePath;
        objectMapper.findAndRegisterModules();

        TypeMap<AuctionItem, AuctionItemDto> auctionItemMapper = modelMapper.createTypeMap(AuctionItem.class, AuctionItemDto.class);
    }
    @Override
    public void loadData() {
        Property[] personalPropertiesFromFile = readProperties();
        properties = new ArrayList<Property>(Arrays.asList(personalPropertiesFromFile));

        User[] usersFromFile = readUsers();
        users = new ArrayList<User>(Arrays.asList(usersFromFile));

        auctionItems = new ArrayList<AuctionItem>(readAuctionItems());
    }

    @Override
    public List<Property> getProperties() {
        return Collections.unmodifiableList(properties);
    }

    @Override
    public List<User> getUsers() {
        return Collections.unmodifiableList(users);
    }

    @Override
    public List<AuctionItem> getAuctionItems() {
        return Collections.unmodifiableList(auctionItems);
    }

    @Override
    public void addProperty(Property personalProperty) {
        long id = properties.stream().max(Comparator.comparing(item -> item.getId())).get().getId() + 1;
        personalProperty.setId(id);
        properties.add(personalProperty);
    }

    @Override
    public void addAuctionItem(AuctionItem auctionItem) {
        long id = auctionItems.stream().max(Comparator.comparing(item -> item.getId())).get().getId() + 1;
        auctionItem.setId(id);
        auctionItems.add(auctionItem);
    }

    @Override
    public void saveProperties() {
        try {
            DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
            objectMapper.writer(pp).writeValue(
                    Paths.get(basePath, PERSONAL_PROPERTIES_FILE_NAME).toFile(),
                    properties);
        } catch (IOException e) {
            throw new RuntimeException("IO error happened while reading destinations: " + e.getMessage(), e);
        }
    }

    @Override
    public void saveAuctionItems() {
        try {
            List<AuctionItemDto> auctionItemsToSave = new ArrayList();

            for (AuctionItem item : auctionItems
            ) {
                AuctionItemDto data = new AuctionItemDto();
                data.setState(item.getState());
                data.setPropertyId(item.getProperty().getId());
                data.setId(item.getId());
                data.setDeposit(item.getDeposit());
                data.setEndBidding(item.getEndBidding());
                data.setStartBidding(item.getStartBidding());
                data.setUpsetPrice(item.getUpsetPrice());
                data.setMinBidIncrement(item.getMinBidIncrement());
                auctionItemsToSave.add(data);
            }

            DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
            objectMapper.writer(pp).writeValue(
                    Paths.get(basePath, AUCTION_ITEMS_FILE_NAME).toFile(),
                    auctionItemsToSave);
        } catch (IOException e) {
            throw new RuntimeException("IO error happened while reading destinations: " + e.getMessage(), e);
        }
    }

    private Property[] readProperties() {
        try {
            return objectMapper.readValue(
                    Paths.get(basePath, PERSONAL_PROPERTIES_FILE_NAME).toFile(),
                    PersonalProperty[].class);
        } catch (IOException e) {
            throw new RuntimeException("IO error happened while reading destinations: " + e.getMessage(), e);
        }
    }
    private User[] readUsers() {
        try {
            return objectMapper.readValue(
                    Paths.get(basePath, USERS_FILE_NAME).toFile(),
                    User[].class);
        } catch (IOException e) {
            throw new RuntimeException("IO error happened while reading destinations: " + e.getMessage(), e);
        }
    }
    private List<AuctionItem> readAuctionItems() {
        try {
            AuctionItemDto[] auctionItemData = objectMapper.readValue(
                    Paths.get(basePath, AUCTION_ITEMS_FILE_NAME).toFile(),
                    AuctionItemDto[].class);

            List<AuctionItem> auctionItemsFromFile = new ArrayList<AuctionItem>();

            for (AuctionItemDto data : auctionItemData
                 ) {
                AuctionItem auctionItem = new AuctionItem();
                auctionItem.setId(data.getId());
                auctionItem.setDeposit(data.getDeposit());
                auctionItem.setMinBidIncrement(data.getMinBidIncrement());
                auctionItem.setUpsetPrice(data.getUpsetPrice());
                auctionItem.setStartBidding(data.getStartBidding());
                auctionItem.setState(data.getState());
                auctionItem.setEndBidding(data.getEndBidding());
                auctionItem.setProperty(properties.stream().filter(p -> p.getId().equals(data.getPropertyId())).findFirst().orElse(null));
                auctionItemsFromFile.add(auctionItem);
            }

            return auctionItemsFromFile;

        } catch (IOException e) {
            throw new RuntimeException("IO error happened while reading destinations: " + e.getMessage(), e);
        }
    }

}
