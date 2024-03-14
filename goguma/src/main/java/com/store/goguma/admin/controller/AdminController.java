package com.store.goguma.admin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.store.goguma.admin.dto.PageReqDTO;
import com.store.goguma.entity.EmojiHistory;
import com.store.goguma.entity.MainEmoji;
import com.store.goguma.handler.exception.LoginRestfulException;
import com.store.goguma.service.AdminService;
import com.store.goguma.service.EmojiHistoryService;
import com.store.goguma.service.EmojiUploadService;
import com.store.goguma.user.dto.OauthDTO;
import com.store.goguma.user.dto.my.EmojiHistoryReqDTO;
import com.store.goguma.user.dto.my.EmojiHistoryResDTO;

import jakarta.servlet.http.HttpSession;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private HttpSession httpSession;

	@Autowired
	private AdminService adminService;

	@Autowired
	private EmojiHistoryService emojiHistoryService;
	
	
	
	
	// 관리자 마이 페이지
	// localhost://admin/modiUser
	@GetMapping("/user")
	public String User(Model model) {

		OauthDTO principal = (OauthDTO) httpSession.getAttribute("principal");

		OauthDTO updateUser = adminService.selectAdminByUid(principal.getUId());

		model.addAttribute("user", updateUser);

		log.info("셀렉트한 유저 마이페이지 데이터 " + updateUser);

		return "admin/admin_user";
	}

	// admin 개인정보 update 페이지
	@GetMapping("/modify")
	public String adminModify(Model model) {

		return "admin/admin_modi";
	}

	// admin 롤을 가진 user의 개인정보 updateProc
	@PostMapping("/modify")
	public String adminModifyProc(OauthDTO dto, Model model) {

		OauthDTO principal = (OauthDTO) httpSession.getAttribute("principal");

		String addr1 = dto.getAddr1();
		String addr2 = dto.getAddr2();
		String address = addr1 + addr2;

		dto.setAddress(address);
		dto.setEmail(principal.getEmail());
		dto.setSocial(principal.getSocial());
		dto.setRole(principal.getRole());

		adminService.modifyAdminByEmail(dto);

		return "redirect:/admin/user";
	}

	/*
	 * admin 상품 결제 이력 관리 페이지 결제 이력 리스트 출력, 페이징
	 */
	@GetMapping("/history")
	public String salesHistory(EmojiHistoryReqDTO historyReqDTO, Model model, PageReqDTO page) {
		log.info("history로 들어오는 pagedto" + page);

		EmojiHistoryResDTO list = adminService.selectAllPayHistoryByY(historyReqDTO);

		model.addAttribute("histories", list.getDtoList());
		model.addAttribute("pg", list.getPg());
		model.addAttribute("start", list.getStart());
		model.addAttribute("end", list.getEnd());
		model.addAttribute("last", list.getLast());

		return "admin/admin_payment_history";
	}

	// admin 활불 사유 검색 ajax
	@PostMapping("/payment-reason")
	@ResponseBody
	public EmojiHistory paymentReasonProc(String merchantId) {

		// merchantId로 환불 사유 검색
		EmojiHistory cancel = adminService.selectCancelByMaerchantId(merchantId);

		String reason = cancel.getCancelReason();

		log.info("돌아오는 리즌 데이터 확인: " + reason);

		return cancel;
	}

	// admin 환불 처리 ajax
	@PostMapping("/payment-confirm")
	public int updateConfirmPayment(String merchantId) {

		log.info("환불 승인시 아이디 확인@@@@@@@@@@@@@@@@@@@@@: " + merchantId);

		// merchantId로 환불 사유 검색
		adminService.updateConfirmPayment(merchantId);

		return 1;
	}

	// 이모지 페이지
	// 이모지 리스트 출력, 페이징
	@GetMapping("/emoji")
	public String managementEmoji(Model model) {

		OauthDTO user = (OauthDTO) httpSession.getAttribute("principal");
		if (user == null) {
			throw new LoginRestfulException(com.store.goguma.utils.Define.ENTER_YOUR_LOGIN, HttpStatus.BAD_REQUEST);
		}
		
		List<MainEmoji> mainEmojiList = emojiHistoryService.findMainEmojiAllByUserId(user.getUId());
        log.info(mainEmojiList.toString());
		
        
        
		
		
		return "admin/emoji_management";
	}
	

	
	@GetMapping("/notice")
	public String managementNotice() {

		return "";
	}

	@GetMapping("/report")
	public String managementReport() {

		return "admin/admin_management_report";
	}

	// 유저간 거래 상품 관리 페이지
	@GetMapping("/product")
	public String managementProduct(Model model) {

		OauthDTO user = (OauthDTO) httpSession.getAttribute("principal");
		if (user == null) {
			throw new LoginRestfulException(com.store.goguma.utils.Define.ENTER_YOUR_LOGIN, HttpStatus.BAD_REQUEST);
		}
		
		List<MainEmoji> mainEmojiList = emojiHistoryService.findMainEmojiAllByUserId(user.getUId());
        log.info(mainEmojiList.toString());
		
        
		
		
		return "admin/admin_management_product";
	}

	@GetMapping("/banner")
	public String managementBanner() {

		return "";
	}

}
