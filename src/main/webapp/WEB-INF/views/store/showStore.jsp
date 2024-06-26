<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>OhMyStreetFood!</title>
<!-- Bootstrap CSS -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<!-- Font Awesome CSS -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
<!-- Google Fonts -->
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Noto+Sans+KR:wght@100..900&display=swap" rel="stylesheet">
<!-- Jquery -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
<!-- CSS -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/review.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/like.css">
<!-- JavaScript -->
<script type="text/javascript" src="${pageContext.request.contextPath}/js/addStoreOwner.js"></script>
</head>
<body>
	<div class="main">
		<div class="row">
			<div>
				<a href="${pageContext.request.contextPath}/store/list" style="text-decoration: none; color: inherit;"> 
					<i class="fas fa-arrow-left"></i>
				</a>
			</div>
			
			<span style="display: flex; flex-direction: row; justify-content: space-between;">
                <span><i class="fas fa-flag"></i><strong>&nbsp;사장님 인증 상점</strong></span>
       			<span><a href="#">사장님과 채팅하기</a></span>
        	</span>
        	
		    <div class="card" style="width: 100%; height: auto; border: none;">
		        <div class="row g-0">
		            <div class="col-md-3" style="padding: 0 20px;">
		                <img src="${pageContext.request.contextPath}/img/00.jpg" class="card-img-top rounded-circle" alt="사진" style="max-width: 120px; height: auto;">
		            </div>
		            <div class="col-md-9 card-body" style="padding: 0 20px;">
		                    <span style="display: flex; flex-direction: row; justify-content: space-between;">
			                    <span><h5 class="card-title">${store.storeName}</h5></span>
			                    <span><i id="like-btn" class="far fa-heart"></i></span>
								<!-- <i class="fas fa-heart"></i> -->
		                    </span>
		                    <p class="card-text">${store.introduce}</p>
		                    <p class="card-text">
			                		리뷰 ${store.totalReview}
			                		평점 ${store.totalRating}
			                		찜 ${store.likes}
			                </p>
		            		<span><small class="text-muted">업데이트 ${store.modifiedAt}</small></span>
		            </div>
		        </div>
		    </div>
		    
		    <div>
		    	<span style="display: flex; flex-direction: row; justify-content: space-between; margin-bottom: 20px;">
                  <span><h5>가게 정보</h5></span>
                  <span>정보 수정</span>
                 </span>
                 
                <span style="display: flex; flex-direction: row; justify-content: space-between; margin-bottom: 10px;">
			        <span>운영일자</span>
			        <span>${store.operatingDate}</span>
			    </span>
				
				<span style="display: flex; flex-direction: row; justify-content: space-between; margin-bottom: 10px;">
			        <span>운영시간</span>
			        <span>${store.operatingHours}</span>
			    </span>
			    
			    <span style="display: flex; flex-direction: row; justify-content: space-between; margin-bottom: 10px;">
			        <span>메뉴</span>
			        <span>&nbsp;</span>
			    </span>
			    
			    <c:forEach items="${menus}" var="menu"> 
				    <span style="display: flex; flex-direction: row; justify-content: space-between; margin-bottom: 10px;">
				        <span>- ${menu.menuName}</span>
				        <span>${menu.price}</span>
				    </span>
				 </c:forEach>
		    </div>

			<!-- KAKAO MAP API -->
			<div>
				<div class="col-md-12" id="map" style="width: 100%; height: 200px; border-radius: 20px"></div>
				<div class="col-md-12" id="result"></div>
			</div>
			
			<div>
		    	<span style="display: flex; flex-direction: row; justify-content: space-between; margin-bottom: 20px;">
                  <span><h5>사진 갤러리</h5></span>
                  <span>더보기</span>
                 </span>
                 
               <span class="gallery-container">
		  	    <span class="gallery-item"><img src="https://via.placeholder.com/140" alt="사진 1" class="gallery-img"></span>
			    <span class="gallery-item"><img src="https://via.placeholder.com/140" alt="사진 2" class="gallery-img"></span>
			    <span class="gallery-item"><img src="https://via.placeholder.com/140" alt="사진 3" class="gallery-img"></span>
			    <span class="gallery-item"><img src="https://via.placeholder.com/140" alt="사진 4" class="gallery-img"></span>
			    <span class="gallery-item"><img src="https://via.placeholder.com/140" alt="사진 5" class="gallery-img"></span>
			  </span>
			</div>

			<div>
		    	<span style="display: flex; flex-direction: row; justify-content: space-between; margin-bottom: 20px;">
                  <span><h5>리뷰 정보</h5></span>
                  <button id="openModalBtn">리뷰작성</button>
                 </span>
                 
                <span style="display: flex; flex-direction: column; justify-content: center; align-items: center; width: 100%; height: auto; background-color:#f6f6f6; border-radius:10px; margin-bottom: 20px;">
				    <span style="padding: 20px">
					    <i class="fas fa-star"></i>
					    <i class="fas fa-star"></i>
					    <i class="fas fa-star"></i>
					    <i class="far fa-star"></i>
					    <i class="far fa-star"></i>
				    </span>
				</span>
				
				<div style="width: 100%; height: auto; background-color:#f6f6f6; border-radius:10px; margin-bottom: 20px;">
			    	<span style="display: flex; flex-direction: row; justify-content: space-between;">
				    	<span>닉네임</span>
				    	<span>작성일자</span>
				    </span>
					<span>내가 먹었던 붕어빵 중에서 가장 맛있었음다. 겉바속촉.. 존맛탱.. 슈크림붕어빵이 진리임니다리.. 사장님이 친절하고 붕어빵이 맛있어요 냠냠</span>
				</div>			    
				
				<div style="width: 100%; height: auto; background-color:#f6f6f6; border-radius:10px; margin-bottom: 20px;">
			    	<span style="display: flex; flex-direction: row; justify-content: space-between;">
				    	<span>닉네임</span>
				    	<span>작성일자</span>
				    </span>
					<span>내가 먹었던 붕어빵 중에서 가장 맛있었음다. 겉바속촉.. 존맛탱.. 슈크림붕어빵이 진리임니다리.. 사장님이 친절하고 붕어빵이 맛있어요 냠냠</span>
				</div>
				
				<div class="col-md-12 text-center">
					<a href="${pageContext.request.contextPath}/review/list/${store.storeNo}">
						<spring:message code="review.more" />
					</a>
				</div>
				<a href="${pageContext.request.contextPath}/store/report/${store.storeNo}">신고하기</a>
		    </div>
		    
		    <!-- 찜 목록 효과 -->
		    <div id="notification-insert" class="notification">찜 목록에 추가되었습니다.</div>
		    <div id="notification-delete" class="notification">찜 목록에 제외되었습니다.</div>
		</div>
	</div>
	
	<!-- 모달 화면 -->
	<div id="modal" class="modal" <c:if test="${modalOn}"> style="display: block;"</c:if>>
        <div class="modal-content">
			<div class="review-container">
				<span class="close-button">&times;</span>
				<h1><spring:message code="review.write" /></h1>
				<form:form id="reviewForm" method="post" modelAttribute="requestReview" action="${pageContext.request.contextPath}/review/insert">
					<p>
						<form:hidden path="storeStoreNo" />
						<form:errors path="storeStoreNo" id="error"/>
					</p>
					<label for="username"><spring:message code="user" /></label>
					<form:input path="memberUsername" disabled="true"/>
					<form:hidden path="memberUsername" />
					
					<label for="rating"><spring:message code="rating" /></label>
					<div class="rating">
						<form:radiobutton path="rating" value="1" id="star1"/>
						<label for="star1" title="1 stars"><i class="fas fa-star"></i></label>
						<form:radiobutton path="rating" value="2" id="star2"/>
						<label for="star2" title="2 stars"><i class="fas fa-star"></i></label>
						<form:radiobutton path="rating" value="3" id="star3"/>
						<label for="star3" title="3 stars"><i class="fas fa-star"></i></label>
						<form:radiobutton path="rating" value="4" id="star4"/>
						<label for="star4" title="4 stars"><i class="fas fa-star"></i></label>
						<form:radiobutton path="rating" value="5" id="star5"/>
						<label for="star5" title="5 stars"><i class="fas fa-star"></i></label>
					</div>
					<label for="content"><spring:message code="review.content" /></label>
					<form:textarea path="content" rows="5" />
					<c:if test="${not empty errors}">
				        <ul>
				            <c:forEach var="error" items="${errors}">
				                <div style="text-align: center;">
									<span id="error">${error.defaultMessage}</span>
								</div>
				            </c:forEach>
				        </ul>
				    </c:if>
					<input id="review-btn" type="submit" value="<spring:message code="write.btn" />" />
				</form:form>
			</div>
        </div>
    </div>

	<!-- kakaoMap API key -->
	<script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=d42b402c7a6ae8d76807bdcfbc3a1b41&libraries=services,clusterer,drawing"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/js/kakaoMapInput.js"></script>

	<!-- 리뷰 모달 -->
	<script src="${pageContext.request.contextPath}/js/modal.js"></script>
	
	<!-- like 요청 -->
	<script src="${pageContext.request.contextPath}/js/likeRequest.js"></script>

</body>
</html>
