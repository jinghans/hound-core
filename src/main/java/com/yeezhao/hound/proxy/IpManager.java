package com.yeezhao.hound.proxy;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpManager {
	public static final int BATCH_REQUEST_PROXY_SIZE = 30;
	public static final Logger LOG = LoggerFactory.getLogger(IpManager.class);
	private IIpServiceOperator service;
	private LinkedBlockingQueue<IpObject> pool;
	
	public IpManager(IIpServiceOperator service){
		this.service = service;
		this.pool = new LinkedBlockingQueue<IpObject>();
	};
	public int getCurrentPoolSize(){
		return pool.size();
	}
	public IIpServiceOperator getService() {
		return service;
	}
	public void setService(IIpServiceOperator service) {
		this.service = service;
	}

	public List<IpObject> getIps(int count) throws IpServiceException, InterruptedException{
		if(pool.size()<count){
			batchLoadIpsFromService(BATCH_REQUEST_PROXY_SIZE);
		}

		List<IpObject> ips = new LinkedList<IpObject>();
		for(int i = 0; i<count; i++){
			IpObject ip = pool.take();
			if(ip != null){
				ips.add(ip);
			}
		}
		return ips;
	}
	
	private void batchLoadIpsFromService(int loadSize) throws IpServiceException{
		try{
			if(loadSize<=0){
				return;
			}
			List<IpObject> newIps = service.getIps(loadSize);
			if(newIps==null||newIps.isEmpty()){
				throw new IpServiceException("can not get ip");
			}
			pool.addAll(newIps);
			System.out.println("succeed loadIpsFromService size: " + loadSize);
		} catch(Exception e){
			System.out.println("fail loadIpsFromService: " + e.getMessage());
		}
	}
	
}
