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
@Table(name = "MerchantPromotion")
public class MerchantPromotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long mpId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = true, updatable = true)
    private AppUser appUser;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", insertable = true, updatable = true,unique = true)
    private Product product;
    private int discount;
    private int originalPrice;
    @CreationTimestamp
    private Timestamp mpCreateTime;
}
