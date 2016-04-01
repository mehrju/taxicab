package com.xc0ffeelabs.taxicab.network;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseObject;

import java.util.HashMap;
import java.util.Map;

public class ParseEndPoints {

    private static final String USER_ID = "userId";
    private static final String DRIVER_IDS = "driverId";

    public static void initiateTrip(String userId,
                                    String driverId,
                                    FunctionCallback<ParseObject> response) {
        Map<String, String> request = new HashMap<>();
        request.put(USER_ID, userId);
        request.put(DRIVER_IDS, driverId);
        ParseCloud.callFunctionInBackground("initiateTrip", request, response);
    }
}
