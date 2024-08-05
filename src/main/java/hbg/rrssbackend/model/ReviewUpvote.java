package hbg.rrssbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ReviewUpvote")
@IdClass(ReviewUpvoteID.class)
public class ReviewUpvote {

    @Id
    @Column(name = "userId") // Anahtar olarak kullanılan sütun
    private Long userId;

    @Id
    @Column(name = "reviewId") // Anahtar olarak kullanılan sütun
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false) // İlişkilendirme, aynı sütunu kullanır
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewId", insertable = false, updatable = false) // İlişkilendirme, aynı sütunu kullanır
    private Review review;
}
