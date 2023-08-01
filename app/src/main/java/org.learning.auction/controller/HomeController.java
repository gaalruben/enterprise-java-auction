package org.learning.auction.controller;

import org.learning.auction.domain.User;
import org.learning.auction.model.StatisticsDTO;
import org.learning.auction.model.UserDetailsDTO;
import org.learning.auction.service.Statistics;
import org.learning.auction.service.StatisticsService;
import org.learning.auction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {
    @Autowired
    UserService userService;

    @Autowired
    StatisticsService statisticsService;

    @RequestMapping(value = "/")
    public String showRootPage(Model model){
        User loggedInUser = userService.getLoggedinUser();
        UserDetailsDTO userDetails = new UserDetailsDTO(loggedInUser.getFullName(),
                                                        loggedInUser.getRole().toString(),
                (int)loggedInUser.getAccount().getBalance() +  " HUF");

        Statistics statistics = statisticsService.calculateStatistics();

        StatisticsDTO statisticsDTO = new StatisticsDTO(statistics.getNumberOfAllAuctionItems(),
                                                        statistics.getNumberOfFinishedAuctionItems(),
                                                        statistics.getNumberOfInProgressAuctionItems(),
                                                        statistics.getNumberOfNotStartedAuctionItems(),
                                                        statistics.getNumberOfUsers());

        model.addAttribute("userDetails", userDetails);
        model.addAttribute("statistics", statisticsDTO);

        return "index";
    }
}
