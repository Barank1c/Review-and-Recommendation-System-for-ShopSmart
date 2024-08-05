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
@Table(name = "SupportTicket")
public class SupportTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ticketId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = true, updatable = true)
    private AppUser appUser;
    private String title;
    private String text;
    private String answer;
    @CreationTimestamp
    private Timestamp tTime;
}
