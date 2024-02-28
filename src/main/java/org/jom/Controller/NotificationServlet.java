package org.jom.Controller;

import com.google.gson.Gson;
import org.jom.Auth.JwtUtils;
import org.jom.Dao.NotificationDAO;
import org.jom.Dao.UserDAO;
import org.jom.Model.NotificationModel;
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

@WebServlet("/notification")
public class NotificationServlet extends HttpServlet {
    //Get Notifications
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
                if (role.equals("supplier") || role.equals("collector") || role.equals("stock-manager") || role.equals("production-manager") || role.equals("sales-manager") || role.equals("distributor") || role.equals("admin")) {

                    NotificationDAO notificationDAO = new NotificationDAO();
                    List<NotificationModel> notifications = notificationDAO.getNotifications(user_id);

                    Gson gson = new Gson();
                    String objectArray = gson.toJson(notifications); // Object array to json

                    if (notifications.size() != 0) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"notifications\": " + objectArray + "}");
                        System.out.println("Send notifications");
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"notifications\": \"0\"}");
                        System.out.println("No notifications");
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

    //Update seen status Notifications
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
            StringBuilder requestBody = new StringBuilder();

            try (BufferedReader reader = request.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    requestBody.append(line);
                }
            }

            JSONObject jsonData = new JSONObject(requestBody.toString());
            JSONArray notificationsArray = jsonData.getJSONArray("notifications");
            int[] notifications = new int[notificationsArray.length()];

            for (int i = 0; i < notificationsArray.length(); i++) {
                notifications[i] = notificationsArray.getInt(i);
            }

            if (user_id != 0) {
                if (role.equals("supplier") || role.equals("collector") || role.equals("stock-manager") || role.equals("production-manager") || role.equals("sales-manager") || role.equals("distributor") || role.equals("admin")) {

                    boolean status = false;
                    NotificationDAO notificationDAO = new NotificationDAO();
                    for (int i = 0; i < notificationsArray.length(); i++) {
                        status = notificationDAO.updateSeenStatus(user_id, notifications[i]);
                        if (status == false) break;
                    }

                    if (status) {
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{\"message\": \"Seen\"}");
                        System.out.println("Notifications seen");
                    } else {
                        response.setStatus(HttpServletResponse.SC_ACCEPTED);
                        out.write("{\"message\": \"Haven't seen\"}");
                        System.out.println("Notifications haven't seen");
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
