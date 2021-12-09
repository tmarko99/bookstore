package com.bookstore.service;

import com.bookstore.entity.UserShipping;
import com.bookstore.repository.UserShippingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserShippingService {
    @Autowired
    private UserShippingRepository userShippingRepository;

    public UserShipping findById(Long id){
        return userShippingRepository.findById(id).orElseThrow();
    }

    public void removeById(Long userShippingId) {
        userShippingRepository.deleteById(userShippingId);
    }
}
