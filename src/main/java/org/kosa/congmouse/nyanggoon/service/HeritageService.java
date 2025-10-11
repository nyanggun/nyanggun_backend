// src/main/java/org/kosa/congmouse/nyanggoon/service/HeritageService.java

package org.kosa.congmouse.nyanggoon.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.HeritageCreateDto;
import org.kosa.congmouse.nyanggoon.entity.HeritageEncyclopedia;
import org.kosa.congmouse.nyanggoon.repository.HeritageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class HeritageService {

    private final HeritageRepository heritageRepository;

    // 문화재 저장 from 국가유산성 api
    @Transactional
    public void saveHeritageList() {
        String listUrl = "https://www.khs.go.kr/cha/SearchKindOpenapiList.do?pageUnit=1000&ccbaCncl=N&ccbaKdcd=11&ccbaCtcd=11";

        RestTemplate restTemplate = new RestTemplate();

        try{
            String xmlResponse = restTemplate.getForObject(listUrl, String.class);
            XmlMapper xmlMapper = new XmlMapper();
            Map<String, Object> root = xmlMapper.readValue(xmlResponse, Map.class);
            List<Map<String, Object>> itemList = (List<Map<String, Object>>) root.get("item");
            log.info("국가유산청 api 문화재 리스트 조회 성공");

            // listUrl에서 받아온 데이터에서 ccbaAsno(관리번호)만 빼서 detailUrl 조회하는 메서드
            getHeritageByManageNumber(itemList);


        }catch(Exception e){
            throw new RuntimeException("국가유산청 api 문화재 리스트 조회 요청 실패");
        }
    }

    // listUrl에서 받아온 데이터에서 ccbaAsno(관리번호)만 빼서 detailUrl 조회하는 메서드
    private void getHeritageByManageNumber(List<Map<String, Object>> itemList) {
        RestTemplate restTemplate = new RestTemplate();
        XmlMapper xmlMapper = new XmlMapper();
        for(Map<String, Object> item : itemList){
            String manageNumber = (String)item.get("ccbaAsno");

            if(manageNumber != null && !manageNumber.isEmpty()){
                String detailUrl = "https://www.khs.go.kr/cha/SearchKindOpenapiDt.do?ccbaKdcd=11&ccbaAsno=" + manageNumber + "&ccbaCtcd=11";
                log.info("국가유산청 api 문화재 조회 시도 manageNumber{} ", manageNumber);
                try{
                String xmlResponse = restTemplate.getForObject(detailUrl, String.class);
                    log.info("국가유산청 api 문화재 조회 시도 xmlResponse{} ", xmlResponse);
                HeritageCreateDto dtos = xmlMapper.readValue(xmlResponse, HeritageCreateDto.class);
                    log.info("국가유산청 api 문화재 조회 시도 dtos{} ", dtos);
                HeritageEncyclopedia heritageEncyclopedia = dtos.toEntity();
                    log.info("국가유산청 api 문화재 조회 성공 manageNumber{} ", manageNumber);
                // db 저장
                heritageRepository.save(heritageEncyclopedia);

                }catch(Exception e){
                    throw new RuntimeException("국가유산청 api 문화재 관리번호 조회 요청 실패");
                }
            }
        }
    }
}