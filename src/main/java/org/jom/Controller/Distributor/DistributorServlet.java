package org.jom.Controller.Distributor;

import com.google.gson.Gson;
import org.jom.Auth.JwtUtils;
import org.jom.Dao.DistributionDAO;
import org.jom.Dao.OutletDAO;
import org.jom.Dao.ProductsDAO;
import org.jom.Dao.UserDAO;
import org.jom.Model.DistributionModel;
import org.jom.Model.ProductModel;
import org.jom.Model.UserModel;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/distributor")
public class DistributorServlet extends HttpServlet {
    // Get distributor dashboard content
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
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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
                if (user.getRole().equals("distributor")) {
                    DistributionDAO distributionDAO = new DistributionDAO();
                    List<DistributionModel> products = distributionDAO.DistributorsOnlyRemaining(user_id);
                    List<DistributionModel> distributions = distributionDAO.DistributionRecordsFromYear(user_id);
                    List<DistributionModel> visitsArray = distributionDAO.lastSevenDaysVisits(user_id);
                    int visits = distributionDAO.todayDistributionCount(user_id);

                    Gson gson = new Gson();
                    // Object array to json
                    String objectArray = gson.toJson(products);
                    String distributionsArray = gson.toJson(distributions);
                    String visitsStringArray = gson.toJson(visitsArray);

                    ProductsDAO productsDAO = new ProductsDAO();
                    OutletDAO outletDAO = new OutletDAO();

                    if (products.size() != 0) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"list\":" + objectArray + ",\"accepted\":" + productsDAO.acceptedProductCount() + ",\"allocated\":" + distributionDAO.allocatedAcceptedProductCount(user_id) + ",\"distributions\":" + distributionsArray + ",\"visits\":" + visits + ",\"outlets\":" + outletDAO.rowCount() + ",\"last_seven\":" + visitsStringArray + "}");
                        System.out.println("View all remaining Products");
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"size\": 0,\"accepted\":" + productsDAO.acceptedProductCount() + ",\"allocated\":" + distributionDAO.allocatedAcceptedProductCount(user_id) + ",\"distributions\":" + distributionsArray + ",\"visits\":" + visits + ",\"outlets\":" + outletDAO.rowCount() + ",\"last_seven\":" + visitsStringArray + "}");
                        System.out.println("No remaining Products");
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
