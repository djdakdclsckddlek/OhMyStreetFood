package org.omsf.member.service;

import org.omsf.member.model.Member;

public interface MemberService<T extends Member> {

	boolean getMemberId(String username);

}
