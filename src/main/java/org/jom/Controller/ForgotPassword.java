package org.jom.Controller;

import org.jom.Auth.JwtUtils;
import org.jom.Dao.OTPDAO;
import org.jom.Dao.UserDAO;
import org.jom.Email.SendEmail;
import org.jom.Model.OTPModel;
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

@WebServlet("/forgot-password")
public class ForgotPassword extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        StringBuilder requestBody = new StringBuilder();

        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }

        JSONObject jsonObject = new JSONObject(requestBody.toString());
        String email = jsonObject.getString("email");

        UserDAO userDAO = new UserDAO();
        UserModel user = userDAO.getUserByEmail(email);

        //check if email exists
        if (user.getId() != 0) {
            try {
                SendEmail sendEmail = new SendEmail();
                String subject = "Password Recovery";
                int otp = sendEmail.SendOTP(email, subject);
                System.out.println(otp);

                OTPModel record = new OTPModel(user.getId(), email, otp);
                record.saveOTP();

                if (record.getId() != 0) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.write("{\"message\": \"OTP Sent\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    out.write("{\"message\": \"OTP not Sent\"}");
                }

                JSONObject payload = new JSONObject();
                payload.put("email", user.getEmail());
                payload.put("id", record.getId());

                JwtUtils jwtUtils = new JwtUtils(payload);
                String token = jwtUtils.generateJwt();

                Cookie cookie = new Cookie("jwt-forgot", token);
                cookie.setPath("/");
                cookie.setSecure(true); // For HTTPS
                cookie.setHttpOnly(false);

                // Set the cookie to expire after 10 minutes (in seconds)
                int tenMins = 10 * 60;
                cookie.setMaxAge(tenMins);

                response.addCookie(cookie);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                out.close();
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("{\"message\": \"Invalid Email\"}");
        }

    }

    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Get all cookies from the request
        Cookie[] cookies = request.getCookies();
        JSONObject jsonObject = new JSONObject();
        int otp_id = 0;
        boolean jwtCookieFound = false;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt-forgot".equals(cookie.getName())) {
                    JwtUtils jwtUtils = new JwtUtils(cookie.getValue());
                    if (!jwtUtils.verifyJwtAuthentication()) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        out.write("{\"message\": \"UnAuthorized\"}");
                        System.out.println("UnAuthorized1");
                        return;
                    }
                    jsonObject = jwtUtils.getAuthPayload();
                    jwtCookieFound = true;
                    break;  // No need to continue checking if "jwt" cookie is found
                }
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("{\"message\": \"UnAuthorized\"}");
            System.out.println("No cookies found in the request.");
            return;
        }

        // If "jwt" cookie is not found, respond with unauthorized status
        if (!jwtCookieFound) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("{\"message\": \"UnAuthorized - JWT cookie not found\"}");
            System.out.println("UnAuthorized - JWT cookie not found");
            return;
        }

        otp_id = (int) jsonObject.get("id");
        String email = jsonObject.getString("email");

        StringBuilder requestBody = new StringBuilder();

        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }

        JSONObject jsonDta = new JSONObject(requestBody.toString());
        int otp = jsonDta.getInt("otp");

        OTPDAO otpDao = new OTPDAO();
        OTPModel record = otpDao.getRecord(otp_id);

        UserDAO userDAO = new UserDAO();
        UserModel user = userDAO.getUserByEmail(email);

        try {
            if (otp == record.getOtp()) {
                record.updateValidity(0);
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"message\": \"OTP verified\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                out.write("{\"message\": \"OTP not verified\"}");
            }

            JSONObject payload = new JSONObject();
            payload.put("email", user.getEmail());
            payload.put("user", user.getId());
            payload.put("id", record.getId());

            JwtUtils jwtUtils = new JwtUtils(payload);
            String token = jwtUtils.generateJwt();

            Cookie cookie = new Cookie("jwt-forgot", token);
            cookie.setPath("/");
            cookie.setSecure(true); // For HTTPS
            cookie.setHttpOnly(false);

            // Set the cookie to expire after 10 minutes (in seconds)
            int tenMins = 10 * 60;
            cookie.setMaxAge(tenMins);
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Get all cookies from the request
        Cookie[] cookies = request.getCookies();
        JSONObject jsonObject = new JSONObject();
        int user_id = 0;
        boolean jwtCookieFound = false;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt-forgot".equals(cookie.getName())) {
                    JwtUtils jwtUtils = new JwtUtils(cookie.getValue());
                    if (!jwtUtils.verifyJwtAuthentication()) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        out.write("{\"message\": \"UnAuthorized\"}");
                        System.out.println("UnAuthorized1");
                        return;
                    }
                    jsonObject = jwtUtils.getAuthPayload();
                    jwtCookieFound = true;
                    break;  // No need to continue checking if "jwt" cookie is found
                }
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("{\"message\": \"UnAuthorized\"}");
            System.out.println("No cookies found in the request.");
            return;
        }

        // If "jwt" cookie is not found, respond with unauthorized status
        if (!jwtCookieFound) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("{\"message\": \"UnAuthorized - JWT cookie not found\"}");
            System.out.println("UnAuthorized - JWT cookie not found");
            return;
        }

        user_id = (int) jsonObject.get("user");
        String password = request.getParameter("password");

        UserDAO userDAO = new UserDAO();
        UserModel user = userDAO.getUserById(user_id);

        try {
            if (user.getId() != 0) {
                if (userDAO.updatePassword(user_id, password)) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.write("{\"message\": \"Password updated\"}");
                    System.out.println("Password updated");
                } else {
                    response.setStatus(HttpServletResponse.SC_ACCEPTED);
                    out.write("{\"message\": \"Cannot update password\"}");
                    System.out.println("Cannot update password");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"Invalid User\"}");
                System.out.println("Invalid User");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }

    }
}
