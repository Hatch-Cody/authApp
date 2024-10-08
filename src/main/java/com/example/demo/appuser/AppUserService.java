package com.example.demo.appuser;

import com.example.demo.registration.token.ConfirmationToken;
import com.example.demo.registration.token.ConfirmationTokenService;
import com.example.demo.registration.token.PasswordResetToken;
import com.example.demo.registration.token.PasswordResetTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG =
            "user with email %s not found";

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final PasswordResetTokenService passwordResetTokenService;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                String.format(USER_NOT_FOUND_MSG, email)));
    }

    public String signUpUser(AppUser appUser) {
        boolean userExists = appUserRepository
                .findByEmail(appUser.getEmail())
                .isPresent();

        if (userExists) {
            // TODO check if attributes are the same and if email not confirmed send confirmation email.

            throw new IllegalStateException("email already taken");
        }

        String encodedPassword = bCryptPasswordEncoder
                .encode(appUser.getPassword());

        appUser.setPassword(encodedPassword);

        appUserRepository.save(appUser);

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );

        confirmationTokenService.saveConfirmationToken(
                confirmationToken);

//        TODO: SEND EMAIL

        return token;
    }

    public int enableAppUser(String email) {
        return appUserRepository.enableAppUser(email);
    }

    public boolean updateUserProfile(String email, String firstName, String lastName, String profilePictureUrl) {
        int result = appUserRepository.updateUserProfile(email, firstName, lastName, profilePictureUrl);
        return result > 0;
    }

    public boolean updateUserPassword(String email, String newPassword) {
        String encodedPassword = bCryptPasswordEncoder.encode(newPassword);
        int result = appUserRepository.updateUserPassword(email, encodedPassword);
        return result > 0;
    }

    public AppUser getUserProfile(String email) {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email)));
    }

    public String createPasswordResetTokenForUser(AppUser user) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );
        passwordResetTokenService.savePasswordResetToken(passwordResetToken);
        return token;
    }

    public String resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenService.getToken(token);
        if (passwordResetToken.isPresent() && passwordResetToken.get().getConfirmedAt() == null) {
            LocalDateTime expiredAt = passwordResetToken.get().getExpiresAt();
            if (expiredAt.isBefore(LocalDateTime.now())) {
                return "Token has expired";
            }
            AppUser user = passwordResetToken.get().getAppUser();
            String encodedPassword = bCryptPasswordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
            appUserRepository.save(user);
            passwordResetTokenService.setConfirmedAt(token);
            return "Password reset successfully";
        }
        return "Invalid token";
    }
}