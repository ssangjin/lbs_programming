package com.example;

import com.example.daos.DatastoreDao;
import com.example.daos.LocationDataDao;
import com.example.data.LocationData;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "CollectData", value = "/collect_data")
public class CollectData extends HttpServlet {
    static LocationData locationData = null;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        resp.setCharacterEncoding("euc-kr");
        PrintWriter out = resp.getWriter();

        try {
            // [START LocationDataBuilder]
            locationData = new LocationData.Builder()
                    .userId(req.getParameter(LocationData.USER_ID))   // form parameter
                    .latitude(Double.parseDouble(req.getParameter(LocationData.LATITUDE)))
                    .longitude(Double.parseDouble(req.getParameter(LocationData.LONGITUDE)))
                    .provider(req.getParameter(LocationData.PROVIDER))
                    .accuracy(Float.parseFloat(req.getParameter(LocationData.ACCURACY)))
                    .build();
            // [END LocationDataBuilder]

            LocationDataDao dao = dao = new DatastoreDao();
            try {
                Long id = dao.createLocationData(locationData);
                out.println("Data created. ID = " + id.toString() + " LocationData:" + locationData.toString());
            } catch (Exception e) {
                throw new ServletException("Error creating LocationData", e);
            }

        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    public LocationData getLocationData() {
        return locationData;
    }
}
