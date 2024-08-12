package com.example.demo.profile;

import com.example.demo.appuser.AppUser;
import com.example.demo.appuser.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@AllArgsConstructor
public class UserProfileController {

    private final AppUserService appUserService;

    @GetMapping
    public ResponseEntity<AppUser> getUserProfile(Authentication authentication) {
        AppUser user = appUserService.getUserProfile(authentication.getName());
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity<String> updateUserProfile(
            Authentication authentication,
            @RequestBody UpdateProfileRequest request) {
        boolean updated = appUserService.updateUserProfile(
                authentication.getName(),
                request.getFirstName(),
                request.getLastName(),
                request.getProfilePictureUrl()
        );
        return updated
                ? ResponseEntity.ok("Profile updated successfully")
                : ResponseEntity.badRequest().body("Failed to update profile");
    }

    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(
            Authentication authentication,
            @RequestBody UpdatePasswordRequest request) {
        boolean updated = appUserService.updateUserPassword(
                authentication.getName(),
                request.getNewPassword()
        );
        return updated
                ? ResponseEntity.ok("Password updated successfully")
                : ResponseEntity.badRequest().body("Failed to update password");
    }
}