package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{
        //Connect the user to a vpn by considering the following priority order.




        //Else, establish the connection where the maskedIp is "updatedCountryCode.serviceProviderId.userId" and return the updated user.

        User user=userRepository2.findById(userId).get();
        if(user.getMaskedIp()!=null){//1. If the user is already connected to any service provider, throw "Already connected" exception.//
            throw new Exception("Already connected");

        }else if(countryName.equalsIgnoreCase(countryName)){//2. Else if the countryName corresponds to the original country of the user, do nothing.//
            // This means that the user wants to connect to its original country, for which we do not require a connection. Thus, return the user as it is.//
            return user;

        }else{//3. Else, the user should be subscribed under a serviceProvider having option to connect to the given country.

            if(user.getServiceProviderList()==null){//If the connection can not be made (As user does not have a serviceProvider or serviceProvider does not have given country,
                // throw "Unable to connect" exception.
                throw new Exception("Unable to connect");
            }

            List<ServiceProvider>serviceProviderList=user.getServiceProviderList();
            int id=Integer.MAX_VALUE;
            ServiceProvider serviceProvidercheck=null;
            Country country=null;

            for(ServiceProvider serviceProvider:serviceProviderList){
                List<Country>countryList=serviceProvider.getCountryList();
                for (Country c:countryList){
                    if(countryName.equalsIgnoreCase(c.getCountryName().toString())&&id>serviceProvider.getId()){  // If multiple service providers allow you to connect to the country,
                                                                                                                // use the service provider having smallest id.
                        id=serviceProvider.getId();
                        serviceProvidercheck=serviceProvider;
                        country=c;
                    }
                }
            }

            if(serviceProvidercheck!=null){
                Connection connection=new Connection();
                connection.setUser(user);
                connection.setServiceProvider(serviceProvidercheck);

                String cc=country.getCode();
                int givenid=serviceProvidercheck.getId();
                String maskid=cc+"."+givenid+"."+userId;

                user.setMaskedIp(maskid);
                user.setConnected(true);
                user.getConnectionsList().add(connection);
                serviceProvidercheck.getConnectionList().add(connection);
                userRepository2.save(user);
                serviceProviderRepository2.save(serviceProvidercheck);

            }
        }
        return user;

    }
    @Override
    public User disconnect(int userId) throws Exception {
        //If the given user was not connected to a vpn, throw "Already disconnected" exception.
        //Else, disconnect from vpn, make masked Ip as null, update relevant attributes and return updated user.
        User user=userRepository2.findById(userId).get();
        if(user.getConnected()==false){
            throw new Exception("Already disconnected");
        }
        user.setConnected(false);
        user.setMaskedIp(null);
        userRepository2.save(user);
        return user;

    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        //Establish a connection between sender and receiver users
        //To communicate to the receiver, sender should be in the current country of the receiver.
        //If the receiver is connected to a vpn, his current country is the one he is connected to.
        //If the receiver is not connected to vpn, his current country is his original country.
        //The sender is initially not connected to any vpn. If the sender's original country does not match receiver's current country,
        // we need to connect the sender to a suitable vpn. If there are multiple options, connect using the service provider having smallest id
        //If the sender's original country matches receiver's current country, we do not need to do anything as they can communicate. Return the sender as it is.
        //If communication can not be established due to any reason, throw "Cannot establish communication" exception

        User sender=userRepository2.findById(senderId).get();
        User reciver=userRepository2.findById(receiverId).get();

        if(reciver.getMaskedIp()!=null){
            String str=reciver.getMaskedIp();
            String cc=str.substring(0,3);

            if(cc.equalsIgnoreCase(sender.getCountry().getCode())){
                return sender;
            }else{
                String countryName="";
                if(cc.equalsIgnoreCase(CountryName.AUS.toCode())){
                    countryName=CountryName.AUS.toString();
                }
                if(cc.equalsIgnoreCase(CountryName.IND.toCode())){
                    countryName=CountryName.IND.toString();
                }
                if(cc.equalsIgnoreCase(CountryName.JPN.toCode())){
                    countryName=CountryName.JPN.toString();
                }
                if(cc.equalsIgnoreCase(CountryName.CHI.toCode())){
                    countryName=CountryName.CHI.toString();
                }
                if(cc.equalsIgnoreCase(CountryName.USA.toCode())){
                    countryName=CountryName.USA.toString();
                }
                User user=connect(senderId,countryName);
                if(!user.getConnected()){
                    throw new Exception("Cannot establish communication");
                }else{
                    return user;
                }
            }
        }else{
            if(reciver.getCountry().equals(sender.getCountry())){
                return sender;
            }

            String countryname=reciver.getCountry().getCountryName().toString();
            User user=connect(senderId,countryname);
            if(!user.getConnected()){
                throw new Exception("Cannot establish communication");
            }else{
                return user;
            }
        }

    }
}
