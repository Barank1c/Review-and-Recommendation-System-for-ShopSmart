package hbg.rrssbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Rating")
@IdClass(RatingID.class)
public class Rating {
    @Id
    @Column(name = "productId") // Anahtar olarak kullanılan sütun
    private Long productId;

    @Id
    @Column(name = "userId") // Anahtar olarak kullanılan sütun
    private Long userId;

    @Min(1) @Max(5)
    private int rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", insertable = false, updatable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private AppUser appUser;

}

