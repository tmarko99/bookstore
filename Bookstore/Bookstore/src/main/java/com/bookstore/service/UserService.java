package com.bookstore.service;

import com.bookstore.entity.*;
import com.bookstore.entity.security.UserRole;
import com.bookstore.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserPaymentRepository userPaymentRepository;

    @Autowired
    private UserShippingRepository userShippingRepository;

    public User findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User createUser(User user, Set<UserRole> userRoles) throws Exception {
        User byUsername = userRepository.findByUsername(user.getUsername());

        if(byUsername != null){
            //throw new Exception("User already exists. Nothing will be done");
        }
        else{
            for(UserRole ur : userRoles){
                roleRepository.save(ur.getRole());
            }
            user.getUserRoles().addAll(userRoles);

            user.setFirstName(user.getFirstName());
            user.setLastName(user.getLastName());
            user.setEmail(user.getEmail());
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUser(user);
            user.setShoppingCart(shoppingCart);

            user.setUserShippingList(new ArrayList<UserShipping>());
            user.setUserPaymentList(new ArrayList<UserPayment>());

            byUsername = userRepository.save(user);
        }
        return byUsername;
    }

    public User save(User user){
        return userRepository.save(user);
    }

    public void updateUserBilling(UserBilling userBilling, UserPayment userPayment, User user){
        userPayment.setUser(user);
        userPayment.setUserBilling(userBilling);
        userPayment.setDefaultPayment(true);
        userBilling.setUserPayment(userPayment);
        user.getUserPaymentList().add(userPayment);

        save(user);
    }

    public void setDefaultPayment(Long userPaymentId, User user){
        List<UserPayment> userPayments = userPaymentRepository.findAll();

        for(UserPayment userPayment : userPayments){
            if(userPayment.getId() == userPaymentId){
                userPayment.setDefaultPayment(true);
                userPaymentRepository.save(userPayment);
            }
            else{
                userPayment.setDefaultPayment(false);
                userPaymentRepository.save(userPayment);
            }
        }
}

    public void updateUserShipping(UserShipping userShipping, User user) {
        userShipping.setUser(user);
        userShipping.setUserShippingDefault(true);
        user.getUserShippingList().add(userShipping);

        save(user);
    }

    public void setDefaulShipping(Long shippingId, User user) {
        List<UserShipping> userShippings = userShippingRepository.findAll();

        for(UserShipping userShipping : userShippings){
            if(userShipping.getId() == shippingId){
                userShipping.setUserShippingDefault(true);
                userShippingRepository.save(userShipping);
            }
            else{
                userShipping.setUserShippingDefault(false);
                userShippingRepository.save(userShipping);
            }
        }
    }

    public User findById(Long id){
        return userRepository.findById(id).orElseThrow();
    }
}
