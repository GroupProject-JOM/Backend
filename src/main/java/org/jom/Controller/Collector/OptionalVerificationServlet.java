package org.jom.Controller.Collector;

import org.jom.Dao.Supplier.Collection.CollectionDAO;
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

@WebServlet("/optional-verification")
public class OptionalVerificationServlet extends HttpServlet {
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
        int final_amount = jsonObject.getInt("amount");
        int collection_id = jsonObject.getInt("id");
        int user_id = jsonObject.getInt("user");

        UserDAO userDAO = new UserDAO();
        UserModel user = userDAO.getUserById(user_id);

        //check if emails are correct
        if(user.getRole().equals("collector") || user.getRole().equals("admin")) {

            CollectionDAO collectionDAO = new CollectionDAO();
            String supplier_email = collectionDAO.getSupplierEmail(collection_id,user_id);
            String supplier_name = collectionDAO.getSupplierName(collection_id,user_id);

            try {
                SendEmail sendEmail = new SendEmail();
                int otp = sendEmail.optionalVerification(supplier_email,final_amount,supplier_name);
                System.out.println(otp);

                OTPModel record = new OTPModel(user_id,user.getEmail(),otp);
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
