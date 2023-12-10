package org.jom.Controller.StockManager;

import com.google.gson.Gson;
import org.jom.Auth.JwtUtils;
import org.jom.Dao.CollectorDAO;
import org.jom.Dao.Supplier.Collection.CollectionDAO;
import org.jom.Dao.Supplier.Collection.SupplyDAO;
import org.jom.Dao.UserDAO;
import org.jom.Model.Collection.CollectorModel;
import org.jom.Model.Collection.SupplyModel;
import org.jom.Model.UserModel;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@WebServlet("/collectors")
public class CollectorsServlet extends HttpServlet {
    //Get collectors with date
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Get all cookies from the request
        Cookie[] cookies = request.getCookies();
        JSONObject jsonObject = new JSONObject();
        int user_id = 0;
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

        user_id = (int) jsonObject.get("user");

        try {
            UserDAO userDAO = new UserDAO();
            UserModel user = userDAO.getUserById(user_id);

            if (user.getId() != 0) {
                if (user.getRole().equals("stock-manager")) {

                    Date currentDate = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(currentDate);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String today = dateFormat.format(currentDate);

                    CollectorDAO collectorDAO = new CollectorDAO();
                    CollectionDAO collectionDAO = new CollectionDAO();
                    Gson gson = new Gson();

                    List<CollectorModel> collectors = collectorDAO.getCollectors(today);

                    String collectors_array = gson.toJson(collectors);

                    if (collectors.size() != 0) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"size\": " + collectors.size() + ",\"list\":" + collectors_array + "}");
                        System.out.println("Send collectors");
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"size\": 0}");
                        System.out.println("No collectors");
                    }

                } else {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    out.write("{\"message\": \"Invalid User\"}");
                    System.out.println("Invalid User");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.write("{\"message\": \"Invalid User\"}");
                System.out.println("Invalid User");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            out.close();
        }
    }
}
