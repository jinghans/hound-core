package com.yeezhao.hound.core.loc;

import java.util.Map;

import org.junit.Test;
import com.yeezhao.hound.core.GoogleSev;

public class TestGoogleServ {
	
	@Test
	public void testGetLocation(){
		String locValue = "Columbia University";
		GoogleSev gserv = new GoogleSev();
		Map<String, String> result = gserv.getInfoGoogle(locValue);
		System.out.println(result);
	}
	
}
