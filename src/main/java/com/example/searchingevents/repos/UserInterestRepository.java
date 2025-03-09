package com.example.searchingevents.repos;

import com.example.searchingevents.models.UserInterest;
import com.example.searchingevents.models.Users;
import com.example.searchingevents.models.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
    UserInterest findByUserAndCityAndEventType(Users user, String city, EventType eventType);
    List<UserInterest> findAllByUserOrderByFrequencyDesc(@Param("user") Users user);
}
