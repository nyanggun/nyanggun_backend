// src/main/java/org/kosa/congmouse/nyanggoon/service/HeritageService.java

package org.kosa.congmouse.nyanggoon.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.kosa.congmouse.nyanggoon.dto.HeritageApiResponseDto;
import org.kosa.congmouse.nyanggoon.dto.HeritageApiResponseWrapper;
import org.kosa.congmouse.nyanggoon.dto.HeritageListResponseDto;
import org.kosa.congmouse.nyanggoon.entity.HeritageEncyclopedia;
import org.kosa.congmouse.nyanggoon.repository.HeritageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HeritageService {

    private final HeritageRepository heritageRepository;

    /**
     * DB에서 전체 문화재를 조회하고, 데이터가 없으면 API를 호출하여 저장 후 조회합니다.
     */
    @Transactional(readOnly = true) // 데이터 조회 트랜잭션
    public List<HeritageListResponseDto> getAllHeritages() {
        // 1. DB에서 데이터 조회 시도
        List<HeritageEncyclopedia> entities = heritageRepository.findAll();

        // 2. 데이터가 없으면 API 호출 및 저장 로직 실행
        if (entities.isEmpty()) {
            System.out.println("DB에 문화재 데이터가 없습니다. 외부 API를 호출하여 동기화를 시도합니다.");
            // DB에 저장하고 다시 조회하기 위해 synchronized 블록을 사용하여 동기화 충돌 방지
            synchronized (this) {
                // 저장 후 다시 조회
                fetchAndSaveHeritageList();
                entities = heritageRepository.findAll();
            }
        }

        // 3. Entity를 DTO로 변환하여 반환
        return entities.stream()
                .map(this::mapToResponseDto) // DTO 변환 및 Badge URL 주입
                .toList();
    }

    /**
     * 문화재청 API를 호출하여 데이터를 가져와 DB에 저장하는 메서드 (기존 코드를 @Transactional로 변경)
     */
    @Transactional // DB 저장 트랜잭션
    public void fetchAndSaveHeritageList() {
        String url = "https://www.khs.go.kr/cha/SearchKindOpenapiList.do?pageUnit=1000&ccbaCncl=N&ccbaKdcd=11&ccbaCtcd=11";

        try {
            // ... (기존 API 호출 및 XML 파싱 로직)
            RestTemplate restTemplate = new RestTemplate();
            String xmlResponse = restTemplate.getForObject(url, String.class);

            XmlMapper xmlMapper = new XmlMapper();
            HeritageApiResponseWrapper wrapper = xmlMapper.readValue(xmlResponse, HeritageApiResponseWrapper.class);

            List<HeritageApiResponseDto> apiList = wrapper.getItems();
            if (apiList == null || apiList.isEmpty()) {
                System.out.println("문화재 데이터를 찾을 수 없습니다.");
                return;
            }

            // DTO → Entity 변환 후 저장 (로직 유지)
            List<HeritageEncyclopedia> entities = apiList.stream()
                    .map(dto -> HeritageEncyclopedia.builder()
                            .subjectCode(dto.getSubjectCode())
                            .manageNumber(dto.getManageNumber())
                            .name(dto.getName())
                            .chineseName(dto.getChineseName())
                            .cityCode(dto.getCityCode())
                            .longitude(dto.getLongitude())
                            .latitude(dto.getLatitude())
                            .heritageCode(dto.getHeritageCode())
                            .address(dto.getAddress())
                            .period(dto.getPeriod())
                            .imageUrl(dto.getImageUrl())
                            .content(dto.getContent())
                            .build())
                    .collect(Collectors.toList());

            heritageRepository.saveAll(entities);
            System.out.println("문화재 데이터 저장 완료: " + entities.size() + "건");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("문화재 데이터 저장 실패: " + e.getMessage());
            // 💡 데이터 로드 실패 시 강제로 런타임 예외를 발생시켜 클라이언트에게 알림
            throw new RuntimeException("문화재 API 데이터 동기화에 실패했습니다.", e);
        }
    }

    /**
     * Entity를 DTO로 변환하고 Badge URL을 주입하는 내부 메서드
     */
    private HeritageListResponseDto mapToResponseDto(HeritageEncyclopedia entity) {
        HeritageListResponseDto dto = HeritageListResponseDto.from(entity);
        try {
            // 뱃지 URL 주입 로직
            String badgeUrl =
                    "https://cdn.jsdelivr.net/gh/nyanggun/nyanggoon-badges@main/" + dto.getName() + ".png";
            dto.setBadgeUrl(badgeUrl);
        } catch (Exception e) {
            // 이름이 없거나 인코딩 에러일 경우
            dto.setBadgeUrl("https://cdn.jsdelivr.net/gh/nyanggun/nyanggoon-badges@main/%EA%B8%B0%EB%B3%B8.png");
        }
        return dto;
    }
}