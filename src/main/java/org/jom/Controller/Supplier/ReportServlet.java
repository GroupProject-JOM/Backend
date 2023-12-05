package org.jom.Controller.Supplier;

import com.google.gson.Gson;
import org.jom.Dao.CocoRateDAO;
import org.jom.Dao.Supplier.Collection.SupplyDAO;
import org.jom.Dao.UserDAO;
import org.jom.Model.CocoModel;
import org.jom.Model.Collection.SupplyModel;
import org.jom.Model.UserModel;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/report")
public class ReportServlet extends HttpServlet {
    // get report page content to supplier
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        int user_id = Integer.parseInt(request.getParameter("user"));

        try {
            UserDAO userDAO = new UserDAO();
            UserModel user = userDAO.getUserById(user_id);

            if (user.getId() != 0) {
                if (user.getRole().equals("supplier")) {
                    CocoRateDAO cocoRateDAO = new CocoRateDAO();
                    CocoModel cocoRate = cocoRateDAO.getLastRecord();
                    List<CocoModel> last_six_records = cocoRateDAO.getLastSixMonthRecords();
                    List<Float> average_list = cocoRateDAO.getMonthlyAverageRate();

                    if (cocoRate.getId() != 0) {
                        Gson gson = new Gson();
                        String object = gson.toJson(cocoRate); // Object array to json
                        String last_six = gson.toJson(last_six_records);
                        String avg = gson.toJson(average_list);

                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"rate\": " + object + ",\"last_six\":" + last_six + ",\"size\":" + last_six_records.size() + ",\"avg\":" + avg + ",\"avg_size\":" + average_list.size() + "}");
                        System.out.println("Send rate");
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"rate\": \"No rates\"}");
                        System.out.println("No rates");
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
