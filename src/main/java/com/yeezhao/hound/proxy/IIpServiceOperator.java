package com.yeezhao.hound.proxy;

import java.util.List;

public interface IIpServiceOperator {
	public List<IpObject> getIps(int count) throws IpServiceException;
//	public void setOrderId(String orderId);
//	public void setIpLocation(String location);
//	public void setHost(String host);
//	public void setIsNew(boolean isNew);
}