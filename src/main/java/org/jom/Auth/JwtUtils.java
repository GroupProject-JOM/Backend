package org.jom.Auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.time.Instant;
import java.util.Date;


public class JwtUtils {
    private int user;
    private String page;
    private String authorizationHeader;
    private static final String SECRET_KEY = "shaka bum";
    private JSONObject payload;

    public JwtUtils() {
    }

    public JwtUtils(JSONObject payload) {
        this.payload = payload;
    }

    public JwtUtils(String authorizationHeader) {
        this.authorizationHeader = authorizationHeader;
    }

    public String generateJwt() {
        String base64UrlHeader = this.generateJwtHeader();
        String base64UrlPayload = this.generateJwtPayload();

        String base64UrlHeaderAndPayload = base64UrlHeader + "." + base64UrlPayload;
        String signature = generateJwtSignature(base64UrlHeaderAndPayload);

        String jwt = base64UrlHeaderAndPayload + "." + signature;
        System.out.println("JWT token is generated\n");
        System.out.println(jwt);

        return jwt;
    }

    private String generateJwtHeader() {
        // Creating the header
        JSONObject header = new JSONObject();
        header.put("alg", "HS256");
        header.put("typ", "JWT");  // abc2.s23d.isg3

        return Base64.getUrlEncoder().withoutPadding().encodeToString(header.toString().getBytes());
    }

    private String generateJwtPayload() {
        // Adding issued time (iat)
        Instant issuedAt = Instant.now();
        this.payload.put("iat", issuedAt.getEpochSecond());

        // Adding expiration time (exp), e.g., 1 hour from now
        Instant expirationTime = issuedAt.plus(1, ChronoUnit.HOURS);
        this.payload.put("exp", expirationTime.getEpochSecond());

        return Base64.getUrlEncoder().withoutPadding().encodeToString(this.payload.toString().getBytes());
    }

    private String generateJwtSignature(String base64UrlHeaderAndPayload) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");

            SecretKeySpec secret_key = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            return Base64.getUrlEncoder().withoutPadding().encodeToString(sha256_HMAC.doFinal(base64UrlHeaderAndPayload.getBytes()));
        } catch (Exception e) {
            System.out.println("Failed to calculate HMAC: " + e.getMessage());
            throw new RuntimeException();
        }
    }

    public boolean isJwtExpired() {
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
                .build()
                .parseClaimsJws(this.authorizationHeader);

        Date expiration = claimsJws.getBody().getExpiration();
        Date now = new Date();

        return expiration.before(now);
    }

    public boolean verifyJwtAuthentication() {
        if (isJwtExpired()) return false;

        String[] jwtSegments = getJwtSegmentsFromAuthHeader();

        if (jwtSegments != null && jwtSegments.length == 3) {
            String header = new String(Base64.getUrlDecoder().decode(jwtSegments[0]));
            String payload = new String(Base64.getUrlDecoder().decode(jwtSegments[1]));
            System.out.println(header);
            System.out.println(payload);

            String signature = jwtSegments[2];
            String base64UrlHeaderAndPayload = jwtSegments[0] + "." + jwtSegments[1];
            String calculatedSignature = generateJwtSignature(base64UrlHeaderAndPayload);

            if (!signature.equals(calculatedSignature)) {
                System.out.println("JWT signature verification failed as the signature is not matching");
                return false;
            }
        } else {
            System.out.println("Invalid authorization header");
            return false;
        }
        return true;
    }

    public JSONObject getAuthPayload() {
        String[] jwtSegments = getJwtSegmentsFromAuthHeader();

        return new JSONObject(new String(Base64.getUrlDecoder().decode(jwtSegments[1])));
    }


    private String[] getJwtSegmentsFromAuthHeader() {
        String[] jwtSegments = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwt = authorizationHeader.substring(7); // Extracts the jwt token removing the word Bearer prefix
            System.out.println(jwt);

            jwtSegments = jwt.split("\\.");
        }
        return jwtSegments;
    }
}
