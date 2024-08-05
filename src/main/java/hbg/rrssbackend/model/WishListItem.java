package hbg.rrssbackend.model;

import hbg.rrssbackend.model.WishListItemID;
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
@Table(name = "WishListItem")
@IdClass(WishListItemID.class)
public class WishListItem {


    @Id
    @Column(name = "productId") // Anahtar olarak kullanılan sütun
    private Long productId;

    @Id
    @Column(name = "wishListId") // Anahtar olarak kullanılan sütun
    private Long wishListId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", insertable = false, updatable = false) // İlişkilendirme, aynı sütunu kullanır
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wishListId", insertable = false, updatable = false) // İlişkilendirme, aynı sütunu kullanır
    private WishList wishList;

}
