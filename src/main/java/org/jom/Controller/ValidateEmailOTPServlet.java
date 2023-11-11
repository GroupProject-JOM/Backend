package org.jom.Controller;

import org.jom.Dao.OTPDAO;
import org.jom.Dao.UserDAO;
import org.jom.Model.*;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/validateE")
public class ValidateEmailOTPServlet extends HttpServlet {
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
        int id = jsonObject.getInt("id");
        int otpId = jsonObject.getInt("oId");

        UserDAO userDAO = new UserDAO();
        UserModel user = userDAO.getUserById(id);

        OTPDAO otpDao = new OTPDAO();
        OTPModel record = otpDao.getRecord(otpId);

        //check if emails are correct
        if(user.getValidity() != 1 ) {
            if(otp == record.getOtp()){
                user.updateValidity(1);
                record.updateValidity(0);
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"message\": \"OTP Validated\"}");
                System.out.println("Otp validated");

            }else{
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"OTP Invalid\"}");
            }
        } else{
            // TODO handle error user already validated
        }

    }

    // Send relevant user id for email
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String email = request.getParameter("email");

        try {
            LoginModel loginModel = new LoginModel(email);
            UserModel user = loginModel.getUser();

            SupplierModel supplier = new SupplierModel(user.getId());
            supplier.getSupplier();

            if(user.getId() !=0){
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"id\": "+ user.getId() +",\"sId\": \""+ supplier.getId() + "\"}");
                System.out.println("Send id and sId");
            }else{
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                out.write("{\"Id\": \"No user in this email\"}");
                System.out.println("No User");
            }
            // TODO handle

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }
}
