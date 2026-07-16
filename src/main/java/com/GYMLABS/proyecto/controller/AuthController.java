package com.GYMLABS.proyecto.controller;

import com.GYMLABS.proyecto.dto.AuthRequest;
import com.GYMLABS.proyecto.dto.AuthResponse;
import com.GYMLABS.proyecto.security.CustomUserDetails;
import com.GYMLABS.proyecto.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(userDetails);

        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        Integer idEmpresa = null;
        String empresaNombre = null;
        
        if (userDetails.getPersonal().getSede() != null && userDetails.getPersonal().getSede().getEmpresa() != null) {
            idEmpresa = userDetails.getPersonal().getSede().getEmpresa().getIdEmpresa();
            empresaNombre = userDetails.getPersonal().getSede().getEmpresa().getNombre();
        }

        org.springframework.http.ResponseCookie jwtCookie = org.springframework.http.ResponseCookie.from("jwt", jwt)
                .httpOnly(true)
                .secure(true) // Debe ser true si SameSite=None
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("None") // Permite cross-domain cookies (Vercel <-> Render)
                .build();

        return ResponseEntity.ok()
                .header(org.springframework.http.HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new AuthResponse(
                        jwt, // Restauramos el JWT para que el frontend lo pueda usar como Bearer token
                        userDetails.getPersonal().getNombre(),
                        role,
                        idEmpresa,
                        empresaNombre
                ));
    }
}
