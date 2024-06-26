package org.omsf.member.controller;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.omsf.member.model.GeneralMember;
import org.omsf.member.model.Member;
import org.omsf.member.model.Owner;
import org.omsf.member.service.EmailService;
import org.omsf.member.service.GeneralMemberService;
import org.omsf.member.service.MemberService;
import org.omsf.member.service.OwnerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class MemberController { // yunbin

	private final GeneralMemberService generalMemberService;
	private final OwnerService ownerService;
	private final MemberService<Member> memberService;
	private final EmailService emailService;

	private final PasswordEncoder passwordEncoder;

	@GetMapping("/signin")
	public String showSignInPage(Model model) {
		return "member/signin";
	}

	@GetMapping("/signup/{memberType}")
	public String showGeneralSignUpPage(@PathVariable String memberType, Model model) {
		if (memberType.equals("general")) {
			GeneralMember generalMember = GeneralMember.builder().memberType(memberType).build();
			model.addAttribute("member", generalMember);
		} else {
			Owner owner = Owner.builder().memberType(memberType).build();
			model.addAttribute("member", owner);
		}

		return "member/signup";
	}

	@PostMapping("/signup/general")
	public String processSignUp(@Valid @ModelAttribute("member") GeneralMember generalMember, BindingResult result,
			Model model, RedirectAttributes redirectAttributes) {
		try {
			if (!generalMember.getPasswordConfirm().isEmpty()
					&& !generalMember.getPassword().equals(generalMember.getPasswordConfirm())) {
				result.rejectValue("passwordConfirm", "passwordInCorrect", "비밀번호가 일치하지 않습니다.");
				model.addAttribute("member", generalMember);
				return "member/signup";
			}

			if (result.hasErrors()) {
				model.addAttribute("member", generalMember);
				return "member/signup";
			}

			generalMemberService.insertGeneralMember(generalMember);
			redirectAttributes.addFlashAttribute("success", true);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/signin";
	}

	@PostMapping("/signup/owner")
	public String processSignUp(@Valid @ModelAttribute("member") Owner owner, BindingResult result, Model model) {
		try {
			if (!owner.getPasswordConfirm().isEmpty() && !owner.getPassword().equals(owner.getPasswordConfirm())) {
				result.rejectValue("passwordConfirm", "passwordInCorrect", "비밀번호가 일치하지 않습니다.");
				model.addAttribute("owner", owner);
				return "member/signup";
			}

			if (result.hasErrors()) {
				model.addAttribute("owner", owner);
				return "member/signup";
			}

			ownerService.insertOwner(owner);
			model.addAttribute("username", owner.getUsername());
			model.addAttribute("success", true);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "store/addStore";
	}

	@PostMapping({ "/signup/confirmId", "/findPassword/confirmId" })
	@ResponseBody
	public ResponseEntity<Boolean> comfirmId(String username) {
		boolean result = true;

		if (username.trim().isEmpty()) {
			result = false;
		} else {
			if (memberService.checkMemberId(username)) {
				result = false;
			} else {
				result = true;
			}
		}

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PostMapping({ "/signup/confirmNickName", "/modifyMember/confirmNickName" })
	@ResponseBody
	public ResponseEntity<Boolean> comfirmNickName(String nickName) {
		boolean result = true;

		if (nickName.trim().isEmpty()) {
			result = false;
		} else {
			if (generalMemberService.checkMemberNickName(nickName)) {
				result = false;
			} else {
				result = true;
			}
		}

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PostMapping("/signup/confirmBusinessRegistrationNumber")
	@ResponseBody
	public ResponseEntity<Boolean> comfirmBusinessRegistrationNumber(String businessRegistrationNumber) {
		boolean result = true;

		if (businessRegistrationNumber.trim().isEmpty()) {
			result = false;
		} else {
			if (ownerService.checkBusinessRegistrationNumber(businessRegistrationNumber)) {
				result = false;
			} else {
				result = true;
			}
		}

		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/mypage")
	public String showMypage() {
		return "member/mypage";
	}

	@GetMapping("/findPassword")
	public String showFindPasswordPage() {
		return "member/findPassword";
	}

	@PostMapping("/findPassword")
	public String processFindPassword(String username) {
		try {
			emailService.sendEmail(username);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/signin";
	}

	@GetMapping("/modifyMember")
	public String showConfirmPasswordPage() {
		return "member/confirmPassword";
	}

	@PostMapping("/confirmPassword")
	@ResponseBody
	public boolean confirmPassword(Principal principal, String password, Model model) {
		Optional<Member> _member = memberService.findByUsername(principal.getName());

		if (_member.isPresent()) {
			Member member = _member.get();
			return passwordEncoder.matches(password, member.getPassword());
		}
		return false;
	}

	@GetMapping("/modifyMemberForm")
	public String showModifyMemberFormPage(Model model, Principal principal) {

		for (String authority : currentUserAuthority()) {
			if (authority.equals("ROLE_USER")) {
				Optional<GeneralMember> _member = generalMemberService.findByUsername(principal.getName());

				if (_member.isPresent()) {
					GeneralMember member = _member.get();
					model.addAttribute("member", member);
				}

			} else if (authority.equals("ROLE_OWNER")) {
				Optional<Owner> _member = ownerService.findByUsername(principal.getName());

				if (_member.isPresent()) {
					Owner member = _member.get();
					model.addAttribute("member", member);
				}
			}
		}

		return "member/modify";
	}

	@PostMapping("/modifyMember/general")
	public String processModifyMember(@Valid @ModelAttribute("member") GeneralMember generalMember,
			BindingResult result, Model model) {

		if (generalMember.getPassword() != null && !generalMember.getPassword().isEmpty()) {
			if (result.hasErrors()) {
				model.addAttribute("member", generalMember);
				return "member/modify";
			}

			if (!generalMember.getPassword().equals(generalMember.getPasswordConfirm())) {
				result.rejectValue("passwordConfirm", "passwordInCorrect", "비밀번호가 일치하지 않습니다.");
				model.addAttribute("member", generalMember);
				return "member/modify";
			}
		}

		generalMemberService.updateMember(generalMember);

		return "redirect:/mypage";
	}

	@PostMapping("/modifyMember/owner")
	public String processModifyMember(@Valid @ModelAttribute("member") Owner owner, BindingResult result, Model model) {

		if (owner.getPassword() != null && !owner.getPassword().isEmpty()) {
			if (result.hasErrors()) {
				model.addAttribute("member", owner);
				return "member/modify";
			}

			if (!owner.getPassword().equals(owner.getPasswordConfirm())) {
				result.rejectValue("passwordConfirm", "passwordInCorrect", "비밀번호가 일치하지 않습니다.");
				model.addAttribute("member", owner);
				return "member/modify";
			}
		}
		ownerService.updateMember(owner);

		return "redirect:/mypage";
	}

	@GetMapping("/withdrawal")
	public String processWithdrawal(Principal principal, Model model, HttpServletRequest request, HttpServletResponse response) {
		try {
			memberService.deleteMember(principal.getName());
			
			SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
            
			return "redirect:/signin?withdrawalSuccess";
		} catch (Exception e) {
			model.addAttribute("error", "서버 오류입니다. 다시 시도해 주세요.");
			return "redirect:/modify";
		}
		
	}

	private List<String> currentUserAuthority() {
		// 현재 인증 정보를 가져옴
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		// 사용자 권한(역할) 가져오기
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		List<String> authorityList = authorities.stream().map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());

		return authorityList;
	}
}
