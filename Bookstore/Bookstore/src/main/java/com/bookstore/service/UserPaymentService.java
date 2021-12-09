package com.bookstore.service;

import com.bookstore.entity.UserPayment;
import com.bookstore.repository.UserPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserPaymentService {
    @Autowired
    UserPaymentRepository userPaymentRepository;

    public UserPayment findById(Long id){
        return userPaymentRepository.findById(id).orElseThrow(() -> new IllegalStateException("Not found"));
    }

    public void removeById(Long id){
        userPaymentRepository.deleteById(id);
    }
    
}
