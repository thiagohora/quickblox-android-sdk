package com.quickblox.snippets.modules;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.internal.core.exception.QBResponseException;
import com.quickblox.internal.core.helper.FileHelper;
import com.quickblox.internal.module.content.Consts;
import com.quickblox.module.locations.QBLocations;
import com.quickblox.module.locations.model.QBLocation;
import com.quickblox.module.locations.model.QBPlace;
import com.quickblox.module.locations.request.QBLocationRequestBuilder;
import com.quickblox.snippets.AsyncSnippet;
import com.quickblox.snippets.R;
import com.quickblox.snippets.Snippet;
import com.quickblox.snippets.Snippets;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vfite on 10.02.14.
 */
public class SnippetsLocationsNew extends Snippets {
    private static final String TAG = SnippetsLocations.class.getSimpleName();

    // Coordinates for follow points
    // First point is SW, second is NE

    // SW point to build rect
    double borderPointLat1 = 49.990106;
    double borderPointLng1 = 36.185703;

    // NE point to build rect
    double borderPointLat2 = 50.043934;
    double borderPointLng2 = 36.28767;


    // Center point for radius area
    public static double lat1 = 50.010431;
    public static double lng1 = 36.224327;

    public static double lat2 = 50.004694;
    public static double lng2 = 36.240807;

    int distanceInMeters = 1338;

    public SnippetsLocationsNew(Context context) {
        super(context);

        snippets.add(createLocation);
        snippets.add(createLocationSync);
        snippets.add(getLocationWithId);
        snippets.add(getLocationWithIdSync);
        snippets.add(updateLocation);
        snippets.add(deleteLocationWithId);

        snippets.add(getLocations);
        snippets.add(getLocationWithFilters);
        snippets.add(getLocationWithFiltersSync);
        snippets.add(deleteLocations);

        snippets.add(createPlace);
        snippets.add(getPlaceWithId);
        snippets.add(updatePlace);
        snippets.add(deletePlace);
        snippets.add(getPlaces);
        snippets.add(getPlacesSync);
        snippets.add(createPlaceTask);
    }


    //
    ///////////////////////////////////////////// Location /////////////////////////////////////////////
    //
    Snippet createLocation = new Snippet("create location") {
        @Override
        public void execute() {
            final QBLocation location = new QBLocation(35, 35, "hello");

            QBLocations.createLocation(location, new QBEntityCallbackImpl<QBLocation>() {

                @Override
                public void onSuccess(QBLocation qbLocation, Bundle args) {
                    Log.i(TAG, ">>> Location is: " + qbLocation);
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet createLocationSync = new AsyncSnippet("create location synchronous", context) {
        @Override
        public void executeAsync() {
            final QBLocation location = new QBLocation(35, 35, "hello");
            QBLocation locationResult = null;
            try {
                locationResult = QBLocations.createLocation(location);
            } catch (QBResponseException e) {
                setException(e);
            }
            if (locationResult != null) {
                Log.i(TAG, ">>> Location is: " + locationResult);
            }
        }
    };

    Snippet getLocationWithId = new Snippet("get location with id") {
        @Override
        public void execute() {
            QBLocation location = new QBLocation(11308);
            QBLocations.getLocation(location, new QBEntityCallbackImpl<QBLocation>() {

                @Override
                public void onSuccess(QBLocation qbLocation, Bundle args) {
                    Log.i(TAG, ">>> Location is: " + qbLocation);
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet getLocationWithIdSync = new AsyncSnippet("get location with id synchronous", context) {
        @Override
        public void executeAsync() {
            QBLocation location = new QBLocation(11308);
            QBLocation locationResult = null;
            try {
                locationResult = QBLocations.getLocation(location);
            } catch (QBResponseException e) {
                setException(e);
            }
            if (locationResult != null) {
                Log.i(TAG, ">>> Location is: " + locationResult);
            }
        }
    };

    Snippet updateLocation = new Snippet("update location") {
        @Override
        public void execute() {
            QBLocation qbLocation = new QBLocation();
            qbLocation.setId(89895);
            qbLocation.setStatus("I'am at Pizza");
            QBLocations.updateLocation(qbLocation, new QBEntityCallbackImpl<QBLocation>() {
                @Override
                public void onSuccess(QBLocation qbLocation, Bundle args) {
                    Log.i(TAG, ">>> Location is: " + qbLocation);
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet deleteLocationWithId = new Snippet("delete location with id") {
        @Override
        public void execute() {
            QBLocation location = new QBLocation(11308);

            QBLocations.deleteLocation(location, new QBEntityCallbackImpl<Void>() {

                @Override
                public void onSuccess() {
                    Log.i(TAG, ">>> Delete location OK ");
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet getLocations = new Snippet("get locations") {
        @Override
        public void execute() {
            QBLocationRequestBuilder qbLocationRequestBuilder = new QBLocationRequestBuilder();
            qbLocationRequestBuilder.setPerPage(10);
            qbLocationRequestBuilder.setPage(1);

            QBLocations.getLocations(qbLocationRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBLocation>>() {

                @Override
                public void onSuccess(ArrayList<QBLocation> locations, Bundle params) {
                    Log.i(TAG, ">>> Locations:" + locations.toString());
                    Log.i(TAG, ">>> currentPage: " + params.getInt(Consts.CURR_PAGE));
                    Log.i(TAG, ">>> perPage: " + params.getInt(Consts.PER_PAGE));
                    Log.i(TAG, ">>> totalPages: " + params.getInt(Consts.TOTAL_ENTRIES));
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet getLocationWithFilters = new Snippet("get locations with filters") {
        @Override
        public void execute() {
            QBLocationRequestBuilder locationRequestBuilder = new QBLocationRequestBuilder();
//            locationRequestBuilder.setCreatedAt(1326471371);
//            locationRequestBuilder.setUserId(8330);
//            locationRequestBuilder.setUserIds(8330, 53779, 55022);
//            locationRequestBuilder.setUserName("testUser");
//            locationRequestBuilder.setUserExternalIds("987", "123456");
//            locationRequestBuilder.setMinCreatedAt(1326471371);
//            locationRequestBuilder.setMaxCreatedAt(1326471371);
//            locationRequestBuilder.setGeoRect(borderPointLat1, borderPointLng1, borderPointLat2, borderPointLng2);
            locationRequestBuilder.setRadius(lat1, lng1, distanceInMeters);
            //locationRequestBuilder.setRadius(lat1, lng1, 0.3f);
//
//            locationRequestBuilder.setSort(SortField.CREATED_AT, SortOrder.ASCENDING);
//            locationRequestBuilder.setSort(SortField.LATITUDE, SortOrder.ASCENDING);
//            locationRequestBuilder.setSort(SortField.LATITUDE, SortOrder.DESCENDING);

//            locationRequestBuilder.setLastOnly();
//            locationRequestBuilder.setHasStatus();
//            locationRequestBuilder.setCurrentPosition(lat1, lng1);
            locationRequestBuilder.setPage(1);
            locationRequestBuilder.setPerPage(10);


            QBLocations.getLocations(locationRequestBuilder, new QBEntityCallbackImpl<ArrayList<QBLocation>>() {

                @Override
                public void onSuccess(ArrayList<QBLocation> locations, Bundle params) {
                    Log.i(TAG, ">>> Locations:" + locations.toString());
                    Log.i(TAG, ">>> currentPage: " + params.getInt(Consts.CURR_PAGE));
                    Log.i(TAG, ">>> perPage: " + params.getInt(Consts.PER_PAGE));
                    Log.i(TAG, ">>> totalPages: " + params.getInt(Consts.TOTAL_ENTRIES));
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet getLocationWithFiltersSync = new AsyncSnippet("get locations with filters synchronous", context) {
        @Override
        public void executeAsync() {
            QBLocationRequestBuilder locationRequestBuilder = new QBLocationRequestBuilder();
//            locationRequestBuilder.setCreatedAt(1326471371);
//            locationRequestBuilder.setUserId(8330);
//            locationRequestBuilder.setUserIds(8330, 53779, 55022);
//            locationRequestBuilder.setUserName("testUser");
//            locationRequestBuilder.setUserExternalIds("987", "123456");
//            locationRequestBuilder.setMinCreatedAt(1326471371);
//            locationRequestBuilder.setMaxCreatedAt(1326471371);
//            locationRequestBuilder.setGeoRect(borderPointLat1, borderPointLng1, borderPointLat2, borderPointLng2);
            locationRequestBuilder.setRadius(lat1, lng1, distanceInMeters);
            //locationRequestBuilder.setRadius(lat1, lng1, 0.3f);
//
//            locationRequestBuilder.setSort(SortField.CREATED_AT, SortOrder.ASCENDING);
//            locationRequestBuilder.setSort(SortField.LATITUDE, SortOrder.ASCENDING);
//            locationRequestBuilder.setSort(SortField.LATITUDE, SortOrder.DESCENDING);

//            locationRequestBuilder.setLastOnly();
//            locationRequestBuilder.setHasStatus();
//            locationRequestBuilder.setCurrentPosition(lat1, lng1);
            locationRequestBuilder.setPage(1);
            locationRequestBuilder.setPerPage(10);

            Bundle params = new Bundle();
            ArrayList<QBLocation> locations = null;
            try {
                locations = QBLocations.getLocations(locationRequestBuilder, params);
            } catch (QBResponseException e) {
                setException(e);
            }
            if (locations != null) {
                Log.i(TAG, ">>> Locations:" + locations.toString());
                Log.i(TAG, ">>> currentPage: " + params.getInt(Consts.CURR_PAGE));
                Log.i(TAG, ">>> perPage: " + params.getInt(Consts.PER_PAGE));
                Log.i(TAG, ">>> totalPages: " + params.getInt(Consts.TOTAL_ENTRIES));
            }
        }
    };

    Snippet deleteLocations = new Snippet("delete locations") {
        @Override
        public void execute() {
            QBLocations.deleteObsoleteLocations(2, new QBEntityCallbackImpl<Void>() {

                @Override
                public void onSuccess() {
                    Log.i(TAG, ">>> Delete locations OK ");
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    //
    ///////////////////////////////////////////// Places /////////////////////////////////////////////
    //
    Snippet createPlace = new Snippet("create place") {
        @Override
        public void execute() {
            QBPlace place = new QBPlace();
            place.setDescription("asdasd");
            place.setAddress("asdad");
            place.setLocationId(88973);
            place.setTitle("the best place on the planet");
            place.setPhotoId(20012);

            QBLocations.createPlace(place, placeQBEntityCallback);
        }
    };

    QBEntityCallback<QBPlace> placeQBEntityCallback =new QBEntityCallbackImpl<QBPlace>() {

        @Override
        public void onSuccess(QBPlace qbPlace, Bundle params) {
            Log.i(TAG, ">> Place: " + qbPlace);
        }

        @Override
        public void onError(List<String> errors) {
            handleErrors(errors);
        }
    };

    Snippet getPlaceWithId = new Snippet("get place") {
        @Override
        public void execute() {
            QBPlace place = new QBPlace(1832);

            QBLocations.getPlace(place, placeQBEntityCallback);
        }
    };

    Snippet updatePlace = new Snippet("update place") {
        @Override
        public void execute() {

            QBPlace place = new QBPlace();
            place.setId(1832);
            place.setTitle("Great title");

            QBLocations.updatePlace(place, placeQBEntityCallback);
        }
    };

    Snippet deletePlace = new Snippet("delete place") {
        @Override
        public void execute() {
            QBPlace place = new QBPlace(1832);

            QBLocations.deletePlace(place, new QBEntityCallbackImpl<Void>(){
                @Override
                public void onSuccess() {
                    Log.i(TAG, ">> Place was deleted");
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet getPlaces = new Snippet("get places") {
        @Override
        public void execute() {
            QBLocations.getPlaces(new QBEntityCallbackImpl<ArrayList<QBPlace>>() {

                @Override
                public void onSuccess(ArrayList<QBPlace> qbPlaces, Bundle args) {
                    Log.i(TAG, ">>> Places:" + qbPlaces);
                    Log.i(TAG, ">>> currentPage: " + args.getInt(Consts.CURR_PAGE));
                    Log.i(TAG, ">>> perPage: " + args.getInt(Consts.PER_PAGE));
                    Log.i(TAG, ">>> totalPages: " + args.getInt(Consts.TOTAL_ENTRIES));
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet getPlacesSync = new AsyncSnippet("get places synchronous", context) {
        @Override
        public void executeAsync() {
            Bundle params = new Bundle();
            ArrayList<QBPlace> places = null;
            try {
                places = QBLocations.getPlaces(params);
            } catch (QBResponseException e) {
               setException(e);
            }
            if (places != null) {
                Log.i(TAG, ">>> Places:" + places.toString());
                Log.i(TAG, ">>> currentPage: " + params.getInt(Consts.CURR_PAGE));
                Log.i(TAG, ">>> perPage: " + params.getInt(Consts.PER_PAGE));
                Log.i(TAG, ">>> totalPages: " + params.getInt(Consts.TOTAL_ENTRIES));
            }
        }
    };

    //
    ///////////////////////////////////////////// Tasks /////////////////////////////////////////////
    //
    Snippet createPlaceTask = new Snippet("create place task") {
        @Override
        public void execute() {
            String placeTitle = "Kharkov city - all the best!";
            String placeDescription = "place description";
            String placeAddress = "Ukraine, Kharkov";
            double placeLongitude = -1.23;
            double placeLatitude = 1.23;

            int fileId = R.raw.kharkov;
            InputStream is = context.getResources().openRawResource(fileId);
            File placePhoto = FileHelper.getFileInputStream(is, "kharkov.jpg", "qb_snippets12");

            QBLocations.createPlaceTask(placeTitle, placeDescription, placeAddress, placeLongitude, placeLatitude, placePhoto, placeQBEntityCallback);
        }
    };
}
