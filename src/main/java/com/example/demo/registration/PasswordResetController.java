package com.example.demo.registration;

import com.example.demo.appuser.AppUser;
import com.example.demo.appuser.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/password-reset")
@AllArgsConstructor
public class PasswordResetController {

    private final AppUserService appUserService;

    @PostMapping("/request")
    public ResponseEntity<String> requestPasswordReset(@RequestParam("email") String email) {
        AppUser user = (AppUser) appUserService.loadUserByUsername(email);
        if (user != null) {
            String token = appUserService.createPasswordResetTokenForUser(user);

            return ResponseEntity.ok("Password reset link sent to email");
        }
        return ResponseEntity.badRequest().body("User not found");
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestParam("token") String token,
                                                @RequestParam("password") String newPassword) {
        String result = appUserService.resetPassword(token, newPassword);
        if ("Password reset successfully".equals(result)) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.badRequest().body(result);
    }
}