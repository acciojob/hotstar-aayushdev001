package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto)
    {
        //Save The subscription Object into the Db and return the total Amount that user has to pay
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        int amt = 0;
        if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.BASIC))
            amt = 500 + 200*subscriptionEntryDto.getNoOfScreensRequired();
        else if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.PRO))
            amt = 800 + 250*subscriptionEntryDto.getNoOfScreensRequired();
        else if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.ELITE))
            amt = 1000 + 350*subscriptionEntryDto.getNoOfScreensRequired();

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setTotalAmountPaid(amt);

        user.setSubscription(subscription);
        userRepository.save(user);

        return amt;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user = userRepository.findById(userId).get();
        if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.ELITE))
            throw new Exception("Already the best Subscription");
        else if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.PRO))
        {
            int amt = 1000 + 350*user.getSubscription().getNoOfScreensSubscribed();
            int diff = amt - user.getSubscription().getTotalAmountPaid();
            user.getSubscription().setTotalAmountPaid(amt);

            userRepository.save(user);
            return diff;
        }
        else if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.BASIC))
        {
            int amt = 800 + 250*user.getSubscription().getNoOfScreensSubscribed();
            int diff = amt - user.getSubscription().getTotalAmountPaid();
            user.getSubscription().setTotalAmountPaid(amt);

            userRepository.save(user);
            return diff;
        }

        return null;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> list = subscriptionRepository.findAll();
        int revenue = 0;

        for(Subscription s : list)
        {
            revenue += s.getTotalAmountPaid();
        }
        return revenue;
    }

}
