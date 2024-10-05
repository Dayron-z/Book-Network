package com.pageconnect.booknetwork.auth;


import com.pageconnect.booknetwork.email.EmailService;
import com.pageconnect.booknetwork.email.EmailTemplateName;
import com.pageconnect.booknetwork.role.RoleRepository;
import com.pageconnect.booknetwork.user.Token;
import com.pageconnect.booknetwork.user.TokenRepository;
import com.pageconnect.booknetwork.user.User;
import com.pageconnect.booknetwork.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.authentication.PasswordEncoderParser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationURL;
    public void register(RegistrationRequest request) throws MessagingException {
        var userRole = roleRepository.findByName("USER").orElseThrow( () ->  new IllegalStateException("Role user was not found"));

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();


        userRepository.save(user);
        sendValidationEmail(user); 
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken  = generateAndSaveActivationToken(user);

        emailService.sendEmail(
                user.getEmail(),
                user.fullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationURL,
                newToken,
                "Account acctivation"
        );
        
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .user(user)
                .build();

        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int length) {
            String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder(); // Used to avoid creating multiple instances as with the String class.
        SecureRandom secureRandom = new SecureRandom();

        for (int i = 0; i < length ; i++) {
            int randomIndex = secureRandom.nextInt(characters.length()); //Between 0 - 9
            codeBuilder.append(characters.charAt(randomIndex)); 
        }

        return codeBuilder.toString();
    }


}                               
