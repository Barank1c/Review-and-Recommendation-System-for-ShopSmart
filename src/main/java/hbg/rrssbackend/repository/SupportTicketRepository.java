package hbg.rrssbackend.repository;

import hbg.rrssbackend.model.RRUpvote;
import hbg.rrssbackend.model.RRUpvoteID;
import hbg.rrssbackend.model.SupportTicket;
import hbg.rrssbackend.model.UserReward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
    @Query(value = "SELECT * FROM support_ticket p where p.user_id = :userId ORDER BY p.ticket_id OFFSET :offset LIMIT :pageSize", nativeQuery = true)
    List<SupportTicket> findUserTicketsByPage(@Param("offset") int offset, @Param("pageSize") int pageSize, @Param("userId") long userId);

    @Query(value = "SELECT * FROM support_ticket p  ORDER BY p.ticket_id OFFSET :offset LIMIT :pageSize", nativeQuery = true)
    List<SupportTicket> findAllTicketsByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);


    @Query("SELECT COUNT(p) FROM SupportTicket p where p.appUser.userId = :userId")
    int getUserTicketCount(long userId);

    @Query("SELECT COUNT(p) FROM SupportTicket p")
    int getALLTicketCount();
}
