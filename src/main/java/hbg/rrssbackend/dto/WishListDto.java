package hbg.rrssbackend.dto;

import hbg.rrssbackend.model.AppUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;



@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WishListDto {
    private long wishListId;
    private String wishListName;
    private Timestamp createTime;
    private long userId;
}
