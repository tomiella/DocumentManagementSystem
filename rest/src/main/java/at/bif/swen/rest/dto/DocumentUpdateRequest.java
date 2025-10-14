package at.bif.swen.rest.dto;

import jakarta.validation.constraints.*;

import java.util.Set;

public record DocumentUpdateRequest(
        @Size(max = 300) String title,
        @Size(max = 10000) String summary,
        Set<@NotBlank @Size(max = 100) String> tags
) {}
