package com.example.hospitalAppointmentSystem.service;

import com.example.hospitalAppointmentSystem.dto.UpdateUserProfileRequest;
import com.example.hospitalAppointmentSystem.dto.UserProfileResponse;
import com.example.hospitalAppointmentSystem.model.User;
import com.example.hospitalAppointmentSystem.repository.UserRepository;
import com.example.hospitalAppointmentSystem.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileResponse getCurrentUserProfile() {

        User user = getCurrentUser();

        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getFullName(),
                user.getPhone()
        );
    }

    public void updateCurrentUserProfile(UpdateUserProfileRequest request) {

        User user = getCurrentUser();

        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());

        userRepository.save(user);
    }

    private User getCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException("Authenticated user not found"));
    }
}

