package com.bookstore.entity;

import javax.persistence.*;

@Entity
public class BillingAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String billingAddressName;
    private String billingAddressStreet1;
    private String billingAddressStreet2;
    private String billingAddressCity;
    private String billingAddressState;
    private String billingAddressCountry;
    private String billingAddressZipCode;

    @OneToOne(cascade = CascadeType.ALL)
    private Order order;


    public BillingAddress() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBillingAddressName() {
        return billingAddressName;
    }

    public void setBillingAddressName(String userBillingName) {
        this.billingAddressName = userBillingName;
    }

    public String getBillingAddressStreet1() {
        return billingAddressStreet1;
    }

    public void setBillingAddressStreet1(String userBillingStreet1) {
        this.billingAddressStreet1 = userBillingStreet1;
    }

    public String getBillingAddressStreet2() {
        return billingAddressStreet2;
    }

    public void setBillingAddressStreet2(String userBillingStreet2) {
        this.billingAddressStreet2 = userBillingStreet2;
    }

    public String getBillingAddressCity() {
        return billingAddressCity;
    }

    public void setBillingAddressCity(String userBillingCity) {
        this.billingAddressCity = userBillingCity;
    }

    public String getBillingAddressCountry() {
        return billingAddressCountry;
    }

    public void setBillingAddressCountry(String userBillingCountry) {
        this.billingAddressCountry = userBillingCountry;
    }

    public String getBillingAddressZipCode() {
        return billingAddressZipCode;
    }

    public void setBillingAddressZipCode(String userBillingZipCode) {
        this.billingAddressZipCode = userBillingZipCode;
    }

    public String getBillingAddressState() {
        return billingAddressState;
    }

    public void setBillingAddressState(String userBillingState) {
        this.billingAddressState = userBillingState;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
