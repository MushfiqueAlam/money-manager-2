package com.money_manager.service;

import com.money_manager.dto.AuthDto;
import com.money_manager.dto.ProfileDto;
import com.money_manager.entity.ProfileEntity;
import com.money_manager.repository.ProfileRepository;
import com.money_manager.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;

    @Value("${MONEY_MANAGER_BACKEND_URL:http://localhost:8080}")
    private String activationUrl;

    public ProfileDto registerProfile(ProfileDto profileDto){
        ProfileEntity newProfile=toEntity(profileDto);
        newProfile.setActivationToken(UUID.randomUUID().toString());
        newProfile=profileRepository.save(newProfile);

//        Sent Activation email
        String activationLink=activationUrl+"/api/v1.0/activate?token="+newProfile.getActivationToken();
        String subject="Activate your money Manager Account";
        String emailBody="Click on the activation link and activate your account: "+activationLink;
        emailService.sendMail(newProfile.getEmail(),subject,emailBody);

        return toDto(newProfile);

    }

    public ProfileEntity toEntity(ProfileDto profileDto){
        return ProfileEntity.builder()
                .id(profileDto.getId())
                .fullName(profileDto.getFullName())
                .email(profileDto.getEmail())
                .password(passwordEncoder.encode(profileDto.getPassword()))
                .profileImageUrl(profileDto.getProfileImageUrl())
                .createdAt(profileDto.getCreatedAt())
                .updatedAt(profileDto.getUpdatedAt())
                .build();
    }

    public ProfileDto toDto(ProfileEntity profileEntity){
        return ProfileDto.builder()
                .id(profileEntity.getId())
                .fullName(profileEntity.getFullName())
                .email(profileEntity.getEmail())
                .password(profileEntity.getPassword())
                .profileImageUrl(profileEntity.getProfileImageUrl())
                .createdAt(profileEntity.getCreatedAt())
                .updatedAt(profileEntity.getUpdatedAt())
                .build();
    }

    //activate profile
    public boolean activateProfile(String activationToken){
        return profileRepository.findByActivationToken(activationToken)
                .map(profile  -> {
                    profile.setIsActive(true);
                    profileRepository.save(profile);
                    return true;
                }).orElse(false);
    }

    public boolean isAccountActive(String email){
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }

    public ProfileEntity getCurrentProfile(){
       Authentication authentication= SecurityContextHolder.getContext().getAuthentication();

      return profileRepository.findByEmail(authentication.getName())
               .orElseThrow(()->new UsernameNotFoundException("profile is not found with email "+authentication.getName()));
    }

    public ProfileDto getPublicProfile(String email){

        ProfileEntity currentUser=null;
        if(email==null){
           currentUser= getCurrentProfile();
        }else {
          currentUser=  profileRepository.findByEmail(email)
                  .orElseThrow(()-> new UsernameNotFoundException("Profile not found with email "+email));
        }
        return ProfileDto.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .profileImageUrl(currentUser.getProfileImageUrl())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }

    public Map<String, Object> authenticateAndGenerateToken(AuthDto authDto) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDto.getEmail(),authDto.getPassword()));
            String token=jwtUtil.generateToken(authDto.getEmail());
            return Map.of("token",token,"user",getPublicProfile(authDto.getEmail()));
        }catch (Exception e){
            throw new RuntimeException("Invalid Email or Password");
        }
    }
}
