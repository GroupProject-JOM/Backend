package org.jom.Controller.StockManager;

import com.google.gson.Gson;
import org.jom.Auth.JwtUtils;
import org.jom.Dao.*;
import org.jom.Dao.Supplier.Collection.CollectionDAO;
import org.jom.Dao.Supplier.Collection.SupplyDAO;
import org.jom.Model.CocoModel;
import org.jom.Model.Collection.SupplyModel;
import org.jom.Model.ProductionModel;
import org.jom.Model.UserModel;
import org.jom.Model.YardModel;
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

import static java.lang.Float.parseFloat;

@WebServlet("/yard-data")
public class YardServlet extends HttpServlet {
    // update single yard data
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
            Gson gson = new Gson();
            BufferedReader bufferedReader = request.getReader();
            YardModel yard = gson.fromJson(bufferedReader, YardModel.class);

            if (user_id != 0) {
                if (role.equals("stock-manager")) {

                    YardDAO yardDAO = new YardDAO();

                    if (yardDAO.updateBlockData("yard" + yard.getDate(), yard)) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"messages\": \"Yard data updated successfully\"}");
                        System.out.println("Yard data updated successfully");
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"messages\": \"Yard data is not updated\"}");
                        System.out.println("Yard data is not updated");
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

    // to yard
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
            JSONArray blockArray = json_data.getJSONArray("blocks");
            JSONArray dayArray = json_data.getJSONArray("days");
            JSONArray amountArray = json_data.getJSONArray("amounts");
            int yard = json_data.getInt("yard");
            int collector = json_data.getInt("collector");
            int final_amount = -json_data.getInt("final_amount");
            int id = json_data.getInt("id");

            // Convert JSONArrays to String arrays
            int[] blocks = new int[blockArray.length()];
            int[] days = new int[blockArray.length()];
            int[] amounts = new int[blockArray.length()];

            for (int i = 0; i < blockArray.length(); i++) {
                blocks[i] = blockArray.getInt(i);
                days[i] = dayArray.getInt(i);
                amounts[i] = amountArray.getInt(i);
            }

            if (user_id != 0) {
                if (role.equals("stock-manager")) {

                    YardDAO yardDAO = new YardDAO();
                    boolean status = false;

                    for (int i = 0; i < blockArray.length(); i++) {
                        YardModel yardModel = new YardModel(blocks[i], amounts[i], days[i]);
                        status = yardDAO.updateBlockData("yard" + yard, yardModel);
                        if (!status) break;
                    }

                    CollectorDAO collectorDAO = new CollectorDAO();
                    CollectionDAO collectionDAO = new CollectionDAO();

                    System.out.println(collector);

                    if (collector < 1) {
                        if (status && collectionDAO.updateFinalAmount(id, -final_amount)) {
                            response.setStatus(HttpServletResponse.SC_OK);
                            out.write("{\"message\": \"Yard updated successfully\"}");
                            System.out.println("Yard updated successfully");
                        } else {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            out.write("{\"message\": \"Yard is not updated\"}");
                            System.out.println("Yard is not updated");
                        }
                    } else {
                        if (status && collectorDAO.updateTodayAmount(final_amount, collector)) {
                            response.setStatus(HttpServletResponse.SC_OK);
                            out.write("{\"message\": \"Yard updated successfully\"}");
                            System.out.println("Yard updated successfully");
                        } else {
                            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            out.write("{\"message\": \"Yard is not updated\"}");
                            System.out.println("Yard is not updated");
                        }
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

    // complete yard collection
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

        int id = Integer.parseInt(request.getParameter("id"));

        try {
            if (user_id != 0) {
                if (role.equals("stock-manager")) {

                    SupplyDAO supplyDAO = new SupplyDAO();
                    SupplyModel supply = supplyDAO.getSupply(id);
                    int final_amount = supply.getFinal_amount();

                    CollectionDAO collectionDAO = new CollectionDAO();
                    org.jom.Dao.Supplier.Collection.YardDAO yardDAO = new org.jom.Dao.Supplier.Collection.YardDAO();

                    String date = collectionDAO.getRequestedDateById(id);

                    CocoRateDAO cocoRateDAO = new CocoRateDAO();
                    CocoModel cocoRate = cocoRateDAO.getRateByDate(date.substring(0, 10));

                    float value = final_amount * parseFloat(cocoRate.getPrice());

                    if (collectionDAO.updateFinalAmount(final_amount, value, id) && yardDAO.updateDeliveredTime(id)) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"message\": \"Collection completed successfully\"}");
                        System.out.println("Collection completed successfully");
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"Collection is not completed\"}");
                        System.out.println("Collection is not completed");
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
