package com.pageconnect.booknetwork.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter /*It is executed once for each HTTP request.*/ {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; /*Provides a method to load user information according to your username*/



    @Override
    protected void doFilterInternal(
           @NonNull HttpServletRequest request,
           @NonNull HttpServletResponse response,
           @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("Request URL: " + request.getRequestURI()); // Add a log

        if (request.getServletPath().startsWith("/api/v1/auth")){  /*---> Authentication route*/
            System.out.println("Allowing access to auth route");
            System.out.println("Req"+ request);
            System.out.println("Req servletPath"+ request.getServletPath());
            System.out.println("Resp" + response);
            filterChain.doFilter(request, response);
            return;
        }


        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwt;
        final String userEmail;


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("llego1");
            filterChain.doFilter(request, response);
            return;
        }

        System.out.println("llego2");
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);



        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
            System.out.println("llego3");
            if (jwtService.isTokenValid(jwt, userDetails)){
                System.out.println("llego4");
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        System.out.println("llego5");

        filterChain.doFilter(request, response);
    }
}
