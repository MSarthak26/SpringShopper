package SpringBootEcom.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${spring.app.jwtExpirationMs}")
    private Long jwtExpirationMs;     //tokenExpiration time in milliseconds

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;

    //getting JWT from header
    public String getJwtFromHeader(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7); //Removes "Bearer " prefix from the token
        }
        return null;
    }

    //generating token from Username
    public String generateTokenFromUsername(UserDetails userDetails){
        String username = userDetails.getUsername();
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
