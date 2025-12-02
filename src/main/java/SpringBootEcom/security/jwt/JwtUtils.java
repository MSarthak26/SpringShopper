package SpringBootEcom.security.jwt;

import SpringBootEcom.security.services.UserDetailsImpl;
import SpringBootEcom.security.services.UserDetailsServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${spring.app.jwtExpirationMs}")
    private Long jwtExpirationMs;     //tokenExpiration time in milliseconds

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.app.jwtCookie}")
    private String jwtCookie;

    //getting JWT from header
//    public String getJwtFromHeader(HttpServletRequest request){
//        String bearerToken = request.getHeader("Authorization");
//        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
//            return bearerToken.substring(7); //Removes "Bearer " prefix from the token
//        }
//        return null;
//    }

    //getting cookie from request
    public String getCookieFromRequest(HttpServletRequest request){
        Cookie cookie = WebUtils.getCookie(request,jwtCookie);
        if (cookie!=null){
            return cookie.getValue();
        }
        return null;
    }


    //Generating a Response Cookie from username
    public ResponseCookie generateCookieFromUsername(UserDetailsImpl userPrincipal){
        String jwt = generateTokenFromUsername(userPrincipal.getUsername());
        ResponseCookie cookie = ResponseCookie.from(jwtCookie,jwt)
                .path("/api")
                .maxAge(24*60*60)
                .httpOnly(false)
                .build();
        return cookie;
    }

    //generating token from Username
    public String generateTokenFromUsername(String username){

        return  Jwts.builder().
                subject(username).
                issuedAt(new Date()).
                expiration(new Date(new Date().getTime() + jwtExpirationMs)).
                signWith(key()).
                compact();
    }

    //getting username from JWT token
    public String getUsernameFromToken(String token){
        return  Jwts.parser().
                verifyWith((SecretKey) key()).
                build().parseSignedClaims(token).
                getPayload().getSubject();
    }

    public ResponseCookie generateCleanCookie(){
        ResponseCookie cookie = ResponseCookie.from(jwtCookie,null)
                .path("/api")
                .build();
        return cookie;
    }

    //generating a signed key
    public Key key(){
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecret)
        );
    }

    //validating a JWT token
    public boolean validateToken(String authToken){
        try{
            Jwts.parser().
                    verifyWith((SecretKey) key()).
                    build().parseSignedClaims(authToken);

            return true;

        } catch (MalformedJwtException e) {
            System.out.println("Some exception occured");
        }
        return false;
    }


}
