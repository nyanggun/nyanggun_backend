package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.entity.Member;
import org.kosa.congmouse.nyanggoon.entity.ProfilePicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfilePictureRepository extends JpaRepository<ProfilePicture, Long> {

    // Member Entity를 기준으로 ProfilePicture를 찾는 쿼리
    Optional<ProfilePicture> findByMember(Member member);

    // Member Entity를 기준으로 ProfilePicture를 삭제하는 쿼리
    void deleteByMember(Member member);
}