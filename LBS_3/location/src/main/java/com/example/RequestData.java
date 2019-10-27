package com.example;

import com.example.daos.DatastoreDao;
import com.example.daos.LocationDataDao;
import com.example.data.LocationData;
import com.example.data.Result;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
        Gson gson = new Gson();

        try {
            String userId = req.getParameter(LocationData.USER_ID);

            LocationDataDao dao = dao = new DatastoreDao();
            try {
                JsonArray jsonArray = new JsonArray();
                Result<LocationData> locationDataResult = dao.listLocationDatas(userId);
                for (LocationData location : locationDataResult.result) {
                    jsonArray.add(gson.toJsonTree(location));
                }
                out.println(jsonArray.toString());
            } catch (Exception e) {
                throw new ServletException("Error to get LocationData", e);
            }

        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }
}
