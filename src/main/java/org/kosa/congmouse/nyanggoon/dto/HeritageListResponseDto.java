package org.kosa.congmouse.nyanggoon.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kosa.congmouse.nyanggoon.entity.HeritageEncyclopedia;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonDeserialize(builder = HeritageListResponseDto.HeritageListResponseDtoBuilder.class)
public class HeritageListResponseDto {
//    @JacksonXmlProperty(localName="no")
//    private Long id;
    @JacksonXmlProperty(localName="ccbaKdcd")
    private int subjectCode;
    @JacksonXmlProperty(localName="ccbaMnm1")
    private String name;
    @JacksonXmlProperty(localName="ccbaLcad")
    private String address;
    @JacksonXmlProperty(localName="latitude")
    private BigDecimal latitude;
    @JacksonXmlProperty(localName="longitude")
    private BigDecimal longitude;

    public static HeritageListResponseDto from(HeritageEncyclopedia heritageEncyclopedia){
        return HeritageListResponseDto.builder()
//                .id(heritageEncyclopedia.getId())
                .subjectCode(heritageEncyclopedia.getSubjectCode())
                .name(heritageEncyclopedia.getName())
                .address(heritageEncyclopedia.getAddress())
                .latitude(heritageEncyclopedia.getLatitude())
                .longitude(heritageEncyclopedia.getLongitude()).build();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    // Builder 클래스를 명시해줘야 @JsonDeserialize가 제대로 동작함
    @JsonPOJOBuilder(withPrefix = "")
    public static class HeritageListResponseDtoBuilder {}
}
