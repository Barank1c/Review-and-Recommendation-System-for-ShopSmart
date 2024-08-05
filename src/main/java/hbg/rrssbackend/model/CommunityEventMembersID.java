package hbg.rrssbackend.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@Embeddable
public class CommunityEventMembersID implements Serializable {
    private Long ceId;
    private Long userId;

    public CommunityEventMembersID() {}

    public CommunityEventMembersID(Long ceId, Long userId) {
        this.ceId = ceId;
        this.userId = userId;
    }
}
