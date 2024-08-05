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
@Table(name = "CommunityEventMembers")
@IdClass(CommunityEventMembersID.class)
public class CommunityEventMembers {
    @Id
    @Column(name = "ceId") // Anahtar olarak kullanılan sütun
    private Long ceId;

    @Id
    @Column(name = "userId") // Anahtar olarak kullanılan sütun
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false) // İlişkilendirme, aynı sütunu kullanır
    private AppUser appUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ceId", insertable = false, updatable = false) // İlişkilendirme, aynı sütunu kullanır
    private CommunityEvent communityEvent;
}
