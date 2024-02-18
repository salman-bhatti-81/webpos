package com.wepos.authentication;


import com.wepos.authentication.dto.GenerateTokenRequest;
import com.wepos.authentication.dto.ItemsResponse;
import com.wepos.domain.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wepos.authentication.dto.GenerateTokenResponse;
import com.wepos.config.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;



import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;



@Service
public class AuthenticationService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Config config;

    private static final Logger LOGGER = LogManager.getLogger(AuthenticationService.class);

    public String getToken(String currentTime) {
        // Construct URL for getToken API
        String tokenUrl = config.getBaseUrl() + "/generateToken";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");
        headers.set("X-GIFTLOV-DATE", currentTime);

        GenerateTokenRequest request = new GenerateTokenRequest(config.getUsername(),config.getPassword());

        // Make HTTP request to getToken API
        ResponseEntity<GenerateTokenResponse> response = restTemplate.postForEntity(tokenUrl, request, GenerateTokenResponse.class);

        // Extract token from response
        GenerateTokenResponse authTokenResponse = response.getBody();
        String token = authTokenResponse.getToken();

        return token;
    }



    public String generateSignature(String method, String endpoint, Map<String, String> queryParams, String requestBody, String giftlovDate, String authToken) throws NoSuchAlgorithmException {
        // Sort request parameters
        List<String> paramValues = new ArrayList<>();
        paramValues.addAll(queryParams.values());
        if (requestBody != null && !requestBody.isEmpty()) {
            collectPrimitiveValues(requestBody, paramValues);
        }
        Collections.sort(paramValues);

        // Concatenate values
        StringBuilder concatenatedValues = new StringBuilder();
        for (String value : paramValues) {
            concatenatedValues.append(value);
        }

        // Construct signature string
        StringBuilder signatureString = new StringBuilder();
        int indexDifference = indexOfDifference(config.getBaseUrl(),endpoint);
        String requestURL = endpoint.substring(indexDifference+1);// +1 to start without /
        signatureString.append(requestURL).append(method).append(concatenatedValues).append(giftlovDate).append(authToken);

        // Hash signature using SHA-512 algorithm with API Encryption Key
        byte[] signatureBytes = hashString(signatureString.toString(), config.getApiSecret());
        return bytesToHex(signatureBytes);
    }

    public static int indexOfDifference(CharSequence cs1, CharSequence cs2) {
        if (cs1 == cs2) {
            return -1;
        }
        if (cs1 == null || cs2 == null) {
            return 0;
        }
        int i;
        for (i = 0; i < cs1.length() && i < cs2.length(); ++i) {
            if (cs1.charAt(i) != cs2.charAt(i)) {
                break;
            }
        }
        if (i < cs2.length() || i < cs1.length()) {
            return i;
        }
        return -1;
    }

    private static void collectPrimitiveValues(String requestBody, List<String> paramValues) {
        requestBody = requestBody.replaceAll("[{}\"]", ""); // Remove curly braces and quotes
        String[] parts = requestBody.split(",");
        for (String part : parts) {
            String[] keyValue = part.split(":");
            if (keyValue.length == 2) {
                paramValues.add(keyValue[1].trim());
            }
        }
    }

    private static byte[] hashString(String input, String key) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        digest.update((input + key).getBytes(StandardCharsets.UTF_8));
        return digest.digest();
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }



    public List<Item> getCatalog(Map<String,String> params){
        String currentDate = getCurrentTime();
        String token = this.getToken(currentDate);
        String signature = "";
        try {
            signature = this.generateSignature("GET",config.getBaseUrl() + "/items",params,"",currentDate,token);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        String url = config.getBaseUrl() + "/items";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");
        headers.set("X-GIFTLOV-DATE", currentDate);
        headers.set("Authorization", "Bearer " + token);
        headers.set("signature", signature);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
                .queryParam("rowCount", "{rowCount}")
                .queryParam("lang", "{lang}")
                .queryParam("current", "{current}")
                .encode()
                .toUriString();

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("rowCount", params.get("rowCount"));
        queryParams.put("lang", params.get("lang"));
        queryParams.put("current", params.get("current"));

        HttpEntity<ItemsResponse> response = restTemplate.exchange(
                urlTemplate,
                HttpMethod.GET,
                entity,
                ItemsResponse.class,
                queryParams
        );

        LOGGER.info(response.getBody());
        // Extract items from response
        List<Item> items = response.getBody().getItems();
        return items;
    }

    public static String  getCurrentTime(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyyHHmmss");
        return sdf.format(cal.getTime());
    }
}
