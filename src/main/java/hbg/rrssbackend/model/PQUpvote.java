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
@Table(name = "PQUpvote")
@IdClass(PQUpvoteID.class)
public class PQUpvote {
    @Id
    @Column(name = "userId") // Anahtar olarak kullanılan sütun
    private Long userId;

    @Id
    @Column(name = "pqId") // Anahtar olarak kullanılan sütun
    private Long pqId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false) // İlişkilendirme, aynı sütunu kullanır
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pqId", insertable = false, updatable = false) // İlişkilendirme, aynı sütunu kullanır
    private ProductQuestions productQuestions;
}
