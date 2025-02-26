package com.example.searchingevents.controllers;

import com.example.searchingevents.models.dto.JwtAuthenticationResponse;
import com.example.searchingevents.models.dto.SignInRequest;
import com.example.searchingevents.models.dto.SignUpRequest;
import com.example.searchingevents.services.AuthenticationService;
import io.swagger.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "User registration")
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request) {
        return authenticationService.signUp(request);
    }

    @Operation(summary = "User auth")
    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) {
        return authenticationService.signIn(request);
    }
}
