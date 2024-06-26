package org.omsf.store.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.omsf.review.model.RequestReview;
import org.omsf.store.model.Like;
import org.omsf.store.model.Menu;
import org.omsf.store.model.Store;
import org.omsf.store.model.StorePagination;
import org.omsf.store.service.LikeService;
import org.omsf.store.service.MenuService;
import org.omsf.store.service.StoreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController {
	
	private final StoreService storeService;
	private final MenuService menuService;
//	private final ReviewService reviewService;
	private ObjectMapper objectMapper = new ObjectMapper();
	private final LikeService likeService;

	@GetMapping("/addbygeneral")
	public String showAddStoreGeneralPage() {
	    return "store/addStoreGeneral";
	}
	
	@PostMapping("/createstore")
	@ResponseBody
	public ResponseEntity<String> createStore(

			@RequestParam(value= "store") String storeJson,
			@RequestParam(value = "photo", required = false) ArrayList<MultipartFile> picture,
			@RequestParam(value = "menus", required = false) String menusJson,
	        Principal principal,
	        RedirectAttributes redirectAttributes
	) throws IOException {
		
		Store store = objectMapper.readValue(storeJson, Store.class);
		List<Menu> menus = objectMapper.readValue(menusJson, new TypeReference<List<Menu>>() {});
		store.setUsername(principal.getName());
	
		int storeNo = storeService.createStore(store);
        if (picture != null && !picture.isEmpty()) {
        	int photoNo = storeService.UploadImage(picture, storeNo);
        	store.setPicture(photoNo);
        	storeService.updateStore(store);
        }
        
        menus.stream().peek(menu -> menu.setStoreNo(storeNo))
        .forEach(menuService::createMenu);
        
        return ResponseEntity.ok("");
	}
	
	@GetMapping("list/page")
	@ResponseBody()
	 public List<Store> storePageWithSorting(
			 	@RequestParam(required = false, defaultValue = "likes") String order,
			 	@RequestParam(defaultValue = "1" ) int page,
			 	@RequestParam(required = false) String keyword,
	            @RequestParam(required = false, defaultValue = "DESC") String sort) {
        
		StorePagination pageRequest = StorePagination.builder()
                                    .currPageNo(page) 
                                    .orderType(order)
                                    .searchType("storeName")
                                    .keyword(keyword)
                                    .sortOrder(sort)
                                    .build();

        return storeService.getStoreList(pageRequest); 
    }
	
	@GetMapping("/list")
	public String showStorePage(Model model,
			@RequestParam(required = false) String orderType) {
		StorePagination pageRequest = StorePagination.builder()
				.orderType(orderType)
                .build();
	    List<Store> stores = storeService.getStoreList(pageRequest);
	    model.addAttribute("stores", stores);
	    
	    //처음 20개 스크롤 + 10개씩
	    return "store";
	}
	
	@GetMapping("/{storeNo}")
	public String showStoreDetailPage(Principal principal, @PathVariable Integer storeNo, Model model) {
		Store store = storeService.getStoreByNo(storeNo);
		List<Menu> menu = menuService.getMenusByStoreNo(storeNo);
		
		model.addAttribute("store", store);
		model.addAttribute("menus", menu);
		
		// leejongseop - 리뷰 작성 폼 바인딩
		if (!model.containsAttribute("requestReview")) {
			RequestReview review = new RequestReview();
			review.setStoreStoreNo(storeNo);
			review.setMemberUsername(principal == null ? "" : principal.getName());
            model.addAttribute("requestReview", review);
        }
		
	    return "store/showStore";
	}
	
	@ResponseBody
	@GetMapping("api")
	public List<Store> getStoresByPosition(@RequestParam(value = "position", defaultValue = "서울 종로구") String position){
		log.info("api 요청 완료");
		return storeService.getStoresByPosition(position);
	}
	
	// leejongseop - like 기능
	@PreAuthorize("isAuthenticated()")
	@ResponseBody
	@PostMapping("like/insert")
	public String insertLike(Principal principal, @RequestBody Like like, Errors errors) {
		log.info("like insert api 요청 완료");
		log.info("like 정보 : {}", like.toString());
		like.setMemberUsername(principal.getName());
		likeService.insertLike(like);
		return "찜 목록에 등록";
	}
	
	@PreAuthorize("isAuthenticated()")
	@ResponseBody
	@DeleteMapping("like/delete")
	public String deleteLike(Principal principal, @RequestBody Like like, Errors errors) {
		log.info("like delete api 요청 완료");
		log.info("like 정보 : {}", like.toString());
		like.setMemberUsername(principal.getName());
		likeService.deleteLike(like);
		return "찜 목록에 제외";
	}
	
	// LIKE돼 있는 지 확인 하는 메소드
	@PreAuthorize("isAuthenticated()")
	@ResponseBody
	@GetMapping("like/check")
	public ResponseEntity<Integer> isLike(Principal principal, Like like, Errors errors) {
		log.info("like check api 요청 완료");
		log.info("like 정보 : {}", like.toString());
		like.setMemberUsername(principal.getName());
		int count = likeService.isLike(like);
		log.info("count 개수 : {}", count);
		return new ResponseEntity<>(count, HttpStatus.OK);
	}
}
