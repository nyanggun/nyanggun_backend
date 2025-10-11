package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

// 도감 entity
@Entity
@Table(name="heritage_encyclopedias")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class HeritageEncyclopedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;
    // 종목코드 : ccbaKdcd(11:국보, 12:보물, 13:사적... -> 숫자만)
    @Column(name = "subject_code", nullable = false)
    private int subjectCode;
    // 관리번호 : ccbaAsno(0000010000000, 0000020000000)
    @Column(name = "manage_number", nullable = false, length = 20)
    private String manageNumber;
    // 국가유산명(국문) : ccbaMnm1
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    // 국가유산명(한자) : ccbaMnm2
    @Column(name = "chinese_name", nullable = false, length = 50)
    private String chineseName;
    // 시도코드 : ccbaCtcd(11:서울, 21:부산... -> 숫자만)
    @Column(name = "city_code", nullable = false)
    private int cityCode;
    // 경도 : longitude(126.975312652739)
    @Column(name = "longitude", precision = 9, scale = 6, nullable = false)
    private BigDecimal longitude;
    // 위도 : latitude(37.559975221378)
    @Column(name = "latitude", precision = 9, scale = 6, nullable = false)
    private BigDecimal latitude;
    // 국가유산종목 : ccmaName(국보, 보물, 사적...)
    @Column(name = "heritage_code", nullable = false)
    private String heritageCode;
    // 소재지 상세 : ccbaLcad(서울 중구 세종대로 40 (남대문로4가))
    @Column(name = "address", nullable = false, length = 500)
    private String address;
    // 시대 : ccceName(조선 태조 7년(1398))
    @Column(name = "period", nullable = false, length = 50)
    private String period;
    // 문화유산이미지 : imageUrl(http://www.khs.go.kr/unisearch/images/national_treasure/2685609.jpg)
    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;
    // 내용 : content
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
}
