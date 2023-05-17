package myTest.api.test;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class test {


    public static void searchArr(String keyword) {
        String apiURL = "http://api.vworld.kr/req/address";

        try {
            int responseCode = 0;
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

//            String keyword = "서울 영등포구 영중로 134-1 문성빌딩 704호";
            String text_content = URLEncoder.encode(keyword.toString(), "utf-8");

            // post request
            String postParams = "service=address";
            postParams += "&request=getcoord";
            postParams += "&version=2.0";
            postParams += "&crs=EPSG:4326";
            postParams += "&address=" + text_content;
            postParams += "&arefine=true";
            postParams += "&simple=false";
            postParams += "&format=json";
            postParams += "&type=road";
            postParams += "&errorFormat=json";
            postParams += "&key=B4BEBCB2-2B94-38D5-88C3-72D2921ECAD7";

            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            responseCode = con.getResponseCode();
            BufferedReader br;

            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            con.disconnect();


            String jsonString = response.toString();


// Jackson ObjectMapper 객체 생성
            ObjectMapper objectMapper = new ObjectMapper();

// JSON 파싱
            JsonNode rootNode = objectMapper.readTree(jsonString);


            String x = rootNode.get("response").get("result").get("point").get("x").asText();
            String y = rootNode.get("response").get("result").get("point").get("y").asText();

            System.out.println("x: " + x);
            System.out.println("y: " + y);






        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilo") {
            dist = dist * 1.609344;
        } else if(unit == "meter"){
            dist = dist * 1609.344;
        }
        return (dist);
    }


    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

}