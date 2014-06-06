package com.yeezhao.hound.proxy;

public class IpObject {
	public static final String NOT_USE_LOCATION_FLAG = "all";
	public static enum IpQueueType{
		BATCH(0, "batch"), RECENCY(1, "recency");
		private int type;
		private String name;
		private IpQueueType(int type, String name){
			this.type=type;
			this.name=name;
		}
		public int getType(){ return type;}
		public String getName(){ return name;}
		@Override
		public String toString(){ return name;}
		public static IpQueueType getQueueType(boolean isNew){
			if(isNew)
				return RECENCY;
			else
				return BATCH;
		}
	}

	private IpQueueType queueType;
	private String host;
	private int port;
	private String location = "unknown";
	private long nextUseTime = System.currentTimeMillis();
	
	public IpObject() {
		super();
	}
	public IpObject(IpQueueType queueType, String host, int port,
			String location) {
		super();
		this.queueType = queueType;
		this.host = host;
		this.port = port;
		this.location = location!=null?location:this.location;
	}
	public IpQueueType getQueueType() {
		return queueType;
	}
	public void setQueueType(IpQueueType queueType) {
		this.queueType = queueType;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getIpString(){
		return host+":"+port;
	}
	@Override
	public String toString() {
		return "IpObject [queueType=" + queueType + ", host=" + host
				+ ", port=" + port + ", location=" + location + "]";
	}
	@Override
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((host == null) ? 0 : host.hashCode());
	    return result;
	}
	@Override
	public boolean equals(Object obj) {
	    if (this == obj)
		return true;
	    if (obj == null)
		return false;
	    if (getClass() != obj.getClass())
		return false;
	    IpObject other = (IpObject) obj;
	    if (host == null) {
		if (other.host != null)
		    return false;
	    } else if (!host.equals(other.host))
		return false;
	    return true;
	}
	public long getNextUseTime() {
		return nextUseTime;
	}
	public void setNextUseTime(long nextUseTime) {
		this.nextUseTime = nextUseTime;
	}
}
