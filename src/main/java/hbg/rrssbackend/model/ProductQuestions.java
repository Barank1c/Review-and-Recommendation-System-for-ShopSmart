package hbg.rrssbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ProductQuestions")
public class ProductQuestions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long pqId;
    private String text;
    private String answer;
    @CreationTimestamp
    Timestamp qTime;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", insertable = true, updatable = true)
    private Product product;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = true, updatable = true)
    private AppUser appUser;
    @OneToMany(mappedBy = "productQuestions", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PQUpvote> pqUpvotes;

}
