package com.money_manager.controller;

import ch.qos.logback.core.joran.util.StringToObjectConverter;
import com.money_manager.dto.AuthDto;
import com.money_manager.dto.ProfileDto;
import com.money_manager.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDto> registerProfile(@RequestBody ProfileDto profileDto){
        ProfileDto registerProfile=profileService.registerProfile(profileDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerProfile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token){
        boolean isActivated= profileService.activateProfile(token);
        if(isActivated){
            return ResponseEntity.ok("Profile Activated successfully");
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation token is not found or already used");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> login(@RequestBody AuthDto authDto){
        try{
            if(!profileService.isAccountActive(authDto.getEmail())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Account is not active so please activate account first"));
            }
            Map<String,Object> respone=profileService.authenticateAndGenerateToken(authDto);
            return ResponseEntity.ok(respone);
        } catch (Exception e) {
               return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message",e.getMessage()));
        }
    }

}
