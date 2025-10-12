package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kosa.congmouse.nyanggoon.entity.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoBoxDetailResponseDto {

    private Long id;
    private String title;
    private String relatedHeritage;
    private String nickname;
    private LocalDateTime createdAt;
    private String photoBoxPicturePath;
    private List<String> tags;  // 태그 이름들을 리스트로
    private boolean isBookmarked; // 북마크 여부
    private long bookmarkCount;

    //사진함 사진(링크), 태그 모음, 북마크 횟수는 모두 외부에서(서비스) 조회한 후 넣어줘야 한다.
    public static PhotoBoxDetailResponseDto from(PhotoBox photoBox,  PhotoBoxPicture photoBoxPicture, List<String> tags, Long bookmarkCount, boolean isBookmarked){
        return PhotoBoxDetailResponseDto.builder()
                .id(photoBox.getId())
                .title(photoBox.getTitle())
                .relatedHeritage(photoBox.getRelatedHeritage())
                .nickname(photoBox.getMember().getNickname()) // 여기 추가
                .photoBoxPicturePath(photoBoxPicture.getPath()) //사진 게시물 사진위치 넣기
                .tags(tags)
                .createdAt(photoBox.getCreatedAt())
                .bookmarkCount(bookmarkCount)
                .isBookmarked(isBookmarked)
                .build();
    }



}
