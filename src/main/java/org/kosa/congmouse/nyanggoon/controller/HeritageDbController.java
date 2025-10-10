package org.kosa.congmouse.nyanggoon.controller;

import lombok.RequiredArgsConstructor;
import org.kosa.congmouse.nyanggoon.dto.ApiResponseDto;
import org.kosa.congmouse.nyanggoon.dto.HeritageListResponseDto;
import org.kosa.congmouse.nyanggoon.entity.HeritageEncyclopedia;
import org.kosa.congmouse.nyanggoon.repository.HeritageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/heritages")
@RequiredArgsConstructor
public class HeritageDbController {

    private final HeritageRepository heritageRepository;

    // 전체 문화재 조회
    @GetMapping("/all")
    public ApiResponseDto<List<HeritageListResponseDto>> getAllHeritages() {
        List<HeritageEncyclopedia> entities = heritageRepository.findAll();
        List<HeritageListResponseDto> dtos = entities.stream()
                .map(HeritageListResponseDto::from)
                .toList();
        return ApiResponseDto.success(dtos, "전체 문화재 조회 성공");
    }

    // 페이징 조회
    @GetMapping("/page")
    public ApiResponseDto<Page<HeritageListResponseDto>> getHeritagesPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<HeritageEncyclopedia> entityPage = heritageRepository.findAll(pageable);
        Page<HeritageListResponseDto> dtoPage = entityPage.map(HeritageListResponseDto::from);
        return ApiResponseDto.success(dtoPage, "페이지 문화재 조회 성공");
    }

    // 이름 검색
    @GetMapping("/search")
    public ApiResponseDto<List<HeritageListResponseDto>> searchByName(@RequestParam String keyword) {
        List<HeritageEncyclopedia> entities = heritageRepository.findByNameContainingIgnoreCase(keyword);
        List<HeritageListResponseDto> dtos = entities.stream()
                .map(HeritageListResponseDto::from)
                .toList();
        return ApiResponseDto.success(dtos, "검색 결과 조회 성공");
    }

    // 시도/종목 조건 조회
    @GetMapping("/filter")
    public ApiResponseDto<List<HeritageListResponseDto>> filterBySubjectAndCity(
            @RequestParam int subjectCode,
            @RequestParam int cityCode
    ) {
        List<HeritageEncyclopedia> entities = heritageRepository.findBySubjectCodeAndCityCode(subjectCode, cityCode);
        List<HeritageListResponseDto> dtos = entities.stream()
                .map(HeritageListResponseDto::from)
                .toList();
        return ApiResponseDto.success(dtos, "조건 조회 성공");
    }
}
