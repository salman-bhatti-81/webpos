package com.wepos.authentication.dto;

import lombok.Data;

@Data
public class GenerateTokenResponse {
    private String token;
    private String fullName;
    private String organization;
    private String expiryDate;
}
