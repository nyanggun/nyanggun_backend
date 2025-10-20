package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kosa.congmouse.nyanggoon.entity.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyPageResponseDto {
    private Member member; // 내 기본 정보
    private List<Exploration> myExplorations; // 내가 작성한 탐방기록
    private List<ExplorationBookmark> explorationBookmarks; // 탐방 북마크
    private List<ExplorationComment> explorationComments; // 탐방 댓글

    private List<PhotoBox> myPhotoBoxes; // 내가 만든 포토박스
    private List<PhotoBoxBookmark> photoBoxBookmarks; // 포토박스 북마크
    private List<PhotoBoxPicture> photoBoxPictures; // 내가 업로드한 사진들

    private List<Talk> myTalks; // 내가 쓴 담소글
    private List<TalkComment> myTalkComments; // 담소 댓글
    private List<TalkBookmark> talkBookmarks; // 담소 북마크

    private List<EncyclopediaBookmark> encyclopediaBookmarks; // 문화재 백과 북마크
}
