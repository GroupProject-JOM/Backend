package org.jom.Controller;

import com.google.gson.Gson;
import org.jom.Auth.JwtUtils;
import org.jom.Dao.Supplier.SupplierDAO;
import org.jom.Model.EmployeeModel;
import org.jom.Model.LoginModel;
import org.jom.Model.SupplierModel;
import org.jom.Model.UserModel;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/signin")
public class LoginServlet extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        try {
            Gson gson = new Gson();
            // json data to user object
            BufferedReader bufferedReader = request.getReader();
            LoginModel login = gson.fromJson(bufferedReader, LoginModel.class);
            UserModel user = login.getUser();

            if (user.getId() != 0) {
                if (user.getValidity() != 0) {
                    String hashedPwd = passwordHash(login.getPassword());
                    if (user.getPassword().equals(hashedPwd)) {
                        int sId = 0;
                        if (user.getRole().equals("supplier")) {
                            SupplierModel supplier = new SupplierModel(user.getId());
                            supplier.getSupplier();
                            sId = supplier.getId();
                        } else {
                            EmployeeModel employee = new EmployeeModel(user.getId(), 0);
                            employee.getEIdById();
                            sId = employee.geteId();
                        }

                        // create jwt
                        JSONObject payload = new JSONObject();
                        payload.put("user", user.getId());
                        payload.put("name", user.getFirst_name());
                        payload.put("page", user.getRole());
                        payload.put("email", user.getEmail());
                        payload.put("sId", sId);

                        JwtUtils jwtUtils = new JwtUtils(payload);
                        String token = jwtUtils.generateJwt();

                        Cookie cookie = new Cookie("jwt", token);
                        cookie.setPath("/");
                        cookie.setSecure(true); // For HTTPS
                        cookie.setHttpOnly(false);

                        // Set the cookie to expire after one day (in seconds)
                        int oneDayInSeconds = 24 * 60 * 60;
                        cookie.setMaxAge(oneDayInSeconds);

                        response.addCookie(cookie);

                        // create refresh token
                        JSONObject refreshPayload = new JSONObject();
                        refreshPayload.put("username", user.getEmail());
                        refreshPayload.put("id", user.getId());
                        refreshPayload.put("page", user.getRole());

                        jwtUtils.setRefreshPayload(refreshPayload);
                        String refresh = jwtUtils.generateRefresh();

                        Cookie refreshCookie = new Cookie("refresh", refresh);
                        refreshCookie.setPath("/");
                        refreshCookie.setSecure(true); // For HTTPS
                        refreshCookie.setHttpOnly(false);

                        // Set the cookie to expire after six months (in seconds)
                        int oneSixMonthsInSeconds = 24 * 60 * 60 * 180;
                        refreshCookie.setMaxAge(oneSixMonthsInSeconds);

                        response.addCookie(refreshCookie);

                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"message\": \"Login successfully\",\"jwt\": \"" + token + "\",\"refresh\":\"" + refresh + "\"}");
                        System.out.println("Login successful");

                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"message\": \"password\"}");
                        System.out.println("Wrong password");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.write("{\"message\": \"validate\"}");
                    System.out.println("User not validated");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"username\"}");
                System.out.println("Login incorrect");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }

    public String passwordHash(String password) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA");
        messageDigest.update(password.getBytes());
        byte[] rbt = messageDigest.digest();
        StringBuilder stringBuilder = new StringBuilder();

        for (byte b : rbt) {
            stringBuilder.append(String.format("%02x", b));
        }
        return stringBuilder.toString();
    }
}
