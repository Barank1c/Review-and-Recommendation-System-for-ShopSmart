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
@Table(name = "RRUpvote")
@IdClass(RRUpvoteID.class)
public class RRUpvote {

    @Id
    @Column(name = "userId") // Anahtar olarak kullanılan sütun
    private Long userId;

    @Id
    @Column(name = "rrId") // Anahtar olarak kullanılan sütun
    private Long rrId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false) // İlişkilendirme, aynı sütunu kullanır
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rrId", insertable = false, updatable = false) // İlişkilendirme, aynı sütunu kullanır
    private ReviewResponses reviewResponse;

}
