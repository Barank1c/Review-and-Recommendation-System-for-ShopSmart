package hbg.rrssbackend.repository;

import hbg.rrssbackend.model.AppUser;
import hbg.rrssbackend.model.Category;
import hbg.rrssbackend.model.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT u FROM Category u where u.categoryName = :name")
    Optional<Category> findByName(String name);

}
