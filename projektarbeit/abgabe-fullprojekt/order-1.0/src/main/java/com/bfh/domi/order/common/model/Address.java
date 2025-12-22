package com.bfh.domi.order.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Address {

    @Column(name = "ADDRESS_STREET")
    private String street;
    @Column(name = "ADDRESS_STATE_PROVINCE")
    private String stateProvince;
    @Column(name = "ADDRESS_POSTAL_CODE")
    private String postalCode;
    @Column(name = "ADDRESS_CITY")
    private String city;
    @Column(name = "ADDRESS_COUNTRY")
    private String country;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
