package org.jom.Controller.Collector;

import org.jom.Auth.JwtUtils;
import org.jom.Dao.Supplier.Collection.CollectionDAO;
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

@WebServlet("/optional-verification")
public class OptionalVerificationServlet extends HttpServlet {
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
        int user_id = (int) jsonObject.get("user");
        String role = (String) jsonObject.get("page");
        String email = (String) jsonObject.get("email");

        StringBuilder requestBody = new StringBuilder();

        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }

        JSONObject json_data = new JSONObject(requestBody.toString());
        int final_amount = json_data.getInt("amount");
        int collection_id = json_data.getInt("id");

        if(role.equals("collector") || role.equals("admin")) {

            CollectionDAO collectionDAO = new CollectionDAO();
            String supplier_email = collectionDAO.getSupplierEmail(collection_id,user_id);
            String supplier_name = collectionDAO.getSupplierName(collection_id,user_id);

            try {
                SendEmail sendEmail = new SendEmail();
                int otp = sendEmail.optionalVerification(supplier_email,final_amount,supplier_name);
                System.out.println(otp);

                OTPModel record = new OTPModel(user_id,email,otp);
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
