/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.daos;

import com.example.data.LocationData;
import com.example.data.Result;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// [START example]
public class CloudSqlDao implements LocationDataDao {
    // [START constructor]
    private String sqlUrl;

    /**
     * A data access object for LocationDatashelf using a Google Cloud SQL server for storage.
     */
    public CloudSqlDao(final String url) throws SQLException {

        sqlUrl = url;
        final String createTableSql = "CREATE TABLE IF NOT EXISTS LocationDatas ( id INT NOT NULL "
                + "AUTO_INCREMENT, userId VARCHAR(255), updateDate datetime, latitude float, "
                + "longitude float, provider VARCHAR(255), accuracy float, PRIMARY KEY (id))";
        try (Connection conn = DriverManager.getConnection(sqlUrl)) {
            conn.createStatement().executeUpdate(createTableSql);
        }
    }

    // [END constructor]
    // [START create]
    @Override
    public Long createLocationData(LocationData LocationData) throws SQLException {
        final String createLocationDataString = "INSERT INTO LocationDatas "
                + "(userId, updateDate, latitude, longitude, provider, accuracy) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(sqlUrl);
             final PreparedStatement createLocationDataStmt = conn.prepareStatement(createLocationDataString,
                     Statement.RETURN_GENERATED_KEYS)) {
            createLocationDataStmt.setString(1, LocationData.getUserId());
            createLocationDataStmt.setDate(2, LocationData.getSqlDate());
            createLocationDataStmt.setDouble(3, LocationData.getLatitude());
            createLocationDataStmt.setDouble(4, LocationData.getLongitude());
            createLocationDataStmt.setString(5, LocationData.getProvider());
            createLocationDataStmt.setFloat(6, LocationData.getAccuracy());
            createLocationDataStmt.executeUpdate();
            try (ResultSet keys = createLocationDataStmt.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    // [END create]
    // [START read]
    @Override
    public LocationData readLocationData(Long LocationDataId) throws SQLException {
        final String readLocationDataString = "SELECT * FROM LocationDatas WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(sqlUrl);
             PreparedStatement readLocationDataStmt = conn.prepareStatement(readLocationDataString)) {
            readLocationDataStmt.setLong(1, LocationDataId);
            try (ResultSet keys = readLocationDataStmt.executeQuery()) {
                keys.next();
                return new LocationData.Builder()
                        .userId(keys.getString(LocationData.USER_ID))
                        .sqlDate(keys.getDate(LocationData.UPDATE_DATE))
                        .latitude(keys.getDouble(LocationData.LATITUDE))
                        .longitude(keys.getDouble(LocationData.LONGITUDE))
                        .provider(keys.getString(LocationData.PROVIDER))
                        .accuracy(keys.getFloat(LocationData.ACCURACY))
                        .id(keys.getLong(LocationData.ID))
                        .build();
            }
        }
    }

    // [END read]
    // [START update]
    @Override // userId, updateDate, latitude, longitude, provider, accuracy
    public void updateLocationData(LocationData LocationData) throws SQLException {
        final String updateLocationDataString = "UPDATE LocationDatas SET userId = ?, updateDate = ?, latitude = ?, "
                + "longitude = ?, provider = ?, accuracy = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(sqlUrl);
             PreparedStatement updateLocationDataStmt = conn.prepareStatement(updateLocationDataString)) {
            updateLocationDataStmt.setString(1, LocationData.getUserId());
            updateLocationDataStmt.setDate(2, LocationData.getSqlDate());
            updateLocationDataStmt.setDouble(3, LocationData.getLatitude());
            updateLocationDataStmt.setDouble(4, LocationData.getLongitude());
            updateLocationDataStmt.setString(5, LocationData.getProvider());
            updateLocationDataStmt.setFloat(6, LocationData.getAccuracy());
            updateLocationDataStmt.setLong(7, LocationData.getId());
            updateLocationDataStmt.executeUpdate();
        }
    }

    // [END update]
    // [START delete]
    @Override
    public void deleteLocationData(Long LocationDataId) throws SQLException {
        final String deleteLocationDataString = "DELETE FROM LocationDatas WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(sqlUrl);
             PreparedStatement deleteLocationDataStmt = conn.prepareStatement(deleteLocationDataString)) {
            deleteLocationDataStmt.setLong(1, LocationDataId);
            deleteLocationDataStmt.executeUpdate();
        }
    }

    // [END delete]
    // [START listLocationDatas]
    @Override
    public Result<LocationData> listLocationDatas(String cursor) throws SQLException {
        int offset = 0;
        if (cursor != null && !cursor.equals("")) {
            offset = Integer.parseInt(cursor);
        }
        final String listLocationDatasString = "SELECT SQL_CALC_FOUND_ROWS author, createdBy, createdById, "
                + "description, id, publishedDate, title, imageUrl FROM LocationDatas ORDER BY title ASC "
                + "LIMIT 10 OFFSET ?";
        try (Connection conn = DriverManager.getConnection(sqlUrl);
             PreparedStatement listLocationDatasStmt = conn.prepareStatement(listLocationDatasString)) {
            listLocationDatasStmt.setInt(1, offset);
            List<LocationData> resultLocationDatas = new ArrayList<>();
            try (ResultSet rs = listLocationDatasStmt.executeQuery()) {
                while (rs.next()) {
                    LocationData locationData = new LocationData.Builder()
                            .userId(rs.getString(LocationData.USER_ID))
                            .sqlDate(rs.getDate(LocationData.UPDATE_DATE))
                            .latitude(rs.getDouble(LocationData.LATITUDE))
                            .longitude(rs.getDouble(LocationData.LONGITUDE))
                            .provider(rs.getString(LocationData.PROVIDER))
                            .accuracy(rs.getFloat(LocationData.ACCURACY))
                            .id(rs.getLong(LocationData.ID))
                            .build();

                    resultLocationDatas.add(locationData);
                }
            }
            try (ResultSet rs = conn.createStatement().executeQuery("SELECT FOUND_ROWS()")) {
                int totalNumRows = 0;
                if (rs.next()) {
                    totalNumRows = rs.getInt(1);
                }
                if (totalNumRows > offset + 10) {
                    return new Result<>(resultLocationDatas, Integer.toString(offset + 10));
                } else {
                    return new Result<>(resultLocationDatas);
                }
            }
        }
    }
    // [END listLocationDatas]
}
// [END example]
