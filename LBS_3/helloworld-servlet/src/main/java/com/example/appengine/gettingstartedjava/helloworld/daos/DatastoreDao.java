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

package com.example.appengine.gettingstartedjava.helloworld.daos;

import com.example.appengine.gettingstartedjava.helloworld.data.LocationData;
import com.example.appengine.gettingstartedjava.helloworld.data.Result;
import com.google.appengine.api.datastore.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// [START example]
public class DatastoreDao implements LocationDataDao {

  // [START constructor]
  private DatastoreService datastore;
  private static final String LocationData_KIND = "LocationData";

  public DatastoreDao() {
    datastore = DatastoreServiceFactory.getDatastoreService(); // Authorized Datastore service
  }
  // [END constructor]

  // [START entityToLocationData]
  public LocationData entityToLocationData(Entity entity) {
    return new LocationData.Builder()                                     // Convert to LocationData form
            .userId((String) entity.getProperty(LocationData.USER_ID))
            .date((Date) entity.getProperty(LocationData.UPDATE_DATE))
            .latitude((Double) entity.getProperty(LocationData.LATITUDE))
            .longitude((Double) entity.getProperty(LocationData.LONGITUDE))
            .provider((String) entity.getProperty(LocationData.PROVIDER))
            .accuracy((Float) entity.getProperty(LocationData.ACCURACY))
            .id((Long) entity.getProperty(LocationData.ID))
            .build();
  }
  // [END entityToLocationData]

  // [START create]
  @Override
  public Long createLocationData(LocationData LocationData) {
    Entity incLocationDataEntity = new Entity(LocationData_KIND);  // Key will be assigned once written
    incLocationDataEntity.setProperty(LocationData.USER_ID, LocationData.getUserId());
    incLocationDataEntity.setProperty(LocationData.UPDATE_DATE, LocationData.getDate());
    incLocationDataEntity.setProperty(LocationData.LATITUDE, LocationData.getLatitude());
    incLocationDataEntity.setProperty(LocationData.LONGITUDE, LocationData.getLongitude());
    incLocationDataEntity.setProperty(LocationData.PROVIDER, LocationData.getProvider());
    incLocationDataEntity.setProperty(LocationData.ACCURACY, LocationData.getAccuracy());

    Key LocationDataKey = datastore.put(incLocationDataEntity); // Save the Entity
    return LocationDataKey.getId();                     // The ID of the Key
  }
  // [END create]

  // [START read]
  @Override
  public LocationData readLocationData(Long LocationDataId) {
    try {
      Entity LocationDataEntity = datastore.get(KeyFactory.createKey(LocationData_KIND, LocationDataId));
      return entityToLocationData(LocationDataEntity);
    } catch (EntityNotFoundException e) {
      return null;
    }
  }
  // [END read]

  // [START update]
  @Override
  public void updateLocationData(LocationData LocationData) {
    Key key = KeyFactory.createKey(LocationData_KIND, LocationData.getId());  // From a LocationData, create a Key
    Entity entity = new Entity(key);         // Convert LocationData to an Entity
    entity.setProperty(LocationData.USER_ID, LocationData.getUserId());
    entity.setProperty(LocationData.UPDATE_DATE, LocationData.getDate());
    entity.setProperty(LocationData.LATITUDE, LocationData.getLatitude());
    entity.setProperty(LocationData.LONGITUDE, LocationData.getLongitude());
    entity.setProperty(LocationData.PROVIDER, LocationData.getProvider());
    entity.setProperty(LocationData.ACCURACY, LocationData.getAccuracy());

    datastore.put(entity);                   // Update the Entity
  }
  // [END update]

  // [START delete]
  @Override
  public void deleteLocationData(Long LocationDataId) {
    Key key = KeyFactory.createKey(LocationData_KIND, LocationDataId);        // Create the Key
    datastore.delete(key);                      // Delete the Entity
  }
  // [END delete]

  // [START entitiesToLocationDatas]
  public List<LocationData> entitiesToLocationDatas(Iterator<Entity> results) {
    List<LocationData> resultLocationDatas = new ArrayList<>();
    while (results.hasNext()) {  // We still have data
      resultLocationDatas.add(entityToLocationData(results.next()));      // Add the LocationData to the List
    }
    return resultLocationDatas;
  }
  // [END entitiesToLocationDatas]

  // [START listLocationDatas]
  @Override
  public Result<LocationData> listLocationDatas(String startCursorString) {
    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(10); // Only show 10 at a time
    if (startCursorString != null && !startCursorString.equals("")) {
      fetchOptions.startCursor(Cursor.fromWebSafeString(startCursorString)); // Where we left off
    }
    Query query = new Query(LocationData_KIND) // We only care about LocationDatas
        .addSort(LocationData.UPDATE_DATE, Query.SortDirection.ASCENDING); // Use default Index "title"
    PreparedQuery preparedQuery = datastore.prepare(query);
    QueryResultIterator<Entity> results = preparedQuery.asQueryResultIterator(fetchOptions);

    List<LocationData> resultLocationDatas = entitiesToLocationDatas(results);     // Retrieve and convert Entities
    Cursor cursor = results.getCursor();              // Where to start next time
    if (cursor != null && resultLocationDatas.size() == 10) {         // Are we paging? Save Cursor
      String cursorString = cursor.toWebSafeString();               // Cursors are WebSafe
      return new Result<>(resultLocationDatas, cursorString);
    } else {
      return new Result<>(resultLocationDatas);
    }
  }
  // [END listLocationDatas]
}
// [END example]
