package com.myfintrack.myfintrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private String type = "Bearer";
    private Long userId;
    private String email;
    private String fullName;
}
