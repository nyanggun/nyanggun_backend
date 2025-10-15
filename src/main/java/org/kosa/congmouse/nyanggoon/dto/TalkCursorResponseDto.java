package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
public class TalkCursorResponseDto<T>{
    private List<TalkListSummaryResponseDto> contents;
    private Long nextCursor;
    private boolean hasNext;
}
