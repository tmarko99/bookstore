package com.bookstore.repository;

import com.bookstore.entity.BookToCartItem;
import com.bookstore.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface BookToCartItemRepository extends JpaRepository<BookToCartItem, Long> {
    void deleteByCartItem(CartItem cartItem);
}
