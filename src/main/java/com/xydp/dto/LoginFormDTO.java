package com.xydp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginFormDTO {
    private String phone;
    private String code;
    private String password;
}
