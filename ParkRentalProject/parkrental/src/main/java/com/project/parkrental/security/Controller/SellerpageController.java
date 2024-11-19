package com.project.parkrental.security.Controller;

import com.project.parkrental.security.DTO.SellerDto;
import com.project.parkrental.security.Service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/seller")
public class SellerpageController {

    @Autowired
    private SellerService sellerService;

    @GetMapping("/sellerpage")
    public String sellerpage(Model model) {
        return "seller/sellerpage";
    }
}
