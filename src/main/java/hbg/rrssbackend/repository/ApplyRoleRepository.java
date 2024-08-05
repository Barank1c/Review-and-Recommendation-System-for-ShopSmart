package hbg.rrssbackend.repository;

import hbg.rrssbackend.model.AppUser;
import hbg.rrssbackend.model.ApplyRole;
import hbg.rrssbackend.model.Role;
import hbg.rrssbackend.model.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplyRoleRepository extends JpaRepository<ApplyRole, Long> {

    @Query("SELECT u FROM ApplyRole u where u.role = :role and u.appUser.userId = :userId")
    ApplyRole findIsApplied(long userId, Role role);


    @Query(value = "SELECT * FROM apply_role u ORDER BY u.ar_id OFFSET :offset LIMIT :pageSize", nativeQuery = true)
    List<ApplyRole> getApplyRolesByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);


    @Query("SELECT COUNT(u) FROM ApplyRole u")
    int getApplyRolesCount();

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM apply_role WHERE user_id = :userId",nativeQuery = true)
    void removeUserRequests(long userId);

}
