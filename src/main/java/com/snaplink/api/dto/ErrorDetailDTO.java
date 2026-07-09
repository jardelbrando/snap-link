package com.snaplink.api.dto;

public record ErrorDetailDTO(
        String field,
        String message
) {}