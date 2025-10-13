package org.kosa.congmouse.nyanggoon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.ExplorationCommentCreateDto;
import org.kosa.congmouse.nyanggoon.dto.ExplorationCommentResponseDto;
import org.kosa.congmouse.nyanggoon.dto.ExplorationCommentUpdateDto;
import org.kosa.congmouse.nyanggoon.entity.Exploration;
import org.kosa.congmouse.nyanggoon.entity.ExplorationComment;
import org.kosa.congmouse.nyanggoon.entity.Member;
import org.kosa.congmouse.nyanggoon.repository.ExplorationCommentRepository;
import org.kosa.congmouse.nyanggoon.repository.ExplorationRepository;
import org.kosa.congmouse.nyanggoon.repository.MemberRepository;
import org.kosa.congmouse.nyanggoon.security.user.CustomMemberDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
@Slf4j
public class ExplorationCommentService {

    private final ExplorationCommentRepository explorationCommentRepository;
    private final ExplorationRepository explorationRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ExplorationCommentResponseDto createExplorationComment(ExplorationCommentCreateDto explorationCommentCreateDto, Long memberId){

        ExplorationComment parentComment = null;
        Long parentId = explorationCommentCreateDto.getParentExplorationCommentId();

        // ID가 null이 아닐 경우에만 DB에서 조회합니다.
        if (parentId != null) {
            // findById의 결과가 비어있으면 orElse(null)에 의해 parentComment는 null로 유지됩니다.
            parentComment = explorationCommentRepository.findById(parentId).orElse(null);
        }

        // 다른 재료들도 미리 조회해서 준비합니다.
        Exploration exploration = explorationRepository.findById(explorationCommentCreateDto.getExplorationId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 문화재탐방기입니다"));

        Member member = memberRepository.findById(memberId) // memberId를 직접 사용하는 것이 더 안전합니다.
                .orElseThrow(() -> new RuntimeException("존재하지 않는 멤버입니다"));


        // --- 2. 빌더 조립 단계 ---
        // 미리 준비된 안전한 재료들만 빌더에 전달합니다.
        ExplorationComment explorationComment = ExplorationComment.builder()
                .content(explorationCommentCreateDto.getContent())
                .exploration(exploration)
                .parentComment(parentComment) // null이거나, 실제 존재하는 댓글 엔티티가 들어감
                .member(member)
                .build();

        explorationCommentRepository.save(explorationComment);
        return ExplorationCommentResponseDto.from(explorationComment);
    }

    public List<ExplorationCommentResponseDto> getExplorationCommentList() {
        List<ExplorationComment> explorationCommentList = explorationCommentRepository.findAll();
        return explorationCommentList.stream().map(ExplorationCommentResponseDto::from).toList().reversed();
    }


    public ExplorationCommentResponseDto getExplorationComment(Long id) {
        ExplorationCommentResponseDto explorationCommentResponseDto = ExplorationCommentResponseDto.from(explorationCommentRepository.findById(id).orElseThrow(() -> {
            throw new RuntimeException("해당하는 문화재 탐방기 댓글이 없습니다");
        }));
        return explorationCommentResponseDto;
    }

    @Transactional
    public ExplorationCommentResponseDto updateExplorationComment(ExplorationCommentUpdateDto explorationCommentUpdateDto, CustomMemberDetails customMemberDetails) {
        ExplorationComment updateExplorationComment = explorationCommentRepository.findById(explorationCommentUpdateDto.getId()).orElseThrow(() ->{
            throw new RuntimeException("해당하는 댓글이 존재하지 않습니다");
        });
        if(updateExplorationComment.getMember().getId() != customMemberDetails.getMemberId()){
            throw new RuntimeException("해당하는 댓글의 작성 멤버와 수정하는 멤버가 일치하지 않습니다.");
        }
        updateExplorationComment.update(explorationCommentUpdateDto.getContent());
        return ExplorationCommentResponseDto.from(updateExplorationComment);
    }

    @Transactional
    public void deleteExplorationComment(Long id, Long memberId) {
        ExplorationComment deleteExplorationComment = explorationCommentRepository.findById(id).orElseThrow(() -> {
            throw new RuntimeException("해당하는 댓글이 존재하지 않습니다.");
        });
        if(deleteExplorationComment.getMember().getId() != memberId){
            throw new RuntimeException("해당하는 댓글의 작성 멤버와 삭제하는 멤버가 일치하지 않습니다.");
        }
        explorationCommentRepository.deleteById(id);
    }

    public List<ExplorationCommentResponseDto> getExplorationCommentListOfExploration(Long explorationId) {
        List<ExplorationComment> explorationCommentList = explorationCommentRepository.findByExplorationId(explorationId);
        List<ExplorationCommentResponseDto> explorationCommentResponseDtoList = explorationCommentList.stream().map(ExplorationCommentResponseDto::from).toList().reversed();
        return explorationCommentResponseDtoList;
    }
}
