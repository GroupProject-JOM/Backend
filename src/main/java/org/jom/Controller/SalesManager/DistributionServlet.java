package org.jom.Controller.SalesManager;

import com.google.gson.Gson;
import org.jom.Auth.JwtUtils;
import org.jom.Dao.BatchDAO;
import org.jom.Dao.DistributionDAO;
import org.jom.Dao.ProductsDAO;
import org.jom.Dao.UserDAO;
import org.jom.Model.BatchModel;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/distribution")
public class DistributionServlet extends HttpServlet {
    //send distribution data for create distribution
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
        int batch_id = Integer.parseInt(request.getParameter("id"));

        try {
            UserDAO userDAO = new UserDAO();
            UserModel user = userDAO.getUserById(user_id);

            if (user.getId() != 0) {
                if (user.getRole().equals("sales-manager")) {

                    BatchDAO batchDAO = new BatchDAO();
                    BatchModel batchModel = batchDAO.getBatch(batch_id);

                    String[] stringArray = batchModel.getProducts().split(",");

                    int[] idList = new int[stringArray.length];

                    for (int i = 0; i < stringArray.length; i++) {
                        idList[i] = Integer.parseInt(stringArray[i]);
                    }

                    DistributionDAO distributionDAO = new DistributionDAO();
                    Gson gson = new Gson();
                    List<String> distribution_list = new ArrayList<>();

                    for (int num : idList) {
                        List<DistributionModel> distributions = distributionDAO.getRemaining(num);
                        String object = gson.toJson(distributions);
                        distribution_list.add(object);
                    }

                    if (idList.length != 0) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"distributions\": " + distribution_list + "}");
                        System.out.println("Send distributions");
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"message\": \"No distributions\"}");
                        System.out.println("No distributions");
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

    // Allocate distributors
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
            JSONArray distributorsArray = json_data.getJSONArray("distributors");
            int product_id = json_data.getInt("product");
            int batch_id = json_data.getInt("id");

            // Convert JSONArrays to String arrays
            int[] amounts = new int[amountsArray.length()];
            int[] distributors = new int[amountsArray.length()];

            int totalAmount = 0;

            for (int i = 0; i < amountsArray.length(); i++) {
                amounts[i] = amountsArray.getInt(i);
                totalAmount += amounts[i];

                distributors[i] = distributorsArray.getInt(i);
            }

            UserDAO userDAO = new UserDAO();
            UserModel user = userDAO.getUserById(user_id);

            if (user.getId() != 0) {
                if (user.getRole().equals("sales-manager")) {

                    BatchDAO batchDAO = new BatchDAO();
                    BatchModel batchModel = batchDAO.getBatch(batch_id);

                    String[] stringDistributionArray = batchModel.getDistribution().split(",");
                    String[] stringProductsArray = batchModel.getProducts().split(",");
                    String[] stringProductsCountArray = batchModel.getProducts_count().split(",");

                    int[] distributedList = new int[stringProductsArray.length];
                    int[] productList = new int[stringProductsArray.length];
                    int[] productsCountList = new int[stringProductsArray.length];
                    int count = 0;
                    int status = 2;

                    for (int i = 0; i < stringProductsArray.length; i++) {
                        distributedList[i] = Integer.parseInt(stringDistributionArray[i]);
                        productList[i] = Integer.parseInt(stringProductsArray[i]);
                        productsCountList[i] = Integer.parseInt(stringProductsCountArray[i]);

                        if (productList[i] == product_id) distributedList[i] += totalAmount;
                        if (productsCountList[i] <= distributedList[i]) count++;
                    }

                    if (count == stringProductsArray.length) status = 3;

                    String stringDistribution = intArrayToString(distributedList);

                    DistributionDAO distributionDAO = new DistributionDAO();
                    for (int i = 0; i < amountsArray.length(); i++) {
                        distributionDAO.UpdateDistributorAmount(amounts[i], product_id, distributors[i]);
                    }

                    if (batchDAO.UpdateDistribution(status, stringDistribution, batch_id)) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"message\": \"Production batch completed successfully\"}");
                        System.out.println("Products allocated successfully");
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"Production batch is not completed\"}");
                        System.out.println("Products allocation is not successful");
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

    public static String intArrayToString(int[] intArray) {
        StringBuilder resultBuilder = new StringBuilder();

        for (int i = 0; i < intArray.length; i++) {
            resultBuilder.append(intArray[i]);

            if (i < intArray.length - 1) {
                resultBuilder.append(",");
            }
        }

        return resultBuilder.toString();
    }
}
