package com.hastesoft.ghrepo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponseDto {
    
    private int status;
    private String message;
}
