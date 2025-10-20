package org.kosa.congmouse.nyanggoon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.ExplorationDetailDto;
import org.kosa.congmouse.nyanggoon.dto.HeritageEncyclopediaResponseDto;
import org.kosa.congmouse.nyanggoon.dto.PhotoBoxDetailResponseDto;
import org.kosa.congmouse.nyanggoon.entity.Exploration;
import org.kosa.congmouse.nyanggoon.entity.HeritageEncyclopedia;
import org.kosa.congmouse.nyanggoon.entity.PhotoBox;
import org.kosa.congmouse.nyanggoon.entity.PhotoBoxPicture;
import org.kosa.congmouse.nyanggoon.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly= true)
@RequiredArgsConstructor
@Slf4j
public class HomeService {
    private final HeritageEncyclopediaRepository heritageEncyclopediaRepository;
    private final PhotoBoxRepository photoBoxRepository;
    private final PhotoBoxPictureRepository photoBoxPictureRepository;
    private final PhotoBoxTagRepository photoBoxTagRepository;
    private final PhotoBoxBookmarkRepository photoBoxBookmarkRepository;
    private final ExplorationRepository explorationRepository;

    //문화재 도감 정보를 가져오는 메소드 입니다.
    //총 4개, 전체 북마크 순으로 가져옵니다. (비회원일 시)
    //총 4개, 회원이 북마크한 것과 유사한 문화재를 가져옵니다. (회원일 시)
    public List<HeritageEncyclopediaResponseDto> getEncyclopediaByBookmark() {
        log.info("도감 정보를 북마크 순으로 가져옵니다. 4개를 가져옵니다.");

        Pageable pageable = PageRequest.of(0, 4);
        List<HeritageEncyclopedia> heritageEncyclopedias = heritageEncyclopediaRepository.findHeritageTop4ByBookmarkCount(pageable);

        //북마크 수와 북마크 여부는 따로 세지 않습니다. (0, false로 나옴)
        List<HeritageEncyclopediaResponseDto> heritageEncyclopediaResult = heritageEncyclopedias.stream()
                .map(heritage -> HeritageEncyclopediaResponseDto.from(heritage, 0, false))
                .collect(Collectors.toList());


        //만약 추출한 북마크 개수가 4개 이하라면
        //랜덤한 문화재를 넣습니다.

        if (heritageEncyclopediaResult.size() < 4) {
            log.info("북마크 총 개수 4개 이하 - 랜덤한 문화재를 넣습니다.");
            int remaining = 4 - heritageEncyclopediaResult.size();
            Pageable randomPageable = PageRequest.of(0, remaining); // 남은 개수만큼만 랜덤으로 가져오기
            List<HeritageEncyclopedia> heritageEncyclopediaRandom = heritageEncyclopediaRepository.findRandomHeritage(randomPageable);

            List<HeritageEncyclopediaResponseDto> heritageEncyclopediaRandomResults = heritageEncyclopediaRandom.stream()
                    .map(heritage -> HeritageEncyclopediaResponseDto.from(heritage, 0, false))
                    .toList();

            heritageEncyclopediaResult.addAll(heritageEncyclopediaRandomResults);
        }


        return heritageEncyclopediaResult;
    }


    //사진함의 사진을 가져오는 메소드 입니다.
    //북마크 수가 가장 높은 것을 가져옵니다.
    public PhotoBoxDetailResponseDto getPhotoBoxByBookmark() {

        //해당 사진함 가져오기
        //만약 북마크 데이터가 없다면 (아무도 북마크 안했을때) 최신 사진함 사진 하나를 가져옵니다.
        PhotoBox photoBox = photoBoxRepository
                .findMostPhotoBoxBookmark(PageRequest.of(0, 1))
                .get(0);

        //만약 북마크 데이터가 없다면 (아무도 북마크 안했을때) 랜덤으로 사진함 사진 하나를 가져옵니다.
        if (photoBox == null) {
           photoBox = photoBoxRepository.findTopByOrderByCreatedAtDesc();

        }

        // 해당 사진함의 사진 가져오기
        PhotoBoxPicture photoBoxPicture = photoBoxPictureRepository.findByIdwithPhotoBoxId(photoBox.getId());

        // 해당 사진함의 태그 리스트 가져오기
        List<String> tags = photoBoxTagRepository.findTags(photoBox.getId());
        
        //Dto 변환 후 반환
        //북마크 수와 유저 북마크 여부는 전달해줄 필요가 없으므로 0으로 전달합니다.
        return   PhotoBoxDetailResponseDto.from(photoBox, photoBoxPicture, tags, 0L, false);


    }


    //탐방기의 내용을 가져오는 메소드 입니다.
    //총 4개, 전체 북마크 순으로 가져옵니다.
    //북마크가 없다면 최신 순으로 가져옵니다.
    public List<ExplorationDetailDto> getExplorationByBookmark() {
        log.info("탐방기를 북마크 순으로 가져옵니다. 4개를 가져옵니다.");

        Pageable pageable = PageRequest.of(0, 4);
        List<Exploration> explorations = explorationRepository.findExplorationTop4ByBookmarkCount(pageable);
        List<ExplorationDetailDto> explorationResult = explorations.stream()
                .map(ExplorationDetailDto::from)
                .collect(Collectors.toList());

        // 4개 미만일 경우 최신 탐방기로 채우기
        if (explorationResult.size() < 4) {
            log.info("탐방기 북마크 수가 4개 이하 - 최신 탐방기로 채웁니다.");
            int remaining = 4 - explorationResult.size();

            Pageable latestPageable = PageRequest.of(0, remaining);
            List<Exploration> latestExplorations = explorationRepository.findLatestExplorations(latestPageable);

            List<ExplorationDetailDto> latestResults = latestExplorations.stream()
                    .map(ExplorationDetailDto::from)
                    .collect(Collectors.toList());

            explorationResult.addAll(latestResults);
        }
        return explorationResult;
    }

}
