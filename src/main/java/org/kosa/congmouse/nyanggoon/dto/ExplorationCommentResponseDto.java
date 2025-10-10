package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kosa.congmouse.nyanggoon.entity.ExplorationComment;
import org.kosa.congmouse.nyanggoon.repository.ExplorationCommentRepository;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExplorationCommentResponseDto {

    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private Long memberId;
    private String memberNickname;
    private Long explorationId;
    private Long parentCommentId;

    public static ExplorationCommentResponseDto from(ExplorationComment explorationComment) {
        Long parentExplorationCommentId = null;
        if(explorationComment.getParentComment() != null)
            parentExplorationCommentId = explorationComment.getParentComment().getId();
        return ExplorationCommentResponseDto.builder()
                .id(explorationComment.getId())
                .content(explorationComment.getContent())
                .createdAt(explorationComment.getCreatedAt())
                .memberId(explorationComment.getMember().getId())
                .explorationId(explorationComment.getExploration().getId())
                .parentCommentId(parentExplorationCommentId)
                .build();
    }
}
