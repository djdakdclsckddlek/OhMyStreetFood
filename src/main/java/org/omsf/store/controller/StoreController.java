package org.omsf.store.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.omsf.store.model.Menu;
import org.omsf.store.model.Store;
import org.omsf.store.service.MenuService;
import org.omsf.store.service.StoreService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController {
	
	private final StoreService storeService;
//	private final ReviewService reviewService;
	private final MenuService menuService;
	
	@GetMapping("/kakaomap")
	public String testKakaoMap() {
		return "store/kakaomap";
	}
	
	@PostMapping("/create")
	@Transactional
	public String createStore(HttpServletRequest request, Store store, ArrayList<MultipartFile> files) {
		//storevo db에 저장
		// 등록할때 사진이 있다면 storeno를 받고  대표사진을 저장
		// (메인페이지 선택)로 이동
		int storeNo = storeService.createStore(store);
		
		if (files != null) {
			int pictureNo = storeService.UploadImage(files, storeNo);
			store.setPicture(pictureNo);
			storeService.updateStore(store);
		}
		
		return "index";
	}


    @GetMapping("/{storeNo}")
    public String storeDetail(@PathVariable("storeNo") int storeNo, Model model) {
        Store store = storeService.getStoreByNo(storeNo);
//       ReviewService.getReviewListBystoreNo(storeNo); 최신5개만 + 페이징
        List<Menu> menus = menuService.getMenusByStoreNo(storeNo);
        
        model.addAttribute("store", store);
        model.addAttribute("menus", menus);
        return "storeDetail";
    }
}
