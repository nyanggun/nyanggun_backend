package org.kosa.congmouse.nyanggoon.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.TalkDetailResponseDto;
import org.kosa.congmouse.nyanggoon.dto.TalkListSummaryResponseDto;
import org.kosa.congmouse.nyanggoon.service.TalkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//담소 컨트롤러 입니다.
@RestController
@RequestMapping("/talks")
@RequiredArgsConstructor
@Slf4j
public class TalkController {
    private final TalkService talkService;

    //게시글들을 가져오는 컨트롤러 입니다.
    @GetMapping
    public ResponseEntity<List<TalkListSummaryResponseDto>> getAllTalkList(){
        log.info("게시글들 조회 컨트롤러 작동 ok");
        List<TalkListSummaryResponseDto> talks = talkService.findAllTalkList();
        return ResponseEntity.ok(talks);
    }


    //게시글을 상세 확인하는 컨트롤러 입니다.
    @GetMapping("/{id}")
    public ResponseEntity<TalkDetailResponseDto> getTalkDetailById(@PathVariable Long id){
        log.info("게시글 상세 조회 컨트롤러 작동 ok");
        TalkDetailResponseDto postDetailResponseDto = talkService.findTalkDetail(id);
        return ResponseEntity.ok(postDetailResponseDto);
    }
    //게시글을 작성하는 컨트롤러 입니다.

    //게시글을 수정하는 컨트롤러 입니다.

    //게시글을 삭제하는 컨트롤러 입니다.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTalkById(@PathVariable Long id){
        log.info("게시글 삭제 컨트롤러 작동 ok");
        log.info("삭제할 게시글 id : {}", id);
        try{
            talkService.deleteTalk(id);
            return ResponseEntity.noContent().build();
        }
        catch(Exception e){
        return  ResponseEntity.notFound().build();
        }


    }
}
