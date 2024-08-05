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
@Table(name = "CommunityPosts")
public class CommunityPosts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long cpId;
    private String title;
    private String content;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = true, updatable = true)
    private AppUser appUser;
    @CreationTimestamp
    private Timestamp cpTime;
    //geçici ekledim baran ekleyince değişecek
    @OneToMany(mappedBy = "communityPosts", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CPComments> cpCommentsList;
}
