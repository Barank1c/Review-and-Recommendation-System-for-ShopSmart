package hbg.rrssbackend.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CurrentTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "WishList")
public class WishList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long wishListId;
    private String wishListName;
    @CurrentTimestamp
    private Timestamp createTime;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = true, updatable = true)
    private AppUser appUser;
    @OneToMany(mappedBy = "wishList", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WishListItem> wishListItemList;



}
