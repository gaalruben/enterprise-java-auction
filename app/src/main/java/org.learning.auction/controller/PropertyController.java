package org.learning.auction.controller;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.learning.auction.domain.Category;
import org.learning.auction.domain.Dimension;
import org.learning.auction.domain.PersonalProperty;
import org.learning.auction.domain.Property;
import org.learning.auction.domain.RealProperty;
import org.learning.auction.model.PersonalPropertiesRowDTO;
import org.learning.auction.model.PropertyDTO;
import org.learning.auction.model.RealPropertiesRowDTO;
import org.learning.auction.service.PropertyService;
import org.learning.auction.service.Statistics;
import org.learning.auction.service.StatisticsService;
import org.learning.auction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class PropertyController {
    @Autowired
    PropertyService propertyService;

    @ModelAttribute("propertyModel")
    public PropertyDTO getPropertyModel() {
        PropertyDTO propertyDTO = new PropertyDTO();
        propertyDTO.setPersonal(true);
        return propertyDTO;
    }

    @RequestMapping(value = "/properties-management")
    public String propertiesManagement(Model model){
        List<RealPropertiesRowDTO> realPropertyRows = getRealPropertyRowList();
        List<PersonalPropertiesRowDTO> personalPropertyRows = getPersonalPropertyRowList();

        model.addAttribute("personalPropertyRows", personalPropertyRows);
        model.addAttribute("realPropertyRows", realPropertyRows);
        model.addAttribute("propertyModel", PropertyDTO.builder().build());

        return "properties-management";
    }

    @PostMapping(value = "/properties-management")
    public String createProperty(@Valid PropertyDTO propertyModel, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model){
        String result;
        if (bindingResult.hasErrors()) {
            result = "properties-management";
        } else {
            Property prop;
            if (propertyModel.isPersonal()){
                prop = PersonalProperty.builder()
                        .name(propertyModel.getName())
                        .category(Category.valueOf(propertyModel.getCategory()))
                        .description(propertyModel.getDescription())
                        .ownerName(propertyModel.getOwnerName())
                        .weight(propertyModel.getWeight())
                        .size(Dimension.builder().width(propertyModel.getSizeWidth())
                                .height(propertyModel.getSizeWidth())
                                .depth(propertyModel.getSizeDepth()).build())
                        .build();
            }else{
                prop = RealProperty.builder()
                        .name(propertyModel.getName())
                        .category(Category.valueOf(propertyModel.getCategory()))
                        .description(propertyModel.getDescription())
                        .ownerName(propertyModel.getOwnerName())
                        .location(propertyModel.getLocation())
                        .areaSize(propertyModel.getAreaSize())
                        .build();
            }
            propertyService.createProperty(prop);
            redirectAttributes.addFlashAttribute("successMessage", "Auction item added successfully");
            result = "redirect:/properties-management";
        }

        model.addAttribute("propertyModel", propertyModel);
        return result;
    }

    private List<PersonalPropertiesRowDTO> getPersonalPropertyRowList(){
        List<Property> personalProperties = propertyService.getPersonalProperties();
        List<PersonalPropertiesRowDTO> personalPropertyRows = new ArrayList<>();
        for (Property property : personalProperties) {
            PersonalProperty pp = (PersonalProperty) property;
            PersonalPropertiesRowDTO row = PersonalPropertiesRowDTO.builder()
                    .name(pp.getName())
                    .category(pp.getCategory().toString())
                    .description(pp.getDescription())
                    .ownerName(pp.getOwnerName())
                    .weight((int)pp.getWeight() + " g")
                    .sizeWidth((int)pp.getSize().getWidth() + " cm")
                    .sizeHeight((int)pp.getSize().getHeight() + " cm")
                    .sizeDepth((int)pp.getSize().getDepth() + " cm")
                    .build();

            personalPropertyRows.add(row);
        }
        return personalPropertyRows;
    }

    private List<RealPropertiesRowDTO> getRealPropertyRowList(){
        List<Property> realProperties = propertyService.getRealProperties();
        List<RealPropertiesRowDTO> realPropertyRows = new ArrayList<>();

        for (Property property : realProperties) {
            RealProperty rp = (RealProperty) property;
            RealPropertiesRowDTO row = RealPropertiesRowDTO.builder()
                    .name(rp.getName())
                    .category(rp.getCategory().toString())
                    .description(rp.getDescription())
                    .ownerName(rp.getOwnerName())
                    .location(rp.getLocation())
                    .areaSize((int)rp.getAreaSize() + " nm")
                    .build();


            realPropertyRows.add(row);
        }
        return realPropertyRows;
    }
}
