package org.jom.Controller.Collector;

import org.jom.Auth.JwtUtils;
import org.jom.Dao.OTPDAO;
import org.jom.Dao.UserDAO;
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

@WebServlet("/verify-amount")
public class VerifyAmountServlet extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Get all cookies from the request
        Cookie[] cookies = request.getCookies();
        JwtUtils jwtUtils = new JwtUtils();

        if (!jwtUtils.CheckJWT(cookies)) {
            if (jwtUtils.CheckRefresh(cookies))
                response.addCookie(jwtUtils.getNewJWT(cookies));
            else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"UnAuthorized\"}");
                return;
            }
        }

        // get auth payload data
        JSONObject jsonObject = jwtUtils.getAuthPayload();
        String role = (String) jsonObject.get("page");

        StringBuilder requestBody = new StringBuilder();

        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }

        JSONObject json_data = new JSONObject(requestBody.toString());
        int otp = json_data.getInt("otp");
        int otpId = json_data.getInt("oId");

        if (role.equals("collector") || role.equals("admin")) {

            OTPDAO otpDao = new OTPDAO();
            OTPModel record = otpDao.getRecord(otpId);

            if (otp == record.getOtp()) {
                record.updateValidity(0);
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"message\": \"OTP Validated\"}");
                System.out.println("Otp validated");

            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"OTP Invalid\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("{\"message\": \"Invalid User\"}");
        }
    }
}
