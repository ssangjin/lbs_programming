package com.example;

import com.example.daos.DatastoreDao;
import com.example.daos.LocationDataDao;
import com.example.data.LocationData;
import com.example.data.Result;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "RequestData", value = "/request_data")
public class RequestData extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("euc-kr");
        PrintWriter out = resp.getWriter();

        try {
            String userId = req.getParameter(LocationData.USER_ID);

            LocationDataDao dao = dao = new DatastoreDao();
            try {
                Result<LocationData> locationDataResult = dao.listLocationDatas(userId);
                for (LocationData location : locationDataResult.result) {
                    out.println(location.toString());
                }
            } catch (Exception e) {
                throw new ServletException("Error to get LocationData", e);
            }

        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }
}
