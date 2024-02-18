package com.wepos.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenerateTokenRequest {
    private String username;
    private String password;
}
