package org.jom.Controller.Distributor;

import com.google.gson.Gson;
import org.jom.Auth.JwtUtils;
import org.jom.Dao.BatchDAO;
import org.jom.Dao.DistributionDAO;
import org.jom.Dao.ProductsDAO;
import org.jom.Dao.UserDAO;
import org.jom.Model.DistributionModel;
import org.jom.Model.ProductModel;
import org.jom.Model.UserModel;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/product-list")
public class DistributionServlet extends HttpServlet {
    // Get products for distribution
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

                    Gson gson = new Gson();
                    // Object array to json
                    String objectArray = gson.toJson(products);

                    if (products.size() != 0) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"size\": " + products.size() + ",\"list\":" + objectArray + "}");
                        System.out.println("Send remaining products");
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"size\": \"0\"}");
                        System.out.println("No remaining products");
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

    // Complete distribution
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

        StringBuilder requestBody = new StringBuilder();
        try {
            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
            }

            JSONObject json_data = new JSONObject(requestBody.toString());

            // Retrieve arrays as JSONArrays
            JSONArray amountsArray = json_data.getJSONArray("amounts");
            JSONArray productsArray = json_data.getJSONArray("products");
            JSONArray pricesArray = json_data.getJSONArray("prices");
            int outlet = json_data.getInt("id");

            // Convert JSONArrays to String arrays
            int[] amounts = new int[amountsArray.length()];
            int[] products = new int[amountsArray.length()];
            int[] prices = new int[amountsArray.length()];
            String[] finalPrices = new String[amountsArray.length()];

            for (int i = 0; i < amountsArray.length(); i++) {
                amounts[i] = amountsArray.getInt(i);
                products[i] = productsArray.getInt(i);
                prices[i] = pricesArray.getInt(i);
                finalPrices[i] = Integer.toString(prices[i] * amounts[i]);
            }

            UserDAO userDAO = new UserDAO();
            UserModel user = userDAO.getUserById(user_id);

            if (user.getId() != 0) {
                if (user.getRole().equals("distributor")) {

                    DistributionDAO distributionDAO = new DistributionDAO();
                    boolean status = false;

                    for (int i = 0; i < amountsArray.length(); i++) {
                        DistributionModel distributionModel = new DistributionModel(amounts[i], products[i], finalPrices[i], outlet, user_id);

                        if (distributionDAO.addDistributionRecord(distributionModel) != 0) status = true;
                        else status = false;

                        if (distributionDAO.decrementDistributorAmount(amounts[i], products[i], user_id)) status = true;
                        else status = false;
                    }

                    if (status) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"message\": \"Products distributed successfully\"}");
                        System.out.println("Products distributed successfully");
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"Products distribution is not completed\"}");
                        System.out.println("Products distribution is not completed");
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
