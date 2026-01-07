package org.kosa.congmouse.nyanggoon.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.EncyclopediaBookmarkDto;
import org.kosa.congmouse.nyanggoon.dto.HeritageEncyclopediaCreateDto;
import org.kosa.congmouse.nyanggoon.dto.HeritageEncyclopediaResponseDto;
import org.kosa.congmouse.nyanggoon.entity.EncyclopediaBookmark;
import org.kosa.congmouse.nyanggoon.entity.HeritageEncyclopedia;
import org.kosa.congmouse.nyanggoon.entity.Member;
import org.kosa.congmouse.nyanggoon.repository.EncyclopediaBookmarkRepository;
import org.kosa.congmouse.nyanggoon.repository.HeritageEncyclopediaRepository;
import org.kosa.congmouse.nyanggoon.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class HeritageEncyclopediaService {

    private final HeritageEncyclopediaRepository heritageEncyclopediaRepository;
    private final EncyclopediaBookmarkRepository encyclopediaBookmarkRepository;
    private final MemberRepository memberRepository;

    // 문화재 도감 저장 from 국가유산청 api
    @Transactional
    public void saveHeritageList() {
        String listUrl = "https://www.khs.go.kr/cha/SearchKindOpenapiList.do?pageUnit=300&ccbaCncl=N&ccbaKdcd=11&ccbaCtcd=11";

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
            Long id = Long.parseLong((String)item.get("no"));
            String manageNumber = (String)item.get("ccbaAsno");

            if(manageNumber != null && !manageNumber.isEmpty()){
                String detailUrl = "https://www.khs.go.kr/cha/SearchKindOpenapiDt.do?ccbaKdcd=11&ccbaAsno=" + manageNumber + "&ccbaCtcd=11";
                log.info("국가유산청 api 문화재 조회 시도 manageNumber{} ", manageNumber);
                try{
                    String xmlResponse = restTemplate.getForObject(detailUrl, String.class);
                    log.info("국가유산청 api 문화재 조회 시도 xmlResponse{} ", xmlResponse);
                    HeritageEncyclopediaCreateDto dtos = xmlMapper.readValue(xmlResponse, HeritageEncyclopediaCreateDto.class);
                    log.info("국가유산청 api 문화재 조회 시도 dtos{} ", dtos);
                    HeritageEncyclopedia heritageEncyclopedia = dtos.toEntity(id);
                    log.info("국가유산청 api 문화재 조회 성공 manageNumber{} ", manageNumber);
                    // db 저장
                    heritageEncyclopediaRepository.save(heritageEncyclopedia);

                }catch(Exception e){
                    throw new RuntimeException("국가유산청 api 문화재 관리번호 조회 요청 실패");
                }
            }
        }
    }

    // 문화재 도감 리스트 - 가나다순
    public Page<HeritageEncyclopediaResponseDto> getAllHeritageEncyclopediasSortedByKoreanName(int page, int size, Long memberId){
        log.info("===문화재 도감 가나다순 조회 시작===");
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<HeritageEncyclopedia> heritageEncyclopediaPage = heritageEncyclopediaRepository.findAllOrderByName(pageable);
        log.info("===조회된 문화재 도감 가나다순 목록 첫번째 문화재 이름: {} ===", heritageEncyclopediaPage.getContent().getFirst().getName());

        return heritageEncyclopediaPage.map((heritage) -> {
            Long heritageEncyclopediaId = heritage.getId();
            boolean isBookmarked = false;
            if(memberId != null){
                isBookmarked = encyclopediaBookmarkRepository.existsByMemberIdAndHeritageEncyclopediaId(memberId, heritageEncyclopediaId);
            }
            long bookmarkCount = encyclopediaBookmarkRepository.countByHeritageEncyclopediaId(heritageEncyclopediaId);

            return HeritageEncyclopediaResponseDto.from(heritage, bookmarkCount, isBookmarked);
        });
    }

    // 문화재 도감 리스트 - 인기순
    public Page<HeritageEncyclopediaResponseDto> getAllHeritageEncyclopediasSortedByPopular(int page, int size, Long memberId) {
        log.info("===문화재 도감 인기순 조회 시작===");
        Pageable pageable = PageRequest.of(page, size);
        Page<HeritageEncyclopedia> heritageEncyclopediaPage = heritageEncyclopediaRepository.findAllOrderByPopular(pageable);
        log.info("===검색된 문화재 도감 인기순 목록 첫번째 문화재 이름: {} ===", heritageEncyclopediaPage.getContent().get(0).getName());

        return heritageEncyclopediaPage.map((heritage) -> {
            Long heritageEncyclopediaId = heritage.getId();
            boolean isBookmarked = false;
            if(memberId != null){
                isBookmarked = encyclopediaBookmarkRepository.existsByMemberIdAndHeritageEncyclopediaId(memberId, heritageEncyclopediaId);
            }
            long bookmarkCount = encyclopediaBookmarkRepository.countByHeritageEncyclopediaId(heritageEncyclopediaId);

            return HeritageEncyclopediaResponseDto.from(heritage, bookmarkCount, isBookmarked);
        });
    }

    // 상세 페이지
    public HeritageEncyclopediaResponseDto getHeritageEncyclopediaById(Long heritageEncyclopediaId, Long memberId) {
        log.info("=== 문화재 상세 조회: id={} ===", heritageEncyclopediaId);
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("해당하는 멤버가 없습니다"));
        HeritageEncyclopedia heritageEncyclopedia = heritageEncyclopediaRepository.findById(heritageEncyclopediaId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문화재 입니다."));
        boolean isBookmarked = false;
        if(memberId != null){
            isBookmarked = encyclopediaBookmarkRepository.existsByMemberIdAndHeritageEncyclopediaId(memberId, heritageEncyclopediaId);
        }
        long bookmarkCount = encyclopediaBookmarkRepository.countByHeritageEncyclopediaId(heritageEncyclopediaId);
        log.info("=== 문화재 조회 성공: id={} ===", heritageEncyclopedia.getName());
        return HeritageEncyclopediaResponseDto.from(heritageEncyclopedia, bookmarkCount, isBookmarked);
    }

    // 북마크 생성
    @Transactional
    public EncyclopediaBookmarkDto saveBookmark(Long heritageEncyclopediaId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("해당하는 멤버가 없습니다"));
        HeritageEncyclopedia heritageEncyclopedia = heritageEncyclopediaRepository.findById(heritageEncyclopediaId).orElseThrow(() -> new IllegalArgumentException("해당하는 문화재가 없습니다."));

        EncyclopediaBookmark bookmark = EncyclopediaBookmark.builder()
                .member(member)
                .heritageEncyclopedia(heritageEncyclopedia)
                .build();

        EncyclopediaBookmark savedBookmark = encyclopediaBookmarkRepository.save(bookmark);

        return EncyclopediaBookmarkDto.from(savedBookmark);
    }

    // 북마크 삭제
    @Transactional
    public EncyclopediaBookmarkDto deleteBookmark(Long heritageEncyclopediaId, Long memberId) {
        log.info("북마크 삭제");
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("해당하는 멤버가 없습니다"));
        HeritageEncyclopedia heritageEncyclopedia = heritageEncyclopediaRepository.findById(heritageEncyclopediaId).orElseThrow(() -> new IllegalArgumentException("해당하는 문화재가 없습니다."));

        EncyclopediaBookmark bookmark = encyclopediaBookmarkRepository
                .findByMemberAndHeritageEncyclopedia(member, heritageEncyclopedia)
                        .orElseThrow(() -> new IllegalArgumentException("해당하는 북마크가 없습니다."));

        encyclopediaBookmarkRepository.delete(bookmark);
        log.info("삭제된 북마크 memberId{} heritageId{}", memberId, heritageEncyclopediaId);
        return EncyclopediaBookmarkDto.from(bookmark);
    }

    // 검색
    public Page<HeritageEncyclopediaResponseDto> searchHeritageEncyclopedia(String keyword, int page, int size, Long memberId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<HeritageEncyclopedia> heritageEncyclopediaPage = heritageEncyclopediaRepository.searchHeritageEncyclopedia(keyword, pageable);
        log.info("===검색된 문화재 도감 가나다순 목록 첫번째 문화재 이름: {} ===", heritageEncyclopediaPage.getContent().get(0).getName());

        return heritageEncyclopediaPage.map((heritage) -> {
            Long heritageEncyclopediaId = heritage.getId();
            boolean isBookmarked = false;
            if(memberId != null){
                isBookmarked = encyclopediaBookmarkRepository.existsByMemberIdAndHeritageEncyclopediaId(memberId, heritageEncyclopediaId);
            }
            long bookmarkCount = encyclopediaBookmarkRepository.countByHeritageEncyclopediaId(heritageEncyclopediaId);

            return HeritageEncyclopediaResponseDto.from(heritage, bookmarkCount, isBookmarked);
        });
    }
}