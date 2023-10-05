package com.epay.ewallet.stresstestservice.mapperOne;

import com.epay.ewallet.stresstestservice.model.Ecode;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;

@Mapper
public interface IEcode {

	HashMap<String, String> getMessageByCode(HashMap<String, String> map);

	Ecode getEcode(HashMap<String, String> map);
}
