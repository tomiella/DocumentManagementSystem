package at.bif.swen.rest.dto;

import jakarta.validation.constraints.*;

import java.util.Set;

public record DocumentCreateRequest(
        @NotBlank @Size(max = 300) String title,
        @NotBlank @Size(max = 255) String filename,
        @NotBlank @Size(max = 150) String contentType,
        @Positive long size,
        @Size(max = 10000) String summary
) {}
