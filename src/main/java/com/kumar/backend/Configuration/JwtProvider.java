package com.kumar.backend.Configuration;

import com.kumar.backend.Utils.Constant.JwtConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


public class JwtProvider {


    private static SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());
    }

    public static String generateToken(Authentication authentication){
        Collection<? extends GrantedAuthority> authorities=authentication.getAuthorities();
        String roles=populateAuthorities(authorities);
        SecretKey key=getSecretKey();
        String jwt=Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+1000*60*60*24))
                .claim("email",authentication.getName())
                .claim("authorities",roles)
                .signWith(key)
                .compact();
        return jwt;
    }

    public static String getEmailFromToken(String token){
        token=token.substring(7);
        SecretKey key=getSecretKey();
        Claims claims= Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String email=String.valueOf(claims.get("email"));

        return email;
    }

    private static String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<String> auth=new HashSet<String>();

        for(GrantedAuthority authority:authorities){
            auth.add(authority.getAuthority());
        }
        return String.join(",", auth);
    }

}
