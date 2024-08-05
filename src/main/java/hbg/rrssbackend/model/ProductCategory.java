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
@Table(name = "ProductCategory")
@IdClass(ProductCategoryID.class)
public class ProductCategory {
    @Id
    @Column(name = "productId") // Anahtar olarak kullanılan sütun
    private Long productId;

    @Id
    @Column(name = "categoryId") // Anahtar olarak kullanılan sütun
    private Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", insertable = false, updatable = false) // İlişkilendirme, aynı sütunu kullanır
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", insertable = false, updatable = false) // İlişkilendirme, aynı sütunu kullanır
    private Product product;
}
