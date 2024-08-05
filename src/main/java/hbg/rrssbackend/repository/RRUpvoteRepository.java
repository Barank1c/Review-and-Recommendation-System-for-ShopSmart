package hbg.rrssbackend.repository;

import hbg.rrssbackend.model.RRUpvote;
import hbg.rrssbackend.model.RRUpvoteID;
import hbg.rrssbackend.model.Review;
import hbg.rrssbackend.model.ReviewUpvote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RRUpvoteRepository extends JpaRepository<RRUpvote, RRUpvoteID> {

    @Query(value = "select r from RRUpvote r where r.userId = :userId and r.rrId=:rrId")
    RRUpvote isRRUpvotedByUser(long userId, long rrId);

    @Transactional
    @Modifying
    @Query(value = "insert into rrupvote (rr_id,user_id) VALUES (:rrId,:userId)",nativeQuery = true)
    void upvoteRR(long userId, long rrId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM rrupvote  WHERE rr_id = :rrId AND user_id = :userId",nativeQuery = true)
    void unvoteRR(long userId, long rrId);

    @Query(value = "select count(r) from RRUpvote r where r.rrId=:rrId")
    Integer getTotalUpvotesOfRR(long rrId);


}
