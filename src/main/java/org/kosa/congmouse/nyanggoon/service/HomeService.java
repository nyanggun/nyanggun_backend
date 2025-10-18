package org.kosa.congmouse.nyanggoon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.HeritageEncyclopediaResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly= true)
@RequiredArgsConstructor
@Slf4j
public class HomeService {

    //문화재 도감 정보를 가져오는 메소드 입니다.
    //총 4개, 전체 북마크 순으로 가져옵니다. (비회원일 시)
    //총 4개, 회원이 북마크한 것과 유사한 문화재를 가져옵니다. (회원일 시)
    public List<HeritageEncyclopediaResponseDto> getEncyclopediaByBookmark(String username) {
        log.info("도감 정보 4개를 가져옵니다.");


        return null;
    }


    //사진함의 사진을 가져오는 메소드 입니다.
    //북마크 수가 가장 높은 것을 가져옵니다.

    //탐방기의 내용을 가져오는 메소드 입니다.
    //총 4개, 전체 북마크 순으로 가져옵니다.
}
