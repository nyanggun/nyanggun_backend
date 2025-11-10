package org.kosa.congmouse.nyanggoon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRegisterDto {

    @Schema(description="멤버 이메일", example="example@example.com")
    String email;

    @Schema(description="멤버 비밀번호", example="example1234")
    String password;

    @Schema(description="멤버 닉네임", example="nickname")
    String nickname;

    @Schema(description="멤버 전화번호", example="01000000000")
    String phoneNumber;
}
