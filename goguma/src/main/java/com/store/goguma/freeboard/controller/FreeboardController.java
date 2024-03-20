package com.store.goguma.freeboard.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.store.goguma.entity.FreeBoard;
import com.store.goguma.freeboard.dto.BoardTypeDTO;
import com.store.goguma.freeboard.dto.FreeBoardCountRecommendationByCateDto;
//import com.store.goguma.freeboard.dto.FreeBoardCountRecommendationByCateDto;
import com.store.goguma.freeboard.dto.FreeBoardDTO;
import com.store.goguma.freeboard.dto.FreeBoardFormDTO;
import com.store.goguma.freeboard.dto.FreeBoardManyCategoryDto;
import com.store.goguma.handler.exception.BackPageRestfulException;
import com.store.goguma.handler.exception.LoginRestfulException;
//import com.store.goguma.freeboard.dto.FreeBoardManyCategoryDto;
import com.store.goguma.service.FreeBoardService;
import com.store.goguma.user.dto.OauthDTO;
import com.store.goguma.utils.Define;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequestMapping("/freeBoard")
@RequiredArgsConstructor
public class FreeboardController {

	
	private final FreeBoardService freeBoardService;
	private final HttpSession httpSession;
	
	
	// 자유 게시판 메인화면
	@GetMapping("/main")
	public String boardMain( Model model) {
		
		List<FreeBoardDTO> boardList = freeBoardService.findAllFree();
		List<FreeBoardDTO> recommendationList = freeBoardService.countRecommendation();
		List<FreeBoardManyCategoryDto> categoryList = freeBoardService.mainFreeBoardCategory();
		
		int count = 1;
		for(FreeBoardManyCategoryDto i : categoryList) {
			List<FreeBoardCountRecommendationByCateDto> list = freeBoardService.mainFreeBoard(i.getMainCategoryId(), i.getSubCategoryId());
			log.info("cateogrygoogogogogogogoog " + count + " {}" , list);
			model.addAttribute("categoryList" + count , list);
			model.addAttribute("category" + count , i);
			count++;
		}
		
		model.addAttribute("boardList" , boardList);
		model.addAttribute("rDList" , recommendationList);
		
		return "/free_board/free-main";
	}
	
	// list타입 게시글 목록
	@GetMapping(value = "/list", params={"cate1","id"})
	public String boardList(@RequestParam("cate1")int cate1, @RequestParam("id")int id, Model model) {
		
		// 값들어옴 확인했음 테스트 주소 http://localhost/freeBoard/list?cate1=1&cate2=1&name=잡담
		log.info("cate1 "+cate1);
		
		
		// 게시판 형식 일단 게시판 형식부터 ㄱ
		BoardTypeDTO listType = freeBoardService.selectArticleType(cate1, id);
		
		log.info("프리보드 리스트 컨트롤러 리스트 타입 값 확인"+listType.toString());
		
		// 게시판 목록  페이징 하삼
		//freeBoardService.selectArticleAllBycate(cate1, cate2);
		
		model.addAttribute("type", listType);
		
		
		return "/free_board/free-list";
	}
	
	
	// 자유 게시판 카드 형식 글 목록
	@GetMapping("/card")
	public String boardCard() {
		
		return "/free_board/free-card";
	}
	
	// 자유 게시판 글 작성
	@GetMapping("/write")
	public String boardWrite() {
		OauthDTO user = (OauthDTO) httpSession.getAttribute("principal");
		
		// 회원, 비회원 검증
		if (user == null) {
            throw new LoginRestfulException(com.store.goguma.utils.Define.ENTER_YOUR_LOGIN, HttpStatus.BAD_REQUEST);
        }
		
		return "/free_board/free-write";
	}
	
	// 게시글 등록
	@PostMapping("/write")
	public String write(FreeBoardFormDTO boardFormDTO) {
		OauthDTO user = (OauthDTO) httpSession.getAttribute("principal");
		
		// 회원, 비회원 검증
		if (user == null) {
			throw new LoginRestfulException(com.store.goguma.utils.Define.ENTER_YOUR_LOGIN, HttpStatus.BAD_REQUEST);
		}
		
		int userId = user.getUId();
		boardFormDTO.setUId(userId);
		
		
		log.info("info : {}" , boardFormDTO);
		
		int result = freeBoardService.insert(boardFormDTO);
		if(result == 0) {
			throw new BackPageRestfulException(Define.INTERVAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return "redirect:/user/board";
	}
	

	// 게시글 등록
	@PostMapping("/write/update/{id}")
	public String writeUpdate(FreeBoardFormDTO boardFormDTO) {
		OauthDTO user = (OauthDTO) httpSession.getAttribute("principal");
		
		// 회원, 비회원 검증
		if (user == null) {
			throw new LoginRestfulException(com.store.goguma.utils.Define.ENTER_YOUR_LOGIN, HttpStatus.BAD_REQUEST);
		}
		
		int userId = user.getUId();
		boardFormDTO.setUId(userId);
		
		
		log.info("info : {}" , boardFormDTO);
		
		freeBoardService.insert(boardFormDTO);
		
		
		return "redirect:/free_board/list";
	}
	
	/**
	 * 게시글 수정 폼 이동
	 * @param id
	 * @param model
	 * @return
	 */
	@GetMapping("/write/update/{id}")
	public String boardWriteUpdate(@PathVariable(value = "id") Integer id ,  Model model) {
		OauthDTO user = (OauthDTO) httpSession.getAttribute("principal");
		
		// 회원, 비회원 검증
		if (user == null) {
            throw new LoginRestfulException(com.store.goguma.utils.Define.ENTER_YOUR_LOGIN, HttpStatus.BAD_REQUEST);
        }
		
		FreeBoard board = freeBoardService.findById(id);
		
		if(board == null) {
			throw new BackPageRestfulException(Define.INTERVAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		model.addAttribute("board" , board);
		return "/free_board/free-write-update";
	}
	
	@PutMapping("/write/update")
	public String putMethodName(FreeBoardFormDTO boardFormDTO) {
		OauthDTO user = (OauthDTO) httpSession.getAttribute("principal");
		
		// 회원, 비회원 검증
		if (user == null) {
            throw new LoginRestfulException(com.store.goguma.utils.Define.ENTER_YOUR_LOGIN, HttpStatus.BAD_REQUEST);
        }
		
		int result = freeBoardService.updateFreeBoard(boardFormDTO);
		if(result == 0) {
			throw new BackPageRestfulException(Define.INTERVAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return "redirect:/user/board";
	}
}
