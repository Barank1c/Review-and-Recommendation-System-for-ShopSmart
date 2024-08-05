package hbg.rrssbackend.repository;

import hbg.rrssbackend.model.PQUpvote;
import hbg.rrssbackend.model.PQUpvoteID;
import hbg.rrssbackend.model.RRUpvote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PQUpvoteRepository extends JpaRepository<PQUpvote, PQUpvoteID> {

    @Query(value = "select r from PQUpvote r where r.userId = :userId and r.pqId=:pqId")
    PQUpvote isQUpvotedByUser(long userId, long pqId);

    @Transactional
    @Modifying
    @Query(value = "insert into pqupvote (pq_id,user_id) VALUES (:pqId,:userId)",nativeQuery = true)
    void upvoteQ(long userId, long pqId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM pqupvote  WHERE pq_id = :pqId AND user_id = :userId",nativeQuery = true)
    void unvoteQ(long userId, long pqId);

    @Query(value = "select count(r) from PQUpvote r where r.pqId=:pqId")
    Integer getTotalUpvotesOfQ(long pqId);

}
