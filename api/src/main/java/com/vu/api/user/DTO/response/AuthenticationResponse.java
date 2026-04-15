package com.vu.api.user.DTO.response;

public record AuthenticationResponse( boolean authenticated, String token ) {
}
