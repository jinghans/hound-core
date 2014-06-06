package com.yeezhao.hound.core.loc;

import org.junit.Test;

import com.yeezhao.hound.core.JudgeLoc;

public class TestLocationFormatter {
	
	@Test
	public void testFormat(){
		JudgeLoc locer = new JudgeLoc();
		String locValue = "94 Weijin Rd, Nankai, Tianjin, China";
		locValue = "Atlanta, GA";
		locValue = "21 Lower Kent Ridge Road, Singapore";
		locValue = "16,AIU House, Comrade Indrajit Gupta Marg (Kotla Marg), New Delhi,Delhi, Kotla Marg, Mata Sundari Railway Colony, Mandi House, New Delhi, DL 110002, India";
		locValue = "薄扶林, Hong Kong ‎";
//		locValue = "No. 45, Shifu Rd, Taiwan 110 ";
//		locValue = "Harrisburg, Pennsylvania Area";
//		locValue = "Reims Area, France";
		locValue = "Shenzhen, Guangdong, China";
//		locValue = "Asia, China, Northeast China, Heilongjiang";
		String formatLoc = locer.formatLocation(locValue);
		String chAddr = locer.toChAddress(formatLoc);
		System.out.println(formatLoc);
		System.out.println(chAddr);
	}
	
	@Test
	public void testToChAddress(){
		String enAddr = "America#United States#Georgia";
		JudgeLoc locer = new JudgeLoc();
		String chAddr = locer.toChAddress(enAddr);
		System.out.println(chAddr);
	}
}
