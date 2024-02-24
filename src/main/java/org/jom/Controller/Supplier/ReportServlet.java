package org.jom.Controller.Supplier;

import com.google.gson.Gson;
import org.jom.Auth.JwtUtils;
import org.jom.Dao.BatchDAO;
import org.jom.Dao.CocoRateDAO;
import org.jom.Dao.Supplier.Collection.CollectionDAO;
import org.jom.Dao.Supplier.Collection.SupplyDAO;
import org.jom.Dao.UserDAO;
import org.jom.Model.BatchModel;
import org.jom.Model.CocoModel;
import org.jom.Model.Collection.CollectionModel;
import org.jom.Model.Collection.SupplyModel;
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
import java.util.Date;
import java.util.List;

@WebServlet("/report")
public class ReportServlet extends HttpServlet {
    // get report page content to supplier
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
        int supplier_id = (int) jsonObject.get("sId");
        String role = (String) jsonObject.get("page");

        try {
            if (user_id != 0) {
                if (role.equals("supplier")) {
                    Date d = new Date();
                    int year = d.getYear();
                    int thisYear = year + 1900;
                    int lastYear = year + 1900 - 1;

                    CocoRateDAO cocoRateDAO = new CocoRateDAO();
                    CocoModel cocoRate = cocoRateDAO.getLastRecord();
                    List<CocoModel> last_six_records = cocoRateDAO.getLastSixMonthRecords();
                    List<Float> average_list = cocoRateDAO.getMonthlyAverageRate();

                    List<CocoModel> last_year_records = cocoRateDAO.getMonthlyTotal(lastYear, supplier_id);
                    List<CocoModel> this_year_records = cocoRateDAO.getMonthlyTotal(thisYear, supplier_id);

                    Gson gson = new Gson();
                    String object = gson.toJson(cocoRate); // Object array to json
                    String last_six = gson.toJson(last_six_records);
                    String avg = gson.toJson(average_list);
                    String this_year = gson.toJson(this_year_records);
                    String last_year = gson.toJson(last_year_records);

                    response.setStatus(HttpServletResponse.SC_OK);
                    out.write("{\"rate\": " + object + ",\"last_six\":" + last_six + ",\"size\":" + last_six_records.size() + ",\"avg\":" + avg + ",\"avg_size\":" + average_list.size() + ",\"this\":" + this_year + ",\"last\":" + last_year + "}");
                    System.out.println("Send rate");

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

    // get invoice content
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

        try {
            UserDAO userDAO = new UserDAO();

            if (user_id != 0) {
                if (role.equals("supplier")) {

                    StringBuilder requestBody = new StringBuilder();

                    try (BufferedReader reader = request.getReader()) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            requestBody.append(line);
                        }
                    }

                    JSONObject json_data = new JSONObject(requestBody.toString());
                    String sDate = json_data.getString("start");
                    String eDate = json_data.getString("end");

                    CollectionDAO collectionDAO = new CollectionDAO();
                    List<CollectionModel> collections = collectionDAO.getPaidCollections(sDate, eDate, user_id);
                    UserModel userData = userDAO.reportData(user_id);

                    Gson gson = new Gson();
                    String object1 = gson.toJson(collections);
                    String object2 = gson.toJson(userData);

                    if (collections.size() != 0) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"collections\": " + object1 + ",\"user_data\":" + object2 + "}");
                        System.out.println("Send collections between mentioned dates");
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"collections\": \"There are no collections between the mentioned dates\"}");
                        System.out.println("There are no collections between the mentioned dates");
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
