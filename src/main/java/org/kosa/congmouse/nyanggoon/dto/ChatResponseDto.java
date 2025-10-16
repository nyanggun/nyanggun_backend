package org.kosa.congmouse.nyanggoon.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ChatResponseDto {
    private String response;         // AI 답변
    private List<String> options;    // 추천 문화재 이름
    private Long heritageId;         // 선택 사항: 상세 문화재 ID
}