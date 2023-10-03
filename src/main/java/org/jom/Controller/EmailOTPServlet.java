package org.jom.Controller;

import com.google.gson.Gson;
import org.jom.Model.LoginModel;
import org.jom.Model.UserModel;
import org.jom.OTP.SendEmailOTP;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/email")
public class EmailOTPServlet extends HttpServlet {
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
        try {

            SendEmailOTP sendEmailOTP = new SendEmailOTP();
            int otp = sendEmailOTP.SendOTP(email);
            System.out.println(otp);

            response.setStatus(HttpServletResponse.SC_OK);
            out.write("{\"message\": \"OTP Sent\",\"otp\":\""+ otp +"\"}");


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }
}
