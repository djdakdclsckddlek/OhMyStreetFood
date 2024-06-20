package org.omsf.store.dao;

import java.util.List;
import java.util.Optional;

import org.omsf.store.model.Photo;
import org.omsf.store.model.Store;

public interface StoreRepository {
	     
	List<Store> selectAllStore();
	List<Store> getStoreByposition();
	
	Optional<Store> getStoreByNo(int storeNo);
	void createStore(Store store);
	int getStoreNo();
	void updateStore(Store store);
	void deleteStore(int storeNo);
	
	void updateTotalReviewAndRating(Store store);
	void updateLikes(Store store);
	void createPhoto(Photo photo);
	void deletePhoto(int PhotoNo);
}
