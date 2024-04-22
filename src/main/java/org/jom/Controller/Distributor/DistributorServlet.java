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

        try {
            if (user_id != 0) {
                if (role.equals("distributor")) {
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
