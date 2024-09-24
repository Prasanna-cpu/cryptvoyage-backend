package com.kumar.backend.Configuration;

import com.kumar.backend.Utils.Constant.JwtConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;


import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JwtTokenValidator extends OncePerRequestFilter {

    @Value("${secret.key}")
    private String secretKey;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String jwt=request.getHeader(JwtConstant.JWT_HEADER);


        if(jwt!=null && jwt.isEmpty()){
            jwt=jwt.substring(7);

            try{
                SecretKey keys= Keys.hmacShaKeyFor(secretKey.getBytes());
                Claims claims= Jwts
                        .parserBuilder()
                        .setSigningKey(keys)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();

                String email=String.valueOf(claims.get("email"));
                String authorities=String.valueOf(claims.get("authorities"));
                List<GrantedAuthority> authorityList= AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
                Authentication authentication=new UsernamePasswordAuthenticationToken(
                        email,
                        authorityList
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);


            }
            catch(Exception e){
                throw new RuntimeException(e);
            }

        }

        filterChain.doFilter(request,response);

    }
}
