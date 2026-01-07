package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kosa.congmouse.nyanggoon.entity.ContentState;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//사진함 게시글을 불러오는 Dto 입니다.
public class PhotoBoxSummaryResponseDto {
    private Long photoBoxId;
    private String path;
    private ContentState contentState;
    private LocalDateTime createdAt;
}
