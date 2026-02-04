package com.money_manager.service;

import com.money_manager.entity.ProfileEntity;
import com.money_manager.repository.ProfileRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final ProfileRepository profileRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       ProfileEntity exitingProfile= profileRepository.findByEmail(email).orElseThrow(()->
            new UsernameNotFoundException("Profile not found with this email "+ email));
       return User.builder()
               .username(exitingProfile.getEmail())
               .password(exitingProfile.getPassword())
               .authorities(Collections.emptyList()).build();
    }
}
