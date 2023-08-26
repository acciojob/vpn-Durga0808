package com.driver.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String UserName;
    private String PassWord;
    private String originalIp;
    private String maskedIp;
    private Boolean isConnected;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Connection>connectionsList;

    @ManyToMany
    @JoinColumn
    private List<ServiceProvider>serviceProviderList;

    @OneToOne(mappedBy = "user",cascade = CascadeType.ALL)
    private Country country;

    public User() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassWord() {
        return PassWord;
    }

    public void setPassWord(String passWord) {
        PassWord = passWord;
    }

    public String getOriginalIp() {
        return originalIp;
    }

    public void setOriginalIp(String originalIp) {
        this.originalIp = originalIp;
    }

    public String getMaskedIp() {
        return maskedIp;
    }

    public void setMaskedIp(String maskedIp) {
        this.maskedIp = maskedIp;
    }

    public Boolean getConnected() {
        return isConnected;
    }

    public void setConnected(Boolean connected) {
        isConnected = connected;
    }

    public List<Connection> getConnectionsList() {
        return connectionsList;
    }

    public void setConnectionsList(List<Connection> connectionsList) {
        this.connectionsList = connectionsList;
    }

    public List<ServiceProvider> getServiceProviderList() {
        return serviceProviderList;
    }

    public void setServiceProviderList(List<ServiceProvider> serviceProviderList) {
        this.serviceProviderList = serviceProviderList;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
