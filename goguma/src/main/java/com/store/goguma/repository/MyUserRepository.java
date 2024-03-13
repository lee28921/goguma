package com.store.goguma.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.store.goguma.user.dto.my.UserEmojiDTO;

@Mapper
public interface MyUserRepository {
	// 유저 결제 내역
	public List<UserEmojiDTO> findEmojiHistoryByUser(@Param("uId") Integer uId, @Param("start") Integer start);
	
	// 유저 결제 내역 갯수
	public int countEmojiHistoryByUser(Integer uId);
}
