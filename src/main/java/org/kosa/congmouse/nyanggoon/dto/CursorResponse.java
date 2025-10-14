package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CursorResponse<T> {
    private List<PhotoBoxSummaryResponseDto> contents;
    private Long nextCursor;
    private boolean hasNext;
}
