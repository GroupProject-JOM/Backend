package org.jom.Controller.Collector;

import org.jom.Dao.OTPDAO;
import org.jom.Dao.UserDAO;
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

@WebServlet("/verify-amount")
public class VerifyAmountServlet extends HttpServlet {
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

        // TODO handle internal server error
        JSONObject jsonObject = new JSONObject(requestBody.toString());
        int otp = jsonObject.getInt("otp");
        int user_id = jsonObject.getInt("user");
        int otpId = jsonObject.getInt("oId");

        UserDAO userDAO = new UserDAO();
        UserModel user = userDAO.getUserById(user_id);

        if (user.getRole().equals("collector") || user.getRole().equals("admin")) {

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
