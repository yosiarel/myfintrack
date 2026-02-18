package com.myfintrack.myfintrack.service;

import com.myfintrack.myfintrack.entity.User;
import com.myfintrack.myfintrack.exception.BadRequestException;
import com.myfintrack.myfintrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

        private final UserRepository userRepository;

        public User getUserByEmail(String email) {
                return userRepository.findByEmail(email)
                                .orElseThrow(() -> new BadRequestException("User not found"));
        }
}
