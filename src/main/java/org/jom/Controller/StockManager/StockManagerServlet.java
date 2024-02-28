package org.jom.Controller.StockManager;

import com.google.gson.Gson;
import org.jom.Auth.JwtUtils;
import org.jom.Dao.CocoRateDAO;
import org.jom.Dao.EmployeeDAO;
import org.jom.Dao.ProductionDAO;
import org.jom.Dao.Supplier.Collection.CollectionDAO;
import org.jom.Dao.Supplier.Collection.SupplyDAO;
import org.jom.Dao.UserDAO;
import org.jom.Model.CocoModel;
import org.jom.Model.Collection.SupplyModel;
import org.jom.Model.EmployeeModel;
import org.jom.Model.ProductionModel;
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

@WebServlet("/stock-manager")
public class StockManagerServlet extends HttpServlet {
    //Get Dashboard content
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
                if (role.equals("stock-manager")) {
                    Date currentDate = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(currentDate);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String today = dateFormat.format(currentDate);

                    SupplyDAO supplyDAO = new SupplyDAO();
                    CollectionDAO collectionDAO = new CollectionDAO();
                    ProductionDAO productionDAO = new ProductionDAO();
                    Gson gson = new Gson();

                    List<SupplyModel> supply_requests = supplyDAO.getAll();
                    List<SupplyModel> today_supplies = supplyDAO.getCollectionsByDate(today);
                    int today_completed_count = collectionDAO.completedRowCountByDate(today);
                    int today_remaining_count = collectionDAO.remainingRowCountByDate(today);
                    List<ProductionModel> production_requests = productionDAO.getAllPendingProductionRequests();

                    String today_array = gson.toJson(today_supplies);
                    String production_array = gson.toJson(production_requests);
                    String request_array = gson.toJson(supply_requests);

                    response.setStatus(HttpServletResponse.SC_OK);
                    out.write("{\"size\": " + supply_requests.size() + ",\"list\":" + request_array + ",\"today_size\":" + today_supplies.size() + ",\"today\":" + today_array + ",\"completed\":" + today_completed_count + ",\"remaining\":" + today_remaining_count + ",\"p_request\":" + production_requests.size() + ",\"production\":" + production_array + "}");
                    System.out.println("Stock manager dashboard tables contents");
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
