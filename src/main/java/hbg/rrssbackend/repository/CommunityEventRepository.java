package hbg.rrssbackend.repository;

import hbg.rrssbackend.model.CommunityEvent;
import hbg.rrssbackend.model.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityEventRepository extends JpaRepository<CommunityEvent, Long> {
}
