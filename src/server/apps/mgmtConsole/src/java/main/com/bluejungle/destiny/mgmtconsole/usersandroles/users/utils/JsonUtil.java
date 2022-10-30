package com.bluejungle.destiny.mgmtconsole.usersandroles.users.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
	
	public static String toJsonString(Object obj)
		throws JsonProcessingException {
    	ObjectMapper objectMapper = new ObjectMapper();
    	
    	return objectMapper.writeValueAsString(obj);
	}
}
