package com.bookstore.controller;

import com.bookstore.entity.*;
import com.bookstore.entity.security.Role;
import com.bookstore.entity.security.UserRole;
import com.bookstore.repository.BookRepository;
import com.bookstore.service.*;
import com.bookstore.utility.USConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.*;

@Controller
public class HomeController {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private UserSecurityService userSecurityService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserPaymentService userPaymentService;

    @Autowired
    private UserShippingService userShippingService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartItemService cartItemService;

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/login")
    public String login(Model model) {
        model.addAttribute("classActiveLogin", true);
        return "myAccount";
    }


    @RequestMapping("/newUser")
    public String newUser(Model model){
        model.addAttribute("classActiveEdit", true);


        return "myProfile";
    }


    @PostMapping("/newUser")
    public String newUserPost(
            @ModelAttribute("user") User user,
            Model model
    ) throws Exception{
        model.addAttribute("classActiveNewAccount", true);
        model.addAttribute("email", user.getEmail());
        model.addAttribute("username", user.getUsername());

        if (userService.findByUsername(user.getUsername()) != null) {
            model.addAttribute("usernameExists", true);

            return "myAccount";
        }

        if (userService.findByEmail(user.getEmail()) != null) {
            model.addAttribute("emailExists", true);

            return "myAccount";
        }


        user.setPassword(user.getPassword());

        Role role = new Role();
        role.setRoleId(1);
        role.setName("ROLE_USER");
        Set<UserRole> userRoles = new HashSet<>();
        userRoles.add(new UserRole(user, role));
        userService.createUser(user, userRoles);

        model.addAttribute("orderList", user.getOrderList());
        model.addAttribute("registerSuccess", "true");
        return "myAccount";
    }

    @PostMapping("/updateUserInfo")
    public String updateUserInfo(@ModelAttribute("user")User user,
                                 @ModelAttribute("newPassword")String newPassword,
                                 Model model)throws Exception{

        User currentUser = userService.findById(user.getId());
        if(currentUser == null){
            throw new UsernameNotFoundException("User not found");
        }

        if(userService.findByEmail(user.getEmail()) != null){
            if(userService.findByEmail(user.getEmail()).getId() != currentUser.getId()){
                model.addAttribute("emailExists", true);
                return "myProfile";
            }
        }

        if(userService.findByUsername(user.getUsername()) != null){
            if(userService.findByUsername(user.getUsername()).getId() != currentUser.getId()){
                model.addAttribute("usernameExists", true);
                return "myProfile";
            }
        }

        if(newPassword != null && !newPassword.isEmpty()){
            String dbPassword = currentUser.getPassword();
            if(bCryptPasswordEncoder.matches(user.getPassword(), dbPassword)){
                currentUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
            }
            else{
                model.addAttribute("incorrectPassword", true);
                model.addAttribute("classActiveEdit", true);
                return "myProfile";
            }
        }

        currentUser.setFirstName(user.getFirstName());
        currentUser.setLastName(user.getLastName());
        currentUser.setUsername(user.getUsername());
        currentUser.setEmail(user.getEmail());

        userService.save(currentUser);

        model.addAttribute("updateSuccess", true);
        model.addAttribute("user", currentUser);
        model.addAttribute("classActiveEdit", true);

        model.addAttribute("listOfShippingAddresses", true);
        model.addAttribute("listOfCreditCards", true);

        UserDetails userDetails = userSecurityService.loadUserByUsername(currentUser.getUsername());

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(),
                userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "myProfile";

    }

    @RequestMapping("/bookshelf")
    public String bookshelf(Model model){
        List<Book> bookList = bookRepository.findAll();
        model.addAttribute("bookList", bookList);

        return "bookshelf";
    }

    @RequestMapping("/bookDetail/{id}")
    public String bookDetail(@PathVariable("id")Long id, Model model, Principal principal){
        if(principal != null){
            String username = principal.getName();
            User byUsername = userService.findByUsername(username);
            model.addAttribute("user", byUsername);
        }

        Book book = bookRepository.findById(id).get();

        model.addAttribute("book", book);

        List<Integer> qtyList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        model.addAttribute("qtyList", qtyList);
        model.addAttribute("qty", 1);

        return "bookDetail";
    }

    @RequestMapping("myProfile")
    public String myProfile(Model model, Principal principal){
        User byUsername = userService.findByUsername(principal.getName());
        model.addAttribute("user", byUsername);
        model.addAttribute("userPaymentList", byUsername.getUserPaymentList());
        model.addAttribute("userShippingList", byUsername.getUserShippingList());
        model.addAttribute("orderList", byUsername.getOrderList());


        model.addAttribute("userShipping", new UserShipping());

        model.addAttribute("listOfCreditCards", true);
        model.addAttribute("listOfShippingAddresses", true);

        List<String> stateList = USConstants.listOfUSStatesCode;
        Collections.sort(stateList);
        model.addAttribute("stateList", stateList);
        model.addAttribute("classActiveEdit", true);

        return "myProfile";
    }

    @RequestMapping("/listOfCreditCards")
    public String listOfCreditCards(Model model, Principal principal){
        User byUsername = userService.findByUsername(principal.getName());
        model.addAttribute("user", byUsername);
        model.addAttribute("userPaymentList", byUsername.getUserPaymentList());
        model.addAttribute("userShippingList", byUsername.getUserShippingList());
        model.addAttribute("orderList", byUsername.getOrderList());

        model.addAttribute("listOfCreditCards", true);
        model.addAttribute("classActiveBilling", true);
        model.addAttribute("listOfShippingAddresses", true);

        return "myProfile";
    }

    @RequestMapping("/listOfShippingAddresses")
    public String listOfShippingAddresses(Model model, Principal principal){
        User byUsername = userService.findByUsername(principal.getName());
        model.addAttribute("user", byUsername);
        model.addAttribute("userPaymentList", byUsername.getUserPaymentList());
        model.addAttribute("userShippingList", byUsername.getUserShippingList());
        model.addAttribute("orderList", byUsername.getOrderList());

        model.addAttribute("listOfCreditCards", true);
        model.addAttribute("classActiveShipping", true);
        model.addAttribute("listOfShippingAddresses", true);
        model.addAttribute("orderList", byUsername.getOrderList());

        return "myProfile";
    }

    @RequestMapping("/addNewShippingAddress")
    public String addNewShippingAddress(Model model, Principal principal){
        User byUsername = userService.findByUsername(principal.getName());
        model.addAttribute("user", byUsername);

        model.addAttribute("addNewShippingAddress", true);
        model.addAttribute("classActiveShipping", true);
        model.addAttribute("listOfCreditCards", true);
        model.addAttribute("userShipping", new UserShipping());

        List<String> stateList = USConstants.listOfUSStatesCode;
        Collections.sort(stateList);
        model.addAttribute("stateList", stateList);
        model.addAttribute("userPaymentList", byUsername.getUserPaymentList());
        model.addAttribute("userShippingList", byUsername.getUserShippingList());
        model.addAttribute("orderList", byUsername.getOrderList());

        return "myProfile";
    }

    @PostMapping("/addNewShippingAddress")
    public String addNewShippingAddressPost(@ModelAttribute("userShipping") UserShipping userShipping,
                                   Principal principal, Model model){

        User user = userService.findByUsername(principal.getName());
        userService.updateUserShipping(userShipping, user);

        model.addAttribute("user", user);
        model.addAttribute("userPaymentList", user.getUserPaymentList());
        model.addAttribute("userShippingList", user.getUserShippingList());
        model.addAttribute("listOfCreditCards", true);
        model.addAttribute("classActiveShipping", true);
        model.addAttribute("listOfShippingAddresses", true);
        model.addAttribute("orderList", user.getOrderList());

        return "myProfile";
    }

    @RequestMapping("/updateUserShipping/{id}")
    public String updateUserShipping(@PathVariable("id") Long shippingAddressId, Principal principal, Model model){
        User user = userService.findByUsername(principal.getName());
        UserShipping userShipping = userShippingService.findById(shippingAddressId);


        if(user.getId() != userShipping.getUser().getId()){
            return "badRequestPage";
        }
        else{
            model.addAttribute("user", user);
            model.addAttribute("userShipping", userShipping);

            List<String> stateList = USConstants.listOfUSStatesCode;
            Collections.sort(stateList);
            model.addAttribute("stateList", stateList);

            model.addAttribute("addNewShippingAddress", true);
            model.addAttribute("classActiveShipping", true);
            model.addAttribute("listOfCreditCards", true);

            model.addAttribute("userPaymentList", user.getUserPaymentList());
            model.addAttribute("userShippingList", user.getUserShippingList());

            model.addAttribute("orderList", user.getOrderList());
            return "myProfile";
        }
    }

    @RequestMapping("/removeUserShipping/{id}")
    public String removeUserShipping(@PathVariable("id") Long userShippingId, Principal principal, Model model){
        User user = userService.findByUsername(principal.getName());
        UserShipping userShipping = userShippingService.findById(userShippingId);

        if(user.getId() != userShipping.getUser().getId()){
            return "badRequestPage";
        }
        else{
            model.addAttribute("user", user);
            userShippingService.removeById(userShippingId);

            model.addAttribute("listOfShippingAddresses", true);
            model.addAttribute("classActiveShipping", true);
            model.addAttribute("addNewShippingAddress", true);

            model.addAttribute("userPaymentList", user.getUserPaymentList());
            model.addAttribute("userShippingList", user.getUserShippingList());
            model.addAttribute("orderList", user.getOrderList());

            return "myProfile";
        }
    }

    @PostMapping("/setDefaultShippingAddress")
    public String setDefaultShippingAddress(@ModelAttribute("defaultShippingAddressId") Long shippingId, Principal principal, Model model){
        User user = userService.findByUsername(principal.getName());
        userService.setDefaulShipping(shippingId, user);

        model.addAttribute("user", user);
        model.addAttribute("listOfCreditCards", true);
        model.addAttribute("classActiveShipping", true);
        model.addAttribute("listOfShippingAddresses", true);

        model.addAttribute("userPaymentList", user.getUserPaymentList());
        model.addAttribute("userShippingList", user.getUserShippingList());
        model.addAttribute("orderList", user.getOrderList());

        return "myProfile";

    }

    @RequestMapping("/addNewCreditCard")
    public String addNewCreditCard(Model model, Principal principal){
        User byUsername = userService.findByUsername(principal.getName());
        model.addAttribute("user", byUsername);

        model.addAttribute("addNewCreditCard", true);
        model.addAttribute("classActiveBilling", true);

        model.addAttribute("userBilling", new UserBilling());
        model.addAttribute("userPayment", new UserPayment());

        List<String> stateList = USConstants.listOfUSStatesCode;
        Collections.sort(stateList);
        model.addAttribute("stateList", stateList);
        model.addAttribute("listOfShippingAddresses", true);
        model.addAttribute("userPaymentList", byUsername.getUserPaymentList());
        model.addAttribute("userShippingList", byUsername.getUserShippingList());
        model.addAttribute("orderList", byUsername.getOrderList());

        return "myProfile";
    }

    @PostMapping("/addNewCreditCard")
    public String addNewCreditCard(@ModelAttribute("userPayment") UserPayment userPayment,
                                   @ModelAttribute("userBilling") UserBilling userBilling,
                                   Principal principal, Model model){

        User user = userService.findByUsername(principal.getName());
        userService.updateUserBilling(userBilling, userPayment, user);

        model.addAttribute("user", user);
        model.addAttribute("userPaymentList", user.getUserPaymentList());
        model.addAttribute("userShippingList", user.getUserShippingList());
        model.addAttribute("listOfCreditCards", true);
        model.addAttribute("classActiveBilling", true);
        model.addAttribute("listOfShippingAddresses", true);
        model.addAttribute("orderList", user.getOrderList());

        return "myProfile";
    }

    @RequestMapping("/updateCreditCard/{id}")
    public String updateCreditCard(@PathVariable("id") Long creditCardId, Principal principal, Model model){
        User user = userService.findByUsername(principal.getName());
        UserPayment userPayment = userPaymentService.findById(creditCardId);

        if(user.getId() != userPayment.getUser().getId()){
            return "badRequestPage";
        }
        else{
            model.addAttribute("user", user);
            UserBilling userBilling = userPayment.getUserBilling();
            model.addAttribute("userPayment", userPayment);
            model.addAttribute("userBilling", userBilling);

            List<String> stateList = USConstants.listOfUSStatesCode;
            Collections.sort(stateList);
            model.addAttribute("stateList", stateList);

            model.addAttribute("addNewCreditCard", true);
            model.addAttribute("classActiveBilling", true);
            model.addAttribute("listOfShippingAddresses", true);

            model.addAttribute("userPaymentList", user.getUserPaymentList());
            model.addAttribute("userShippingList", user.getUserShippingList());
            model.addAttribute("orderList", user.getOrderList());

            return "myProfile";
        }
    }

    @RequestMapping("/removeCreditCard/{id}")
    public String removeCreditCard(@PathVariable("id") Long creditCardId, Principal principal, Model model){
        User user = userService.findByUsername(principal.getName());
        UserPayment userPayment = userPaymentService.findById(creditCardId);

        if(user.getId() != userPayment.getUser().getId()){
            return "badRequestPage";
        }
        else{
            model.addAttribute("user", user);
            userPaymentService.removeById(creditCardId);

            model.addAttribute("listOfCreditCards", true);
            model.addAttribute("classActiveBilling", true);
            model.addAttribute("listOfShippingAddresses", true);

            model.addAttribute("userPaymentList", user.getUserPaymentList());
            model.addAttribute("userShippingList", user.getUserShippingList());
            model.addAttribute("orderList", user.getOrderList());

            return "myProfile";
        }
    }

    @PostMapping("/setDefaultPayment")
    public String setDefaultPayment(@ModelAttribute("defaultUserPaymentId") Long creditCardId, Principal principal, Model model){
        User user = userService.findByUsername(principal.getName());
        userService.setDefaultPayment(creditCardId, user);

        model.addAttribute("user", user);
        model.addAttribute("listOfCreditCards", true);
        model.addAttribute("classActiveBilling", true);
        model.addAttribute("listOfShippingAddresses", true);

        model.addAttribute("userPaymentList", user.getUserPaymentList());
        model.addAttribute("userShippingList", user.getUserShippingList());
        model.addAttribute("orderList", user.getOrderList());

        return "myProfile";

    }

    @GetMapping("/orderDetail/{id}")
    public String orderDetail(@PathVariable("id")Long orderId, Principal principal,Model model){

        User user = userService.findByUsername(principal.getName());

        Order order = orderService.findOneOrder(orderId);

        if(order.getUser().getId() != user.getId()){
            return "badRequestPage";
        }
        else{
            List<CartItem> cartItemList = cartItemService.findByOrder(order);
            model.addAttribute("cartItemList", cartItemList);
            model.addAttribute("user", user);
            model.addAttribute("order", order);
            model.addAttribute("userPaymentList", user.getUserPaymentList());
            model.addAttribute("userShippingList", user.getUserShippingList());
            model.addAttribute("orderList", user.getOrderList());


            UserShipping userShipping = new UserShipping();
            model.addAttribute("userShipping", userShipping);

            List<String> stateList = USConstants.listOfUSStatesCode;
            Collections.sort(stateList);
            model.addAttribute("stateList", stateList);

            model.addAttribute("listOfShippingAddresses", true);
            model.addAttribute("classActiveOrders", true);
            model.addAttribute("listOfCreditCards", true);
            model.addAttribute("displayOrderDetail", true);



            return "myProfile";
        }

    }



}
