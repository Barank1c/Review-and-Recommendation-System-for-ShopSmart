package hbg.rrssbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "CPComments")
public class CPComments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long cpcId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cpId", insertable = true, updatable = true)
    private CommunityPosts communityPosts;
    private String comment;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = true, updatable = true)
    private AppUser appUser;
    @CreationTimestamp
    private Timestamp cpcTime;
}
