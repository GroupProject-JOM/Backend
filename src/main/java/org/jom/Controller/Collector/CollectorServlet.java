package org.jom.Controller.Collector;

import com.google.gson.Gson;
import org.jom.Dao.EmployeeDAO;
import org.jom.Dao.Supplier.Collection.SupplyDAO;
import org.jom.Model.Collection.SupplyModel;
import org.jom.Model.EmployeeModel;

import javax.servlet.annotation.WebServlet;
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

@WebServlet("/collector")
public class CollectorServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int employee_id = Integer.parseInt(request.getParameter("sId"));

        try {
            EmployeeDAO employeeDAO = new EmployeeDAO();
            EmployeeModel stock_manager = employeeDAO.getEmployee(employee_id);

            if (stock_manager.geteId() != 0) {
                if (stock_manager.getRole().equals("collector")) {

                    Date currentDate = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(currentDate);

                    calendar.add(Calendar.DAY_OF_MONTH, 2);
                    Date nextDay = calendar.getTime();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                    String today = dateFormat.format(currentDate);
                    String day_after_tomorrow = dateFormat.format(nextDay);

                    SupplyDAO supplyDAO = new SupplyDAO();

                    List<SupplyModel> today_collections = supplyDAO.getCollectionByDay(employee_id, today);
                    List<SupplyModel> upcoming_collections = supplyDAO.getUpcomingCollections(employee_id, today,day_after_tomorrow);
                    int today_count = supplyDAO.getCollectionCount(employee_id, today);

                    Gson gson = new Gson();

                    String today_collec = gson.toJson(today_collections); // Object array to json
                    String upcoming_collec = gson.toJson(upcoming_collections); // Object array to json

                    if (today_collections.size() != 0 && upcoming_collections.size() != 0) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"size\": " + today_collections.size() + ",\"today\":" + today_collec + ",\"upcoming\":" + upcoming_collec + ",\"count\":" + today_count + "}");
                        System.out.println("Collector dashboard tables contents");
                    } else if (today_collections.size() == 0 && upcoming_collections.size() == 0) {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"size\": \"-2\"}");
                        System.out.println("No collections");
                    } else if (today_collections.size() == 0) {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"size\": \"-1\",\"upcoming\":" + upcoming_collec + "}");
                        System.out.println("No collections today");
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"size\": " + today_collections.size() + ",\"today\":" + today_collec + ",\"count\":" + today_count + "}");
                        System.out.println("No upcoming collections");
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
