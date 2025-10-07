    package org.kosa.congmouse.nyanggoon.repository;

    import org.kosa.congmouse.nyanggoon.dto.TalkCommentResponseDto;
    import org.kosa.congmouse.nyanggoon.dto.TalkDetailResponseDto;
    import org.kosa.congmouse.nyanggoon.entity.Talk;
    import org.kosa.congmouse.nyanggoon.entity.TalkComment;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;
    import org.springframework.stereotype.Repository;

    import java.util.List;
    import java.util.Optional;

    @Repository
    public interface TalkRepository extends JpaRepository<Talk, Long> {

        //쿼리문은 엔티티 클래스명으로 작성해야 한다.
        @Query("SELECT t FROM Talk t JOIN FETCH t.member")
        List<Talk> findAllWithMember();

        @Query("SELECT t FROM Talk t JOIN FETCH t.member WHERE t.id = :id")
        TalkDetailResponseDto findTalkDetail(@Param("id") Long id);


        //findById(Long id) : 자동 지원
        // deleteById(Long id) : 자동 지원
    }
