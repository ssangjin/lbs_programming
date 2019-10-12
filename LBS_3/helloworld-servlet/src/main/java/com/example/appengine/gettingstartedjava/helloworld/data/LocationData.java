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

package com.example.appengine.gettingstartedjava.helloworld.data;

import java.util.ArrayList;
import java.util.List;

// [START example]
public class LocationData {
  // [START LocationData]
  private String userId;
  private Double latitude;
  private Double longitude;
  private String provider;
  private Float accuracy;
  private List<WifiApData> wifiApDataList;
  // [END LocationData]
  // [START keys]
  public static final String USER_ID = "userId";
  public static final String UPDATE_DATE = "updateDate";
  public static final String LATITUDE = "latitude";
  public static final String LONGITUDE = "longitude";
  public static final String PROVIDER = "provider";
  public static final String ACCURACY = "accuracy";
  public static final String WIFI_DATA_LIST = "wifiApDataList";
  // [END keys]

  // [START constructor]
  // We use a Builder pattern here to simplify and standardize construction of LocationData objects.
  private LocationData(Builder builder) {
    this.userId = builder.userId;
    this.latitude = builder.latitude;
    this.longitude = builder.longitude;
    this.provider = builder.provider;
    this.accuracy = builder.accuracy;
    this.wifiApDataList = new ArrayList<>(builder.wifiApDataList);
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

  public List<WifiApData> getWifiApDataList() {
    return wifiApDataList;
  }
  // [END constructor]

  // [START builder]
  public static class Builder {
    private String userId;
    private Double latitude;
    private Double longitude;
    private String provider;
    private Float accuracy;
    private List<WifiApData> wifiApDataList;

    public LocationData build() {
      return new LocationData(this);
    }

    public void setUserId(String userId) {
      this.userId = userId;
    }

    public void setLatitude(Double latitude) {
      this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
      this.longitude = longitude;
    }

    public void setProvider(String provider) {
      this.provider = provider;
    }

    public void setAccuracy(Float accuracy) {
      this.accuracy = accuracy;
    }

    public void setWifiApDataList(List<WifiApData> wifiApDataList) {
      this.wifiApDataList = wifiApDataList;
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
