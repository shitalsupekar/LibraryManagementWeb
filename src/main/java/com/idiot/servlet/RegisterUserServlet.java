package com.idiot.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/registeruser")
public class RegisterUserServlet extends HttpServlet {
    private static final String checkEmailQuery = "SELECT COUNT(*) FROM user WHERE email=?";
    private static final String insertUserQuery = "INSERT INTO user(name, email, mobile, dob, city, gender, password) VALUES (?, ?, ?, ?, ?, ?, ?)";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        // get PrintWriter
        PrintWriter pw = res.getWriter();
        // set content type
        res.setContentType("text/html");

        // GET THE user info
        String userName = req.getParameter("userName");
        String email = req.getParameter("email");
        String mobile = req.getParameter("mobile");
        String dobStr = req.getParameter("dob");
        String city = req.getParameter("city");
        String gender = req.getParameter("gender");
        String password = req.getParameter("password");

        // Parse date string to java.util.Date
        java.util.Date dob = null;
        try {
            dob = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dobStr);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        // LOAD JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException cnf) {
            cnf.printStackTrace();
        }

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/book", "root", "root");
             PreparedStatement checkEmailStmt = con.prepareStatement(checkEmailQuery);
             PreparedStatement insertUserStmt = con.prepareStatement(insertUserQuery)) {

            // Check if the email already exists
            checkEmailStmt.setString(1, email);
            ResultSet emailResult = checkEmailStmt.executeQuery();

            if (emailResult.next() && emailResult.getInt(1) > 0) {
                pw.println("<h2>Email already registered. Please use a different email address.</h2>");
            } else {
                // Insert the new user if the email is not found
                insertUserStmt.setString(1, userName);
                insertUserStmt.setString(2, email);
                insertUserStmt.setString(3, mobile);
                insertUserStmt.setDate(4, new java.sql.Date(dob.getTime())); // Convert java.util.Date to java.sql.Date
                insertUserStmt.setString(5, city);
                insertUserStmt.setString(6, gender);
                insertUserStmt.setString(7, password);

                int count = insertUserStmt.executeUpdate();
                if (count == 1) {
                    pw.println("<script>alert('Registration Successful!');</script>");
                    res.sendRedirect("login.html");
                } else {
                    pw.println("<h2>Record not Registered Successfully</h2>");
                }
            }
        } catch (SQLException se) {
            se.printStackTrace();
            pw.println("<h1>" + se.getMessage() + "</h1>");
        } catch (Exception e) {
            e.printStackTrace();
            pw.println("<h1>" + e.getMessage() + "</h1>");
        }

        pw.println("<a href='register.html'>Create Account</a>");
       
    }
}
