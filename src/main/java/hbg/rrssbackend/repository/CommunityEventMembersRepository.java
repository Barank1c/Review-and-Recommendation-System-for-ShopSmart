package hbg.rrssbackend.repository;

import hbg.rrssbackend.model.CommunityEventMembers;
import hbg.rrssbackend.model.CommunityEventMembersID;
import hbg.rrssbackend.model.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityEventMembersRepository extends JpaRepository<CommunityEventMembers, CommunityEventMembersID> {
    long countByCeId(Long ceId); // Yeni metod
}
