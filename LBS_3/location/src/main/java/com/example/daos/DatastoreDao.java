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
import com.google.appengine.api.datastore.*;

import java.util.Date;
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
        double d = (Double) entity.getProperty(LocationData.ACCURACY);
        float f = (float) d;

        return new LocationData.Builder()                                     // Convert to LocationData form
                .userId((String) entity.getProperty(LocationData.USER_ID))
                .date((Date) entity.getProperty(LocationData.UPDATE_DATE))
                .latitude((Double) entity.getProperty(LocationData.LATITUDE))
                .longitude((Double) entity.getProperty(LocationData.LONGITUDE))
                .provider((String) entity.getProperty(LocationData.PROVIDER))
                .accuracy((Float) f)
                .id((Long) entity.getProperty(LocationData.ID))
                .build();
    }
    // [END entityToLocationData]

    // [START create]
    @Override
    public Long createLocationData(LocationData locationData) {
        Entity incLocationDataEntity = new Entity(LocationData_KIND);  // Key will be assigned once written
        incLocationDataEntity.setProperty(LocationData.USER_ID, locationData.getUserId());
        incLocationDataEntity.setProperty(LocationData.UPDATE_DATE, locationData.getDate());
        incLocationDataEntity.setProperty(LocationData.LATITUDE, locationData.getLatitude());
        incLocationDataEntity.setProperty(LocationData.LONGITUDE, locationData.getLongitude());
        incLocationDataEntity.setProperty(LocationData.PROVIDER, locationData.getProvider());
        incLocationDataEntity.setProperty(LocationData.ACCURACY, locationData.getAccuracy());

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
    public Result<LocationData> listLocationDatas(String userId) {
        Query.Filter keyFilter = new Query.FilterPredicate(LocationData.USER_ID, Query.FilterOperator.EQUAL, userId);
        Query query = new Query(LocationData_KIND) // We only care about LocationDatas
                .setFilter(keyFilter)
                .addSort(LocationData.UPDATE_DATE, Query.SortDirection.ASCENDING); // Use default Index "title"

        PreparedQuery preparedQuery = datastore.prepare(query);

        List<LocationData> resultLocationDatas = entitiesToLocationDatas(preparedQuery.asIterator());     // Retrieve and convert Entities
        return new Result<>(resultLocationDatas);
    }
    // [END listLocationDatas]
}
// [END example]
