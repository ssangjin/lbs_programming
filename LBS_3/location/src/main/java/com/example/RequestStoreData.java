package com.example;

import com.example.daos.DatastoreDao;
import com.example.daos.LocationDataDao;
import com.example.data.LocationData;
import com.example.data.Result;
import com.google.appengine.repackaged.com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@WebServlet(name = "RequestStoreData", value = "/request_store_data")
public class RequestStoreData extends HttpServlet {
    Connection conn;

    @Override
    public void init() throws ServletException {
        String url = System.getProperty("cloudsql");

        // Server Deploy 설정
//        log("connecting to: " + url);
//        try {
//            conn = DriverManager.getConnection(url);
//        } catch (SQLException e) {
//            throw new ServletException("Unable to connect to Cloud SQL", e);
//        }

        // Local 설정
        url = "jdbc:mysql://localhost:3306/lbs?user=root";
        log("connecting to: " + url);
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new ServletException("Unable to connect to Cloud SQL", e);
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("euc-kr");
        PrintWriter out = resp.getWriter();
        Gson gson = new Gson();

        String code = req.getParameter("code");
        Double lat = Double.parseDouble(req.getParameter("lat"));
        Double lon = Double.parseDouble(req.getParameter("lon"));

        String sql = "select * from StoreInfromation \n" +
                "where commercial_type_classification_code1 = ? \n" +
                "and longitude > ? and longitude < ? \n" +
                "and latitude > ? and latitude < ? limit 500\n";
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, code);
            pstmt.setDouble(2, lon - 0.1);
            pstmt.setDouble(3, lon + 0.1);
            pstmt.setDouble(4, lat - 0.1);
            pstmt.setDouble(5, lat + 0.1);

            Stopwatch stopwatch = Stopwatch.createStarted();
            try (ResultSet rs = pstmt.executeQuery()) {
                stopwatch.stop();

                JsonArray jsonArray = new JsonArray();

                while (rs.next()) {
                    StoreInfo storeInfo = new StoreInfo();

                    storeInfo.name = rs.getString("trade_name");
                    storeInfo.className = rs.getString("\"commercial type classification code2\"");
                    storeInfo.businessName = rs.getString("\"Commercial business classification name\"");
                    storeInfo.industrialName = rs.getString("\"standard industrial classification name\"");
                    storeInfo.latitude = rs.getDouble("latitude");
                    storeInfo.longitude = rs.getDouble("longitude");

                    jsonArray.add(gson.toJsonTree(storeInfo));
                }

                out.println(jsonArray.toString());

            } catch (SQLException ex) {
                throw new ServletException("SQL error", ex);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static class StoreInfo {
        String name;
        String className;
        String businessName;
        String industrialName;
        Double latitude;
        Double longitude;
    }
}
