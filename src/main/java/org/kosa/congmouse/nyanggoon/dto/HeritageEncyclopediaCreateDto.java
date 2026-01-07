package org.kosa.congmouse.nyanggoon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kosa.congmouse.nyanggoon.entity.HeritageEncyclopedia;

import java.math.BigDecimal;

@JacksonXmlRootElement(localName = "result")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 국가유산청 api에 없는 column 속성 처리
@JsonIgnoreProperties(ignoreUnknown = true)
// xml인 자료를 데이터베이스에 저장할 때 사용하는 builder
@JsonDeserialize(builder = HeritageEncyclopediaCreateDto.HeritageEncyclopediaCreateDtoBuilder.class)
public class HeritageEncyclopediaCreateDto {

    private Long id;

    @JacksonXmlProperty(localName = "ccbaAsno")
    private String manageNumber;

    @JacksonXmlProperty(localName = "ccbaKdcd")
    private int subjectCode;

    @JacksonXmlProperty(localName = "ccbaCtcd")
    private int cityCode;

    @JacksonXmlProperty(localName = "longitude")
    private BigDecimal longitude;

    @JacksonXmlProperty(localName = "latitude")
    private BigDecimal latitude;

    @JacksonXmlProperty(localName = "item")
    private Item item;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        @JacksonXmlProperty(localName = "ccmaName")
        private String heritageCode;

        @JacksonXmlProperty(localName = "ccbaMnm1")
        private String name;

        @JacksonXmlProperty(localName = "ccbaMnm2")
        private String chineseName;

        @JacksonXmlProperty(localName = "ccbaLcad")
        private String address;

        @JacksonXmlProperty(localName = "ccceName")
        private String period;

        @JacksonXmlProperty(localName = "imageUrl")
        private String imageUrl;

        @JacksonXmlProperty(localName = "content")
        private String content;
    }

    public HeritageEncyclopedia toEntity(Long id) {
        return HeritageEncyclopedia.builder()
                .id(id)
                .longitude(this.longitude)
                .latitude(this.latitude)
                .name(this.item.getName())
                .heritageCode(this.item.getHeritageCode())
                .address(trim(this.item.getAddress()))
                .chineseName(this.item.getChineseName())
                .cityCode(this.cityCode)
                .content(this.item.getContent())
                .imageUrl(this.item.getImageUrl())
                .manageNumber(this.manageNumber)
                .period(this.item.getPeriod())
                .subjectCode(this.subjectCode)
                .build();
    }

    // address에 /n, /t가 가 포함되어 있어 제거
    public static String trim(String str){
        if(str == null) return null;
        return str.replaceAll("\\s+", " ").trim();
    }

    // xml인 자료를 데이터베이스에 저장할 때 사용하는 builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonPOJOBuilder(withPrefix = "")
    public static class HeritageEncyclopediaCreateDtoBuilder {}
}
