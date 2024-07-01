package org.omsf.store.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Menu {
	private int menuNo;
	private String menuName;
	private long price;
	private Timestamp createdAt;
	private Timestamp modifiedAt;
	
	private int storeNo;
	
}