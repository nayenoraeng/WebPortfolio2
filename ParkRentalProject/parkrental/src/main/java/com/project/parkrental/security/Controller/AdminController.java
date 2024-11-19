package com.project.parkrental.security.Controller;

import com.project.parkrental.security.DTO.SellerDto;
import com.project.parkrental.security.DTO.UserDto;
import com.project.parkrental.security.Service.SellerService;
import com.project.parkrental.security.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private SellerService sellerService;

    @GetMapping("/userlist")
    public String getUserList(Model model) {
        List<UserDto> users = userService.getAllUsers();

        List<SellerDto> sellers = sellerService.getAllSellers();
        model.addAttribute("users", users);
        model.addAttribute(("sellers"), sellers);

        return "admin/userlist";
    }

//    @PostMapping("/updateSellerLockStatus")
//    public String updateSellerLockStatus(@RequestParam long idx, @RequestParam int isLocked) {
//        Seller existingSeller = sellerService.findSellerById(idx);
//        return ;
//    }

}
