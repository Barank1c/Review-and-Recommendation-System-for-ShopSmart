package hbg.rrssbackend.repository;

import hbg.rrssbackend.model.AppUser;
import hbg.rrssbackend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    @Query(value = "SELECT * FROM app_user u ORDER BY u.user_id OFFSET :offset LIMIT :pageSize", nativeQuery = true)
    List<AppUser> findAppUsersByPage(@Param("offset") int offset, @Param("pageSize") int pageSize);

    @Query("SELECT u FROM AppUser u where u.username = :username")
    Optional<AppUser> findByUsername(String username);

    @Query("SELECT COUNT(u) FROM AppUser u")
    int getTotalAppUserCount();

}
