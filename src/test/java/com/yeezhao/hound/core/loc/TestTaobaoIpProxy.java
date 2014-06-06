package com.yeezhao.hound.core.loc;


import java.util.List;

import org.junit.Test;

import com.yeezhao.hound.proxy.IpObject;
import com.yeezhao.hound.proxy.IpServiceException;
import com.yeezhao.hound.proxy.IpServiceOperatorT1;

public class TestTaobaoIpProxy {
	
	@Test
	public void testTaobaoProxy() throws IpServiceException{
		IpServiceOperatorT1 serv = new IpServiceOperatorT1();
		List<IpObject> ips = serv.getIps(10);
		for(IpObject ip : ips){
			System.out.println(ip.getHost() + "," + ip.getPort());
		}
	}
}



