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

@WebServlet("/reset-password")
public class ResetPasswordServlet extends HttpServlet {
    private static final String updateQuery = "UPDATE user SET password = ? WHERE email = ?";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        PrintWriter pw = res.getWriter();
        res.setContentType("text/html");

        String email = req.getParameter("email");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        if (!newPassword.equals(confirmPassword)) {
            pw.println("<h2>Passwords do not match. Please try again.</h2>");
            pw.println("<a href='forgot-password.html'>Go back</a>");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException cnf) {
            cnf.printStackTrace();
        }

        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/book", "root", "root");
             PreparedStatement ps = con.prepareStatement(updateQuery)) {

            ps.setString(1, newPassword);
            ps.setString(2, email);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                pw.println("<h2>Password reset successfully!</h2>");
                pw.println("<a href='login.html'>Login</a>");
            } else {
                pw.println("<h2>Failed to reset password. Please try again later.</h2>");
                pw.println("<a href='forgot-password.html'>Go back</a>");
            }
        } catch (SQLException se) {
            se.printStackTrace();
            pw.println("<h1>" + se.getMessage() + "</h1>");
        } catch (Exception e) {
            e.printStackTrace();
            pw.println("<h1>" + e.getMessage() + "</h1>");
        }
    }
}
