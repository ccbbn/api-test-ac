package myTest.api.test.domain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;


@Entity
@Data
@RequiredArgsConstructor
public class Sido {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String sidoName;
    private String dataTime;
    private String pm10Value;
    private String pm25Value;
    private String pm10Grade;
    private String pm25Grade;
    private String stationName;


    private Double gPm25Value;
    private Double gPm10Value;

    private Double gwPm25Value;
    private Double gwPm10Value;



    @OneToOne(mappedBy = "sido")
    private SidoStation sidoStation;



}
