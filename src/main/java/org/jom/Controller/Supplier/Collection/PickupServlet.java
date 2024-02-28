package org.jom.Controller.Supplier.Collection;

import com.google.gson.Gson;
import org.jom.Auth.JwtUtils;
import org.jom.Dao.Supplier.Collection.CollectionDAO;
import org.jom.Model.Collection.PickupModel;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/pickup")
public class PickupServlet extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        /// Get all cookies from the request
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
        int supplier_id = (int) jsonObject.get("sId");

        try {
            Gson gson = new Gson();
            // json data to user object
            BufferedReader bufferedReader = request.getReader();
            PickupModel pickup = gson.fromJson(bufferedReader, PickupModel.class);
            pickup.setSupplier_id(supplier_id);

            if(pickup.getSupplier_id() == 0){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"UnAuthorized\"}");
                System.out.println("UnAuthorized");
                return;
            }

            if(pickup.getCollection_id() == 0){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"UnAuthorized\"}");
                System.out.println("UnAuthorized");
                return;
            }

            // TODO backend validations and user exists

            pickup.addPickup();

            if(pickup.getId() != 0){
                CollectionDAO collectionDAO = new CollectionDAO();
                if(collectionDAO.updateStatus(1,pickup.getCollection_id())) {
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.write("{\"message\": \"Pickup request added successfully\"}");
                    System.out.println("Pickup request added successfully");
                }
            }else{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"message\": \"Pickup request is not added\"}");
                System.out.println("Pickup request is not added");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }
}
