// reviewed and added notes on 2024.07.14

package com.luv2code.spring_boot_library.utils;

import com.google.gson.Gson;
import java.util.Base64;
import java.util.Map;
import java.util.List;
import java.util.Collections;

public class ExtractJWT {

    public static String payloadJWTExtraction(String token, String extraction) {
        // Remove "Bearer " and split the Token
        token = token.replace("Bearer ", "");
        String[] chunks = token.split("\\.");
        // Decode the Payload and convert it to a string
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        // Parse the payload. Gson: JSON parsing
        Gson gson = new Gson();
        // Parses the decoded payload string into a 'Map' where the keys are String and the values are Object
        Map<String, Object> payloadMap = gson.fromJson(payload, Map.class);
        // extract the desired value
        String emailKey = extraction;
        if (payloadMap.containsKey(emailKey)) {
            String email = (String) payloadMap.get(emailKey);
            return email;
        }
        return null;
    }

    // to get List type - to get roles (admin)
    public static List<String> extractRolesFromJWT(String token, String key) {
        // Remove "Bearer " if present
        token = token.replace("Bearer ", "");
        String[] parts = token.split("\\.");
        // optimization: Not enough parts in token
        if (parts.length < 2) {
            return Collections.emptyList();
        }
        // Decode the payload and convert to a string
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payloadJson = new String(decoder.decode(parts[1]));
        // Parse the payload into a map.
        Gson gson = new Gson();
        Map<String, Object> payloadMap = gson.fromJson(payloadJson, Map.class);
        Object value = payloadMap.get(key);
        if (value instanceof List) {
            return (List<String>) value;
        }
        // Return empty list if not a list or key does not exist
        return Collections.emptyList();
    }
}
