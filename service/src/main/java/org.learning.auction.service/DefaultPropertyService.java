package org.learning.auction.service;

import lombok.Getter;
import lombok.Setter;
import org.learning.auction.domain.PersonalProperty;
import org.learning.auction.domain.Property;
import org.learning.auction.domain.RealProperty;
import org.learning.auction.domain.Role;
import org.learning.auction.persistence.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Getter
@Setter
@Service
@Transactional
public class DefaultPropertyService implements PropertyService{
    @Autowired
    PropertyRepository propertyRepository;

    @Autowired
    UserService userService;

    @Autowired
    AuctionItemService auctionItemService;

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
    public List<Property> findPropertyWithoutRelatedAuctionItem() {
        return StreamSupport.stream(propertyRepository.findAll().spliterator(), false)
                .filter(Predicate.not(property -> auctionItemService.getAuctionItems()
                        .stream()
                        .anyMatch(i -> i.getProperty().getId().equals(property.getId()))))
                .collect(Collectors.toList());
    }

    @Override
    public List<Property> getPersonalProperties() {
        return  StreamSupport.stream(propertyRepository.findAll().spliterator(), false)
                .filter(property -> property instanceof PersonalProperty)
                .collect(Collectors.toList());
    }

    @Override
    public List<Property> getRealProperties() {
        return  StreamSupport.stream(propertyRepository.findAll().spliterator(), false)
                .filter(property -> property instanceof RealProperty)
                .collect(Collectors.toList());
    }

    @Override
    public Property findPropertyByName(String name) {
        List<Property> allProperties = StreamSupport
                .stream(propertyRepository.findAll().spliterator(), false)
                .toList();
        Property result = allProperties.stream()
                .filter(property -> property.getName().equals(name))
                .findFirst().orElseThrow(() -> new AuthenticationException("This property does not exist."));
        return result;
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

    private Boolean authorizeLoggedInUser(){
        if (userService.getLoggedinUser().getRole() != Role.ADMIN){
            throw new PermissionDeniedException("Permission denied");
        }
        return true;
    }
}
