package org.kosa.congmouse.nyanggoon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;

import java.math.BigDecimal;

/**
 * 국가유산청 API (XML)에서 직접 받아오는 DTO
 * 이건 문화재 정보 목록을 추출하게 하려고 만든 DTO이기에 지우면 안된다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JacksonXmlRootElement(localName = "item")
@JsonIgnoreProperties(ignoreUnknown = true)
public class HeritageApiResponseDto {

    @JacksonXmlProperty(localName = "ccbaKdcd")
    private int subjectCode;

    @JacksonXmlProperty(localName = "ccbaAsno")
    private String manageNumber;

    @JacksonXmlProperty(localName = "ccbaMnm1")
    private String name;

    @JacksonXmlProperty(localName = "ccbaMnm2")
    private String chineseName;

    @JacksonXmlProperty(localName = "ccbaCtcd")
    private int cityCode;

    @JacksonXmlProperty(localName = "longitude")
    private BigDecimal longitude;

    @JacksonXmlProperty(localName = "latitude")
    private BigDecimal latitude;

    @JacksonXmlProperty(localName = "ccmaName")
    private String heritageCode;

    @JacksonXmlProperty(localName = "ccbaLcad")
    private String address;

    @JacksonXmlProperty(localName = "ccceName")
    private String period;

    @JacksonXmlProperty(localName = "imageUrl")
    private String imageUrl;

    @JacksonXmlProperty(localName = "content")
    private String content;
}
