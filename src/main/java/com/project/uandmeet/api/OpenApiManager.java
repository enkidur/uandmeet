package com.project.uandmeet.api;

import com.project.uandmeet.api.OpenApiResponseParams;
import com.project.uandmeet.dto.ApiDtoGroup.GuareaDto;
import com.project.uandmeet.dto.ApiDtoGroup.SiareaDto;
import com.project.uandmeet.model.Guarea;
import com.project.uandmeet.model.Siarea;
import com.project.uandmeet.repository.GuareaRepostiory;
import com.project.uandmeet.repository.SiareaRepostiory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
//Component : 개발자가직접 작성한 class를 bean으로 등록하기 위한 annotation.
public class OpenApiManager {

    private final SiareaRepostiory siareaRepostiory;
    private final GuareaRepostiory guareaRepostiory;
    private final String BASE_URL = "http://api.vworld.kr/req/data";
    private final String serviceKey = "?key=908D840D-CAAF-3C01-9A6D-307EE8D34A6E";
    private final String defaultDomain = "&domain=http://localhost:8080/";
    private final String serviceName = "&service=data";
    private final String version = "&version=2.0";
    private final String request = "&request=getfeature";
    private final String format = "&format=json";
    private final String size = "&size=1000"; // max=1000, min = 1, default = 10
    private final String page = "&page=1";
    private final String geometry = "&geometry=false"; //지오메트리(공간정보 좌표) 반환 여부
    private final String attribute = "&attribute=true"; //속성 반환 여부, ture 기본값
    private final String crs = "&crs=EPSG:3857"; //응답결과 좌표계 지원좌표계참고, EPSG:4326(기본값)

    //한반도 전체 지역 검색
    private final String geomFilter = "&geomfilter=BOX(13663271.680031825,3894007.9689600193,14817776.555251127,4688953.0631258525)";
    private final String sidata = "&data=LT_C_ADSIDO_INFO"; //조회할 데이터 서비스 ID값.
    private final String gudata = "&data=LT_C_ADSIGG_INFO"; //조회할 데이터 서비스 ID값.

    public OpenApiManager(SiareaRepostiory siareaRepostiory, GuareaRepostiory guareaRepostiory) {
        this.siareaRepostiory = siareaRepostiory;
        this.guareaRepostiory = guareaRepostiory;
    }

    private String MakeUrl(String data) {
        return BASE_URL + serviceKey + defaultDomain + serviceName + version + request +
                format + size + page + geometry + attribute + crs + geomFilter + data;
    }

    private JSONArray jsonPasing(String siJsonString) throws ParseException {

        JSONParser jsonParser = new JSONParser();

        JSONObject jsonObject = (JSONObject) jsonParser.parse(siJsonString);

        // 가장 큰 JSON 객체 response 가져오기
        JSONObject jsonResponse = (JSONObject) jsonObject.get("response");

        // 그 다음 result 부분 파싱
        JSONObject jsonResult = (JSONObject) jsonResponse.get("result");

        // jsonResult 파싱
        JSONObject jsonFeatureCollection = (JSONObject) jsonResult.get("featureCollection");

        JSONArray jsonFeatures = (JSONArray) jsonFeatureCollection.get("features");

        return jsonFeatures;
    }

    public List<OpenApiResponseParams> fetch() throws ParseException {
        System.out.println(MakeUrl(sidata));

/*      RestTemplate
        spring 3.0 부터 지원한다.
        스프링에서 제공하는 http 통신에 유용하게 쓸 수 있는 템플릿
        HTTP 서버와의 통신을 단순화하고 RESTful 원칙을 지킨다.(json, xml을 쉽게 응답 받음)*/
        RestTemplate restTemplate = new RestTemplate();

        String siJsonString = restTemplate.getForObject(MakeUrl(sidata), String.class);
        JSONArray sijsonFeatures = jsonPasing(siJsonString);

        String guJsonString = restTemplate.getForObject(MakeUrl(gudata), String.class);
        JSONArray gujsonFeatures = jsonPasing(guJsonString);

        List<SiareaDto> siareaDtos = new ArrayList<>();
        List<GuareaDto> guareaDtos = new ArrayList<>();

        //Si에 관한 내용
        for (Object siFeaturesTemp : sijsonFeatures) {
            JSONObject jsonProperties = (JSONObject) siFeaturesTemp;
            System.out.println(jsonProperties);

            JSONObject jsonPropertiesProperties = (JSONObject) jsonProperties.get("properties");
            System.out.println(jsonPropertiesProperties);

            SiareaDto siareaDto = new SiareaDto(jsonProperties, jsonPropertiesProperties);
            Siarea siarea = new Siarea(jsonProperties, jsonPropertiesProperties);

            siareaDtos.add(siareaDto);
            siareaRepostiory.save(siarea);
        }

        //gu 에 관한 내용
        for (Object guFeaturesTemp : gujsonFeatures) {
            JSONObject gujsonProperties = (JSONObject) guFeaturesTemp;
            System.out.println(gujsonProperties);

            JSONObject guJsonPropertiesProperties = (JSONObject) gujsonProperties.get("properties");
            System.out.println(guJsonPropertiesProperties);

            GuareaDto guareaDto = new GuareaDto(gujsonProperties, guJsonPropertiesProperties);
            Guarea guarea = new Guarea(gujsonProperties, guJsonPropertiesProperties);

            guareaDtos.add(guareaDto);
            guareaRepostiory.save(guarea);
        }

        //조합.
        List<OpenApiResponseParams> openApiResponseParams = new ArrayList<>();
        for (SiareaDto siareaDto : siareaDtos) {
            List<String> guNameGroup = new ArrayList<>();
            for (GuareaDto guareaDto : guareaDtos) {
                if (guareaDto.getFullNm().contains(siareaDto.getCtpKorNm())) {
                    guNameGroup.add(guareaDto.getSigKorNm());
                }
            }

            OpenApiResponseParams openApiResponseParamsTemp = new OpenApiResponseParams(siareaDto.getCtpKorNmAbbreviation(), guNameGroup);
            openApiResponseParams.add(openApiResponseParamsTemp);
        }

        //구에 관한 내용.
        return openApiResponseParams;
    }

    public List<OpenApiResponseParams> dataRequest() {

        List<Siarea> siareas = siareaRepostiory.findAll();
        List<Guarea> guareas = guareaRepostiory.findAll();

        List<OpenApiResponseParams> openApiResponseParams = new ArrayList<>();
        for (Siarea siarea : siareas) {
            List<String> guNameGroup = new ArrayList<>();
            for (Guarea guarea : guareas) {
                if (guarea.getFullNm().contains(siarea.getCtpKorNm())) {
                    guNameGroup.add(guarea.getSigKorNm());
                }
            }

            OpenApiResponseParams openApiResponseParamsTemp = new OpenApiResponseParams(siarea.getCtpKorNmAbbreviation(), guNameGroup);
            openApiResponseParams.add(openApiResponseParamsTemp);
        }

        //구에 관한 내용.
        return openApiResponseParams;
    }
}

