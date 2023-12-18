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


@WebServlet("/email")
public class SendEmailOTPServlet extends HttpServlet {
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
        int id = jsonObject.getInt("id");
        UserDAO userDAO = new UserDAO();
        UserModel user = userDAO.getUserById(id);

        //check if emails are correct
        if(user.getEmail().equals(email)) {
            try {
                SendEmail sendEmail = new SendEmail();
                String subject = "Email Verification";
                int otp = SendEmail.SendOTP(email,subject);
                System.out.println(otp);

                OTPModel record = new OTPModel(id,email,otp);
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
            out.write("{\"message\": \"Invalid User\"}");
        }

    }
}
