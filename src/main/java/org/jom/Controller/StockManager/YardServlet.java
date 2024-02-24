package org.jom.Controller.StockManager;

import com.google.gson.Gson;
import org.jom.Auth.JwtUtils;
import org.jom.Dao.BatchDAO;
import org.jom.Dao.CollectorDAO;
import org.jom.Dao.UserDAO;
import org.jom.Dao.YardDAO;
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

                    if (status && collectorDAO.updateTodayAmount(final_amount, collector)) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"message\": \"Yard updated successfully\"}");
                        System.out.println("Yard updated successfully");
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("{\"message\": \"Yard is not updated\"}");
                        System.out.println("Yard is not updated");
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
