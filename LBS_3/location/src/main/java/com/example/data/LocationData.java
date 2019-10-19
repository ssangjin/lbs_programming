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

package com.example.data;

import java.util.Date;

// [START example]
public class LocationData {
    // [START LocationData]
    private Long id;
    private String userId;
    private Double latitude;
    private Double longitude;
    private String provider;
    private Float accuracy;
    private Date date;

    // [END LocationData]
    // [START keys]
    public static final String ID = "id";
    public static final String USER_ID = "userId";
    public static final String UPDATE_DATE = "updateDate";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String PROVIDER = "provider";
    public static final String ACCURACY = "accuracy";
    // [END keys]

    // [START constructor]
    // We use a Builder pattern here to simplify and standardize construction of LocationData objects.
    private LocationData(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.provider = builder.provider;
        this.accuracy = builder.accuracy;
        this.date = builder.date;
    }

    public String getUserId() {
        return userId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getProvider() {
        return provider;
    }

    public Float getAccuracy() {
        return accuracy;
    }

    public Date getDate() {
        return date;
    }

    public Long getId() {
        return id;
    }

    public java.sql.Date getSqlDate() {
        return new java.sql.Date(date.getTime());
    }
    // [END constructor]

    // [START builder]
    public static class Builder {
        private Long id;
        private String userId;
        private Double latitude;
        private Double longitude;
        private String provider;
        private Float accuracy;
        private Date date = new Date(System.currentTimeMillis());

        public LocationData build() {
            return new LocationData(this);
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder latitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder longitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder provider(String provider) {
            this.provider = provider;
            return this;
        }

        public Builder accuracy(Float accuracy) {
            this.accuracy = accuracy;
            return this;
        }

        public Builder date(Date date) {
            this.date = date;
            return this;
        }

        public Builder sqlDate(java.sql.Date sqldate) {
            this.date = new Date(sqldate.getTime());
            return this;
        }
    }

    // [END builder]
    @Override
    public String toString() {
        return
                "UserId: " + userId + ", latitude: " + latitude + ", longitude: " + longitude
                        + ", provider: " + provider + ", accuracy: " + accuracy;
    }
}
// [END example]
