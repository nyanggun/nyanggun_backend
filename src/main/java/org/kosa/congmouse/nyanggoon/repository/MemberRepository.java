package org.kosa.congmouse.nyanggoon.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.kosa.congmouse.nyanggoon.entity.Member;
import org.kosa.congmouse.nyanggoon.entity.MemberState;
import org.kosa.congmouse.nyanggoon.entity.ProfilePicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    @Override
    Optional<Member> findById(Long id);

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);
    // member 정보 수정
    @Modifying
    @Query("""
        UPDATE Member m SET
            m.email = COALESCE(:email, m.email),
            m.nickname = COALESCE(:nickname, m.nickname),
            m.phoneNumber = COALESCE(:phoneNumber, m.phoneNumber),
            m.password = COALESCE(:password, m.password)
        WHERE m.id = :id
    """)
    void updateMemberInfo(
            @Param("id") Long memberId,
            @Param("email") String email,
            @Param("nickname") String nickname,
            @Param("phoneNumber") String phoneNumber,
            @Param("password") String password
    );
//    // 프로필 사진 수정
//    @Modifying
//    @Query("""
//    UPDATE Member m SET
//        m.profilePicture = :profilePicture
//    WHERE m.id = :id
//    """)
//    void updateProfilePicture(
//            @Param("id") Long memberId,
//            @Param("profilePicture") ProfilePicture profilePicture
//    );

     // 관리자 제재 기능: 회원의 상태(MemberState)를 변경합니다.
    @Modifying
    @Query("UPDATE Member m SET m.memberstate = :state WHERE m.id = :id")
    void updateMemberState(@Param("id") Long memberId, @Param("state") MemberState state);
}
