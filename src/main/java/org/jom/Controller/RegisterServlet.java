package org.jom.Controller;

import org.jom.Model.UserModel;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@WebServlet("/signup")
public class RegisterServlet extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");

        String first_name = request.getParameter("fname");
        String last_name = request.getParameter("lname");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phone = request.getParameter("phone");
        String address1 = request.getParameter("address1");
        String address2 = request.getParameter("address2");
        String address3 = request.getParameter("address3");

        System.out.println(first_name);
        System.out.println(last_name);
        System.out.println(email);
        System.out.println(password);
        System.out.println(phone);
        System.out.println(address1);
        System.out.println(address2);
        System.out.println(address3);

        UserModel user = new UserModel(first_name,last_name,email,password,phone,address1,address2,address3);

        if(user.Register()){
            RequestDispatcher dis = request.getRequestDispatcher("http://127.0.0.1:5501/signup/signup2.html");
            try {
                dis.forward(request, response);
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        }else{
            RequestDispatcher dis2 = request.getRequestDispatcher("http://127.0.0.1:5501/signup/signup1.html");
            try {
                dis2.forward(request, response);
            } catch (ServletException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
