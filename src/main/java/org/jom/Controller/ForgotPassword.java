package org.jom.Controller;

import org.jom.Dao.UserDAO;
import org.jom.Email.SendEmail;
import org.jom.Model.OTPModel;
import org.jom.Model.UserModel;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
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
        if(user.getId() != 0) {
            try {
                SendEmail sendEmail = new SendEmail();
                String subject = "Password Recovery";
                int otp = sendEmail.SendOTP(email,subject);
                System.out.println(otp);

                OTPModel record = new OTPModel(user.getId(),email,otp);
                record.saveOTP();

                if(record.getId() != 0) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.write("{\"message\": \"OTP Sent\",\"oId\":\""+ record.getId() +"\"}");
                }else {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    out.write("{\"message\": \"OTP not Sent\"}");
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                out.close();
            }
        } else{
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("{\"message\": \"Invalid Email\"}");
        }

    }
}
