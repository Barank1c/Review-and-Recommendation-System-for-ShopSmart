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
@Table(name = "ApplyRole")
public class ApplyRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long arId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = true, updatable = true)
    private AppUser appUser;
    @Enumerated(EnumType.STRING)
    private Role role;
}
