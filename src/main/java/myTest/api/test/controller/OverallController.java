package myTest.api.test.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import myTest.api.test.domain.Overall;
import myTest.api.test.domain.Sido;
import myTest.api.test.repository.OverallRepository;
import myTest.api.test.repository.SidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Controller
public class OverallController {

    private final OverallRepository overallRepository;

    @Autowired
    public OverallController(OverallRepository overallRepository) {
        this.overallRepository = overallRepository;
    }

    @GetMapping("/overall")
    public String api1(Model model) throws IOException {
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMinuDustFrcstDspth"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=MRi2mirgR5H4KnX%2FSaac2Hh6O76YRJtsHTZ60S%2F5zu%2FNoV5kDjup632dozD9jmKy%2F1inJix1TfB%2F1ns%2FDkY76Q%3D%3D"); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("returnType", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /*xml 또는 json*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("100", "UTF-8")); /*한 페이지 결과 수(조회 날짜로 검색 시 사용 안함)*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호(조회 날짜로 검색 시 사용 안함)*/
//        urlBuilder.append("&" + URLEncoder.encode("searchDate","UTF-8") + "=" + URLEncoder.encode("2023-05-14", "UTF-8")); /*통보시간 검색(조회 날짜 입력이 없을 경우 한달동안 예보통보 발령 날짜의 리스트 정보를 확인)*/
        urlBuilder.append("&" + URLEncoder.encode("searchDate", "UTF-8") + "=" + URLEncoder.encode(LocalDate.now().format(DateTimeFormatter.ISO_DATE), "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("InformCode", "UTF-8") + "=" + URLEncoder.encode("PM25", "UTF-8")); /*통보코드검색(PM10, PM25, O3)*/
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());
        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();


        String jsonString = sb.toString();


// Jackson ObjectMapper 객체 생성
        ObjectMapper objectMapper = new ObjectMapper();

// JSON 파싱
        JsonNode rootNode = objectMapper.readTree(jsonString);

// 원하는 데이터 추출
        JsonNode dataNode = rootNode.path("response").path("body").path("items"); // "response.body.items"에 해당하는 노드를 가져옴

        System.setOut(new PrintStream(System.out, true, "UTF-8"));
        Overall overall = null;
        for (JsonNode itemNode : dataNode) {
            String dataTime = itemNode.path("dataTime").asText();
            String informOverall = itemNode.path("informOverall").asText();
            String informCause = itemNode.path("informCause").asText();
            String informGrade = itemNode.path("informGrade").asText();


            String informData = itemNode.path("informData").asText();

            String informCode = itemNode.path("informCode").asText();



            overall = new Overall();

            overall.setDataTime(dataTime);
            overall.setInformOverall(informOverall);
            overall.setInformCause(informCause);
            overall.setInformGrade(informGrade);
            overall.setInformData(informData);

            overall.setInformCode(informCode);
            overallRepository.save(overall);

            System.out.println("인폰코드");
            System.out.println(informCode);


        }

        model.addAttribute("overall", overall);



        return "case/allArea";

    }





}
