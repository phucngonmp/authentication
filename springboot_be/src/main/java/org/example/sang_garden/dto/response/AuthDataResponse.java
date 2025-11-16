package org.example.sang_garden.dto.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.example.sang_garden.util.UserRole;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthDataResponse(String accessToken, @JsonProperty("role") UserRole userRole) {
}
