package com.epay.ewallet.stresstestservice.mapperOne;

import com.epay.ewallet.stresstestservice.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface IUser {

	User getUserByPhoneNumber(String phone);

	User getUserById(@Param("id") Long id);
	
	Map<String, String> getUserFullNameByPhone(@Param("phone") String phone);

}
