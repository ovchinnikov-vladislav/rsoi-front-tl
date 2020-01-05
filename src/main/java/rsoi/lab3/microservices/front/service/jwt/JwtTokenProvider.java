package rsoi.lab3.microservices.front.service.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import rsoi.lab3.microservices.front.client.GatewayClient;
import rsoi.lab3.microservices.front.client.SessionClient;
import rsoi.lab3.microservices.front.entity.user.Role;
import rsoi.lab3.microservices.front.entity.user.User;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.*;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private SecretKey secretKey;

    @Autowired
    private GatewayClient gatewayClient;
    @Autowired
    private SessionClient sessionClient;

    @PostConstruct
    public void init() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
            char[] keyStorePassword = "314adfas6732fdagd9113A4".toCharArray();
            char[] keyPassword = "463129fda4843H21fdaf".toCharArray();
            KeyStore.ProtectionParameter entryPassword = new KeyStore.PasswordProtection(keyPassword);
            try (InputStream keyStoreData = new FileInputStream("data" + File.separator + "keystore.ks")) {
                keyStore.load(keyStoreData, keyStorePassword);
                KeyStore.SecretKeyEntry secretKeyEntry =
                        (KeyStore.SecretKeyEntry) keyStore.getEntry("secretAlias", entryPassword);
                secretKey = secretKeyEntry.getSecretKey();
            } catch (Exception exc) {
                secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
                KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(secretKey);
                keyStore.setEntry("secretAlias", secretKeyEntry, entryPassword);
                try (FileOutputStream keyStoreOutputStream = new FileOutputStream("data" + File.separator + "keystore.ks")) {
                    keyStore.store(keyStoreOutputStream, keyStorePassword);
                }
            }
        } catch (Exception exc) {
            throw new RuntimeException(exc.getMessage());
        }
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public String resolveToken(Cookie cookie) {
        return cookie.getValue();
    }

    public Map<String, String> generationToken(HashMap<String, String> map) {
        return sessionClient.getToken(map);
    }

    public UUID getUserIdFromToken(String token) {
        Claims claims = getJwsClaimsFromToken(token).getBody();
        return UUID.fromString(claims.get("user_id", String.class));
    }

    public String getUsernameFromToken(String token) {
        Claims claims = getJwsClaimsFromToken(token).getBody();
        return claims.get("username", String.class);
    }

    public User getUserByToken(String token) {
        User user = new User();
        Claims claims = getJwsClaimsFromToken(token).getBody();
        user.setId(UUID.fromString(claims.get("user_id", String.class)));
        user.setUsername(claims.get("username", String.class));
        user.setRoles((List<Role>) claims.get("roles"));
        return user;
    }

    public boolean validateAccessToken(String token) {
        try {
            Jws<Claims> claims = getJwsClaimsFromToken(token);
            Date date = claims.getBody().getExpiration();
            if (date.before(new Date())) {
                logger.warn("JWT Token is expired.");
                return false;
            }
            return true;
        } catch (JwtException | IllegalArgumentException exc) {
            if (exc instanceof ExpiredJwtException) {
                logger.warn("JWT Token is expired.");
            } else {
                logger.warn("JWT Token is invalid.");
            }
            return false;
        }
    }

    private Jws<Claims> getJwsClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secretKey.getEncoded()).parseClaimsJws(token);
    }

}
