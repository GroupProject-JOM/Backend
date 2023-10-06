package org.jom.Controller;

import org.jom.Dao.OTPDAO;
import org.jom.Dao.UserDAO;
import org.jom.Model.OTPModel;
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
        UserModel user = UserDAO.getUserById(id);

        //check if emails are correct
        if(user.getEmail().equals(email)) {
            try {
                SendEmailOTP sendEmailOTP = new SendEmailOTP();
                int otp = SendEmailOTP.SendOTP(email);
                System.out.println(otp);

                OTPModel record = new OTPModel(id,email,otp);
                record.saveOTP();

                if(record.getId() != 0) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.write("{\"message\": \"OTP Sent\",\"oId\":\""+ record.getId() +"\"}");
                }else {
                    // TODO handle otp record unsuccess
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            } finally {
                out.close();
            }
        } else{
            // TODO handle error wrong email
        }

    }
}
