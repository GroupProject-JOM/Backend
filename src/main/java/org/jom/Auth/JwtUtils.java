package org.jom.Auth;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.jom.Dao.UserDAO;
import org.jom.Model.EmployeeModel;
import org.jom.Model.SupplierModel;
import org.jom.Model.UserModel;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.time.Instant;
import java.util.Date;


public class JwtUtils {
    private String token;
    private static final String ACCESS_SECRET_KEY = System.getenv("ACCESS_TOKEN_SECRET");
    private static final String REFRESH_SECRET_KEY = System.getenv("REFRESH_TOKEN_SECRET");
    private JSONObject accessPayload;
    private JSONObject refreshPayload;

    public JwtUtils() {
    }

    public JwtUtils(JSONObject accessPayload) {
        this.accessPayload = accessPayload;
    }

    public JwtUtils(String token) {
        this.token = token;
    }

    public void setRefreshPayload(JSONObject refreshPayload) {
        this.refreshPayload = refreshPayload;
    }

    public String generateJwt() {
        String base64UrlHeader = this.generateJwtHeader();
        String base64UrlPayload = this.generateJwtPayload();

        String base64UrlHeaderAndPayload = base64UrlHeader + "." + base64UrlPayload;
        String signature = generateJwtSignature(base64UrlHeaderAndPayload);

        String jwt = base64UrlHeaderAndPayload + "." + signature;

        return jwt;
    }

    public String generateRefresh() {
        String base64UrlHeader = this.generateJwtHeader();
        String base64UrlPayload = this.generateRefreshPayload();

        String base64UrlHeaderAndPayload = base64UrlHeader + "." + base64UrlPayload;
        String signature = generateRefreshSignature(base64UrlHeaderAndPayload);

        String jwt = base64UrlHeaderAndPayload + "." + signature;

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
        this.accessPayload.put("iat", issuedAt.getEpochSecond());

        // Adding expiration time (exp), e.g., 24 hour from now
        Instant expirationTime = issuedAt.plus(24, ChronoUnit.HOURS);
        this.accessPayload.put("exp", expirationTime.getEpochSecond());

        return Base64.getUrlEncoder().withoutPadding().encodeToString(this.accessPayload.toString().getBytes());
    }

    private String generateRefreshPayload() {
        // Adding issued time (iat)
        Instant issuedAt = Instant.now();
        this.refreshPayload.put("iat", issuedAt.getEpochSecond());

        // Adding expiration time (exp), e.g., 6 months from now
        Instant expirationTime = issuedAt.plus(24 * 30 * 6, ChronoUnit.HOURS);
        this.refreshPayload.put("exp", expirationTime.getEpochSecond());

        return Base64.getUrlEncoder().withoutPadding().encodeToString(this.refreshPayload.toString().getBytes());
    }

    private String generateJwtSignature(String base64UrlHeaderAndPayload) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");

            SecretKeySpec secret_key = new SecretKeySpec(ACCESS_SECRET_KEY.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            return Base64.getUrlEncoder().withoutPadding().encodeToString(sha256_HMAC.doFinal(base64UrlHeaderAndPayload.getBytes()));
        } catch (Exception e) {
            System.out.println("Failed to calculate HMAC: " + e.getMessage());
            throw new RuntimeException();
        }
    }

    private String generateRefreshSignature(String base64UrlHeaderAndPayload) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");

            SecretKeySpec secret_key = new SecretKeySpec(REFRESH_SECRET_KEY.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            return Base64.getUrlEncoder().withoutPadding().encodeToString(sha256_HMAC.doFinal(base64UrlHeaderAndPayload.getBytes()));
        } catch (Exception e) {
            System.out.println("Failed to calculate HMAC: " + e.getMessage());
            throw new RuntimeException();
        }
    }

    public boolean isJwtExpired(String payload) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(payload).getAsJsonObject();
        String exp = jsonObject.get("exp").getAsString();

        // Convert to Instant
        Instant instant = Instant.ofEpochSecond(Long.parseLong(exp));

        Date expiration = Date.from(instant);
        Date now = new Date();

        if (expiration != null && expiration.before(now)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean verifyJwtAuthentication() {
        String[] jwtSegments = getJwtSegmentsFromCookie();

        if (jwtSegments != null && jwtSegments.length == 3) {
            String header = new String(Base64.getUrlDecoder().decode(jwtSegments[0]));
            String payload = new String(Base64.getUrlDecoder().decode(jwtSegments[1]));

            if (isJwtExpired(payload)) return false;

            String signature = jwtSegments[2];
            String base64UrlHeaderAndPayload = jwtSegments[0] + "." + jwtSegments[1];
            String calculatedSignature = generateJwtSignature(base64UrlHeaderAndPayload);

            if (!signature.equals(calculatedSignature)) {
                System.out.println("JWT signature verification failed as the signature is not matching");
                return false;
            }
            return true;
        } else {
            System.out.println("Invalid authorization header");
            return false;
        }
    }

    public boolean verifyRefreshAuthentication() {
        String[] jwtSegments = getJwtSegmentsFromCookie();

        if (jwtSegments != null && jwtSegments.length == 3) {
            String payload = new String(Base64.getUrlDecoder().decode(jwtSegments[1]));

            if (isJwtExpired(payload)) return false;

            String signature = jwtSegments[2];
            String base64UrlHeaderAndPayload = jwtSegments[0] + "." + jwtSegments[1];
            String calculatedSignature = generateRefreshSignature(base64UrlHeaderAndPayload);

            if (!signature.equals(calculatedSignature)) {
                System.out.println("Refresh signature verification failed as the signature is not matching");
                return false;
            }
            return true;
        } else {
            System.out.println("Invalid authorization header");
            return false;
        }
    }

    public JSONObject getAuthPayload() {
        String[] jwtSegments = getJwtSegmentsFromCookie();

        return new JSONObject(new String(Base64.getUrlDecoder().decode(jwtSegments[1])));
    }


    private String[] getJwtSegmentsFromCookie() {
        String[] jwtSegments = null;
        if (token != null)
            jwtSegments = token.split("\\.");

        return jwtSegments;
    }

    public boolean CheckJWT(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    this.token = cookie.getValue();
                    if (!this.verifyJwtAuthentication()) {
                        System.out.println("UnAuthorized1");
                        return false;
                    }
                    return true; // No need to continue checking if "jwt" cookie is found
                }
            }
        } else {
            System.out.println("No cookies found in the request.");
            return false;
        }

        System.out.println("UnAuthorized - JWT cookie not found");
        return false;
    }

    public boolean CheckRefresh(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refresh".equals(cookie.getName())) {
                    this.token = cookie.getValue();
                    if (!this.verifyRefreshAuthentication()) {
                        System.out.println("UnAuthorized1");
                        return false;
                    }
                    return true;
                }
            }
        } else {
            System.out.println("No cookies found in the request.");
            return false;
        }

        System.out.println("UnAuthorized - Refresh cookie not found");
        return false;
    }

    public Cookie getNewJWT(Cookie[] cookies) {
        JSONObject jsonObject = new JSONObject();

        for (Cookie cookie : cookies) {
            if ("refresh".equals(cookie.getName())) {
                this.token = cookie.getValue();
                jsonObject = this.getAuthPayload();
                break;
            }
        }

        UserDAO userDAO = new UserDAO();
        UserModel user = userDAO.getUserByEmail((String) jsonObject.get("username"));

        // create jwt
        JSONObject payload = new JSONObject();
        payload.put("user", user.getId());
        payload.put("name", user.getFirst_name());
        payload.put("page", user.getRole());

        if (user.getRole().equals("supplier")) {
            SupplierModel supplier = new SupplierModel(user.getId());
            supplier.getSupplier();
            payload.put("sId", supplier.getId());
        } else {
            EmployeeModel employee = new EmployeeModel(user.getId(), 0);
            employee.getEIdById();
            payload.put("sId", employee.geteId());
        }

        this.accessPayload = payload;
        this.token =  this.generateJwt();

        Cookie cookie = new Cookie("jwt", this.token);
        cookie.setPath("/");
        cookie.setSecure(true); // For HTTPS
        cookie.setHttpOnly(false);

        // Set the cookie to expire after one day (in seconds)
        int oneDayInSeconds = 24 * 60 * 60;
        cookie.setMaxAge(oneDayInSeconds);

        return cookie;
    }
}
