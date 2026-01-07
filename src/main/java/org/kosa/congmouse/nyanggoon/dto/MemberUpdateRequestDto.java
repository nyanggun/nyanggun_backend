package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 마이페이지 수정 요청 DTO
 * 사용자가 이메일, 닉네임, 전화번호, 비밀번호, 프로필 이미지를 수정할 때 사용
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateRequestDto {

    private String email;         // 이메일 (일반적으로 수정 불가지만 필요 시 열어둘 수 있음)
    private String nickname;      // 닉네임
    private String phoneNumber;   // 전화번호
    private String password;      // 비밀번호 (입력된 경우에만 업데이트)
    private String path; //사진 경로
}
