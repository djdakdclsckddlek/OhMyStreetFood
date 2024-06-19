package org.omsf.store.model;

import java.sql.Date;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class StoreVO {
	
	private int storeNo;
	private String storeName;
	private double latitude;
	private double longitude;
	private String address;
	//private byte[] pricture;
	private String introduce;
	private String operatingDate;
	private String operatingHours;
	private int totalReview;
	private double totalRating;
	private int likes;
	private Date createdAt;
	private Date modifiedAt;
	//private UserVO user;
}
