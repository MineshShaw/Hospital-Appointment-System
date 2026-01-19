package com.example.hospitalAppointmentSystem.controller;

import com.example.hospitalAppointmentSystem.dto.UpdateUserProfileRequest;
import com.example.hospitalAppointmentSystem.dto.UserProfileResponse;
import com.example.hospitalAppointmentSystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile() {
        return ResponseEntity.ok(userService.getCurrentUserProfile());
    }

    @PutMapping("/me")
    public ResponseEntity<Void> updateProfile(
            @Valid @RequestBody UpdateUserProfileRequest request) {

        userService.updateCurrentUserProfile(request);
        return ResponseEntity.ok().build();
    }

}

