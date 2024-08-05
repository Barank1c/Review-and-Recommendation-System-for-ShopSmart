package hbg.rrssbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ReviewResponses")
public class ReviewResponses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long rrId;
    private String text;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = true, updatable = true)
    private AppUser appUser;
    @CreationTimestamp
    private Timestamp RRTime;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewId", insertable = true, updatable = true)
    private Review review;

    @OneToMany(mappedBy = "reviewResponse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RRUpvote> rrUpvotes;

}
