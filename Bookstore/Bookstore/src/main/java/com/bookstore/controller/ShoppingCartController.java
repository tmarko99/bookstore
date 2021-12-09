package com.bookstore.controller;

import com.bookstore.entity.Book;
import com.bookstore.entity.CartItem;
import com.bookstore.entity.ShoppingCart;
import com.bookstore.entity.User;
import com.bookstore.service.BookService;
import com.bookstore.service.CartItemService;
import com.bookstore.service.ShoppingCartService;
import com.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private UserService userService;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private BookService bookService;


    @RequestMapping("/cart")
    public String shoppingCart(Model model, Principal principal){
        User user = userService.findByUsername(principal.getName());
        ShoppingCart shoppingCart = user.getShoppingCart();

        List<CartItem> cartItemList = cartItemService.findByShoppingCart(shoppingCart);

        shoppingCartService.updateShoppingCart(shoppingCart);

        model.addAttribute("cartItemList", cartItemList);
        model.addAttribute("shoppingCart", shoppingCart);

        return "shoppingCart";
    }

    @RequestMapping("/addItem")
    public String addItem(@ModelAttribute("book")Book book,
                          @ModelAttribute("qty")String qty,
                          Model model, Principal principal){
        User user = userService.findByUsername(principal.getName());
        book = bookService.findOne(book.getId());

        if(Integer.parseInt(qty) > book.getInStockNumber()){
            model.addAttribute("notEnoughStock", true);

            return "forward:/bookDetail/"+book.getId();
        }
        cartItemService.addBookToCartItem(book, user, Integer.parseInt(qty));

        model.addAttribute("addBookSuccess", true);

        return "forward:/bookDetail/"+book.getId();
    }

    @RequestMapping("/updateCartItem")
    public String updateShoppingCart(@ModelAttribute("id")Long cartItemId,
                                     @ModelAttribute("qty")int qty){

        CartItem cartItem = cartItemService.findById(cartItemId);
        cartItem.setQty(qty);
        cartItemService.updateCartItem(cartItem);

        return "forward:/shoppingCart/cart";
    }

    @RequestMapping("/removeItem/{id}")
    public String removeItem(@PathVariable("id")Long id){
        cartItemService.removeCartItem(cartItemService.findById(id));

        return "forward:/shoppingCart/cart";
    }
}
