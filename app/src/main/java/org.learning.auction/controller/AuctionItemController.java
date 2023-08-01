package org.learning.auction.controller;

import com.fasterxml.jackson.databind.introspect.TypeResolutionContext;
import lombok.Getter;
import org.learning.auction.domain.AuctionItem;
import org.learning.auction.domain.AuctionState;
import org.learning.auction.domain.User;
import org.learning.auction.model.AuctionItemModel;
import org.learning.auction.model.AuctionItemRowDTO;
import org.learning.auction.model.BiddingDTO;
import org.learning.auction.service.AuctionItemService;
import org.learning.auction.service.PropertyService;
import org.learning.auction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Controller
public class AuctionItemController {
    @Autowired
    AuctionItemService auctionItemService;
    @Autowired
    PropertyService propertyService;
    @Autowired
    UserService userService;

    @ModelAttribute("auctionItemModel")
    public AuctionItemModel getAuctionItemModel() {
        return new AuctionItemModel();
    }

    @RequestMapping(value = "/auction-items-management")
    public String showRootPage(Model model){
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm");

        List<AuctionItem> auctionItems = auctionItemService.getAuctionItems();
        List<AuctionItemRowDTO> auctionItemRows = new ArrayList<>();
        for (AuctionItem auctionItem : auctionItems) {
            AuctionItemRowDTO row = AuctionItemRowDTO.builder()
                    .id(auctionItem.getId())
                    .propertyName(auctionItem.getProperty().getName())
                    .upsetPrice((int)auctionItem.getUpsetPrice())
                    .deposit((int)auctionItem.getDeposit())
                    .startBidding(auctionItem.getStartBidding().format(FORMATTER))
                    .endBidding(auctionItem.getEndBidding().format(FORMATTER))
                    .status(auctionItem.getState().toString())
                    .build();
            auctionItemRows.add(row);
        }

        model.addAttribute("auctionItemRows", auctionItemRows);
        model.addAttribute("propertiesWithoutAuctionItem", propertyService.findPropertyWithoutRelatedAuctionItem());

        return "auction-items-management";
    }

    @RequestMapping(value = "/auction-items")
    public String auctionItems(Model model){
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm");

        List<AuctionItem> auctionItems = auctionItemService.getAuctionItems();
        List<AuctionItemRowDTO> auctionItemRows = new ArrayList<>();
        for (AuctionItem auctionItem : auctionItems) {
            AuctionItemRowDTO row = AuctionItemRowDTO.builder()
                    .id(auctionItem.getId())
                    .propertyName(auctionItem.getProperty().getName())
                    .upsetPrice((int)auctionItem.getUpsetPrice())
                    .deposit((int)auctionItem.getDeposit())
                    .startBidding(auctionItem.getStartBidding().format(FORMATTER))
                    .endBidding(auctionItem.getEndBidding().format(FORMATTER))
                    .status(auctionItem.getState().toString())
                    .build();

            if (auctionItem.getParticipants().contains(userService.getLoggedinUser())){
                row.setUserIsInParticipants(true);
            }else{
                row.setUserIsInParticipants(false);
            }

            auctionItemRows.add(row);
        }

        model.addAttribute("auctionItemRows", auctionItemRows);

        return "auction-items";
    }
    @RequestMapping(value = "/bidding")
    public String biddingPage(Model model){
        BiddingDTO biddingDTO = getBiddingDTO();
        model.addAttribute("priceError", "");

        if(biddingDTO == null)
            return "redirect:/";

        model.addAttribute("biddingDTO", biddingDTO);
        return "bidding";
    }

    private BiddingDTO getBiddingDTO() {
        DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd. HH:mm");

        AuctionItem auctionItem = userService.getLoggedinUser().getAuction_item();
        BiddingDTO biddingDTO;
        if (auctionItem == null) {
            biddingDTO = null;
        }else{
            String lastBiddingDate = auctionItemService.getLastBiddingDate(auctionItem) != null ? auctionItemService.getLastBiddingDate(auctionItem).format(FORMATTER) : "";

            biddingDTO = BiddingDTO.builder()
                    .id(auctionItem.getId())
                    .propertyName(auctionItem.getProperty().getName())
                    .propertyDescription(auctionItem.getProperty().getDescription())
                    .upsetPrice((int)auctionItem.getUpsetPrice())
                    .deposit((int)auctionItem.getDeposit())
                    .price((int)auctionItem.getPrice())
                    .minBidIncrement((int)auctionItem.getMinBidIncrement())
                    .startBidding(auctionItem.getStartBidding().format(FORMATTER))
                    .endBidding(auctionItem.getEndBidding().format(FORMATTER))
                    .status(auctionItem.getState().toString())
                    .lastBidding(lastBiddingDate)
                    .build();
        }
        return biddingDTO;
    }

    @RequestMapping(value = "/bid")
    public String bid(@RequestParam(value = "price",required = true, name = "price") Integer price,Model model){
        AuctionItem ai = userService.getLoggedinUser().getAuction_item();

        if(!auctionItemService.validateBid(price, (int)ai.getMinBidIncrement(), (int)ai.getPrice())) {
             model.addAttribute("priceError", "Bidding price must be greater than minimum bid increment plus the actual price.");
            BiddingDTO biddingDTO = getBiddingDTO();
            model.addAttribute("biddingDTO", biddingDTO);
             return "bidding";
        }else{
            model.addAttribute("priceError", "");
        }

        if (ai != null)
            auctionItemService.bid(price);

        return "redirect:/bidding";
    }

    @RequestMapping(value = "/start")
    public String start(@RequestParam(value = "id", required = true) Long id){
        auctionItemService.setAuctionItemStateById(id, AuctionState.IN_PROGRESS);

        return "redirect:/auction-items-management";
    }

    @RequestMapping(value = "/join")
    public String join(@RequestParam(value = "id", required = true) Long id){
        auctionItemService.joinAuction(auctionItemService.getAuctionItemById(id));


        return "redirect:/auction-items";
    }

    @RequestMapping(value = "/end")
    public String end(@RequestParam(value = "id", required = true) Long id){
        auctionItemService.setAuctionItemStateById(id, AuctionState.FINISHED);

        return "redirect:/auction-items-management";
    }

    @PostMapping(value = "/auction-items-management")
    public String createAuctionItem(@Valid AuctionItemModel auctionItemModel, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model){
        String result;
        if (bindingResult.hasErrors()) {
            model.addAttribute("propertiesWithoutAuctionItem", propertyService.findPropertyWithoutRelatedAuctionItem());
            result = "auction-items-management";
        } else {
           AuctionItem auctionItem = AuctionItem.builder()
                   .property(propertyService.findPropertyByName(auctionItemModel.getPropertyName()))
                   .upsetPrice(auctionItemModel.getUpsetPrice())
                   .deposit(auctionItemModel.getDeposit())
                   .minBidIncrement(auctionItemModel.getMinBidIncrement())
                   .startBidding(LocalDateTime.parse(auctionItemModel.getStartBidding()))
                   .endBidding(LocalDateTime.parse(auctionItemModel.getEndBidding()))
                   .state(AuctionState.NOT_STARTED)
                   .build();
            auctionItemService.createAuctionItem(auctionItem);
            redirectAttributes.addFlashAttribute("successMessage", "Auction item added successfully");
            result = "redirect:/auction-items-management";
        }
        return result;
    }
}
