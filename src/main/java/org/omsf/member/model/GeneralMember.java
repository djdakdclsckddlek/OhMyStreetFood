package org.omsf.member.model;

import java.sql.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
public class GeneralMember extends Member {
	@NotEmpty(message = "닉네임 입력은 필수입니다.")
	@Size(min = 2)
	private String nickName;
	
    @Builder
    public GeneralMember(String username, String nickName, String password,
                         String loginType, Date createdAt, Date modifiedAt) {
        super(username, password, "일반", loginType, createdAt, modifiedAt);
        this.nickName=nickName;
    }
    
}