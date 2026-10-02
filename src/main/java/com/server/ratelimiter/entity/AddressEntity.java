package com.server.ratelimiter.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "address")
public class AddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "address_type")
    private String addressType;

    @Column(name = "city")
    private String city;

    @Column(name = "street")
    private String street;

    @Column(name = "is_default")
    private boolean isDefault;

    public AddressEntity(){

    }

    public AddressEntity(String addressType, String city, String street, boolean isDefault){
        this.addressType = addressType;
        this.city = city;
        this.street = street;
        this.isDefault = isDefault;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
