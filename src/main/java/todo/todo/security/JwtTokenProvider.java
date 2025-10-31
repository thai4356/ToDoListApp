package todo.todo.security;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import todo.todo.exceptions.BusinessException;
import todo.todo.util.Util;

@Component
@Log4j2
public class JwtTokenProvider {

    public String generateTokenHS512(final String subId, final String jwtSecret, final long expirationInMs) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationInMs);
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject(subId)
                .issuedAt(new Date())
                .expiration(expiryDate)
                .signWith(key, Jwts.SIG.HS512).compact();
    }

    public String getSubIdFromJwtHS512(final String token, final String jwtSecret) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

        return claims.getSubject();
    }

    public boolean validateTokenHS512(final String authToken, final String jwtSecret) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            String sub = Jwts.parser().verifyWith(key).build().parseSignedClaims(authToken).getPayload().getSubject();
            if (StringUtils.isBlank(sub)) {
                throw new Exception(authToken);
            }
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token. " + ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token. " + ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token. " + ex.getMessage());
        } catch (Exception ex) {
            log.error("JWT claims string is empty. " + ex.getMessage());
        }
        return false;
    }

    public String generateTokenRs256(final String subId, final long expirationInMs) {
        try {
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + expirationInMs);

            return Jwts.builder()
                    .subject(subId)
                    .issuedAt(new Date())
                    .expiration(expiryDate)
                    .signWith(generateJwtKeyEncryption(), Jwts.SIG.RS256)
                    .compact();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    public String getSubIdFromJwtRs256(final String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(generateJwtKeyDecryption())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException e) {
            throw new BusinessException(e.getMessage());
        }
    }

    public boolean validateTokenRs256(final String authToken) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(generateJwtKeyDecryption())
                    .build()
                    .parseSignedClaims(authToken)
                    .getPayload();
            String sub = claims.getSubject();
            if (StringUtils.isBlank(sub)) {
                throw new Exception(authToken);
            }
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token. " + ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token. " + ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token. " + ex.getMessage());
        } catch (Exception ex) {
            log.error("JWT claims string is empty. " + ex.getMessage());
        }
        return false;
    }


    private PublicKey generateJwtKeyDecryption() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String PUBLIC_KEY_PATH = "oauth-key/oauth-public.key";
        try (Reader reader = new FileReader(PUBLIC_KEY_PATH); PEMParser pemParser = new PEMParser(reader)) {
            Object object = pemParser.readObject();
            Security.addProvider(new BouncyCastleProvider());
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
            if (object instanceof PEMKeyPair keyPair) {
                return converter.getPublicKey(keyPair.getPublicKeyInfo());
            }
            if (object instanceof SubjectPublicKeyInfo publicKeyInfo) {
                return converter.getPublicKey(publicKeyInfo);
            } else {
                throw new InvalidKeySpecException("Not a valid RSA public key.");
            }
        }
    }

    private PrivateKey generateJwtKeyEncryption() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String PRIVATE_KEY_PATH = "oauth-key/oauth-private.key";
        try (Reader reader = new FileReader(PRIVATE_KEY_PATH); PEMParser pemParser = new PEMParser(reader)) {
            Object object = pemParser.readObject();
            Security.addProvider(new BouncyCastleProvider());
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
            if (object instanceof PEMKeyPair keyPair) {
                return converter.getPrivateKey(keyPair.getPrivateKeyInfo());
            }
            if (object instanceof PrivateKeyInfo privateKeyInfo) {
                return converter.getPrivateKey(privateKeyInfo);
            } else {
                throw new InvalidKeySpecException("Not a valid RSA private key.");
            }
        }
    }

    public Map<String, Object> getPayload(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                System.out.println("Invalid JWT token format.");
                return new HashMap<>();
            }
            String payloadBase64 = parts[1];
            byte[] decodedBytes = Base64.getUrlDecoder().decode(payloadBase64);
            String decodedPayload = new String(decodedBytes);
            return Util.stringToObject(Map.class, decodedPayload);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

}