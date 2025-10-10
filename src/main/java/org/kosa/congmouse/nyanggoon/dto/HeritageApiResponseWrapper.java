package org.kosa.congmouse.nyanggoon.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JacksonXmlRootElement(localName = "result") // XML 루트
public class HeritageApiResponseWrapper {

    @JacksonXmlProperty(localName = "totalCnt")
    private int totalCount;

    @JacksonXmlProperty(localName = "pageUnit")
    private int pageUnit;

    @JacksonXmlProperty(localName = "pageIndex")
    private int pageIndex;

    @JacksonXmlElementWrapper(useWrapping = false) // <item> 여러 개 직접 나열됨
    @JacksonXmlProperty(localName = "item")
    private List<HeritageApiResponseDto> items; // 실제 문화재 데이터 리스트
}
