package org.jom.Controller.Supplier.Collection;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jom.Auth.JwtUtils;
import org.jom.Dao.Supplier.Collection.SupplyDAO;
import org.jom.Model.Collection.CollectionModel;
import org.jom.Model.Collection.SupplyModel;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@WebServlet("/collections")
public class CollectionsServlet extends HttpServlet {
    //Get all supplies
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Get all cookies from the request
        Cookie[] cookies = request.getCookies();
        JSONObject jsonObject = new JSONObject();
        int supplier_id = 0;
        boolean jwtCookieFound = false;

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    JwtUtils jwtUtils = new JwtUtils(cookie.getValue());
                    if (!jwtUtils.verifyJwtAuthentication()) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        out.write("{\"message\": \"UnAuthorized\"}");
                        System.out.println("UnAuthorized1");
                        return;
                    }
                    jsonObject = jwtUtils.getAuthPayload();
                    jwtCookieFound = true;
                    break;  // No need to continue checking if "jwt" cookie is found
                }
            }
        } else {response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("{\"message\": \"UnAuthorized\"}");
            System.out.println("No cookies found in the request.");
            return;
        }

        // If "jwt" cookie is not found, respond with unauthorized status
        if (!jwtCookieFound) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write("{\"message\": \"UnAuthorized - JWT cookie not found\"}");
            System.out.println("UnAuthorized - JWT cookie not found");
            return;
        }

        supplier_id = (int) jsonObject.get("sId");

        // Create month pattern
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(currentDate);
        String yearMonth = formattedDate.substring(0, 7);
        String monthPattern = yearMonth + "-%";

        try {
            SupplyDAO supplyDAO = new SupplyDAO();
            List<SupplyModel> ongoing = supplyDAO.getAllOngoing(supplier_id);
            List<SupplyModel> past = supplyDAO.getAllPast(supplier_id);
            int income = supplyDAO.getIncome(supplier_id, monthPattern);

            Gson gson = new Gson();
            String ongoing_array = gson.toJson(ongoing);
            String past_array = gson.toJson(past);

            if (ongoing.size() != 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.write("{\"ongoing\": " + ongoing_array + ",\"past\":" + past_array + ",\"income\":" + income + "}");
                System.out.println("Supplier dashboard tables contents");
            } else {
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                out.write("{\"size\": \"0\",\"past\":" + past_array + ",\"income\":" + income + "}");
                System.out.println("No Ongoing Supplies");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }
}
