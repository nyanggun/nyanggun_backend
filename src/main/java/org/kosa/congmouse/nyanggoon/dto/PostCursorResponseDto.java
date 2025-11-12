package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class PostCursorResponseDto<T> {

    private List<PostListSummaryResponseDto> contents;
    private Long nextCursor;
    private boolean hasNext;
}

