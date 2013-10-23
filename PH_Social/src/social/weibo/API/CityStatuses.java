package social.weibo.API;

import java.util.Date;

import javax.xml.crypto.Data;

public class CityStatuses {
	private long lat1;
	private long lon1;
	private long lat2;
	private long lon2;
	public long getLat1() {
		return lat1;
	}
	public long getLon1() {
		return lon1;
	}
	public long getLat2() {
		return lat2;
	}
	public long getLon2() {
		return lon2;
	}
	public void setLat1(long lat1) {
		this.lat1 = lat1;
	}
	public void setLon1(long lon1) {
		this.lon1 = lon1;
	}
	public void setLat2(long lat2) {
		this.lat2 = lat2;
	}
	public void setLon2(long lon2) {
		this.lon2 = lon2;
	}

	private Date beginTime;
	private Data endTime;
	public Date getBeginTime() {
		return beginTime;
	}
	public Data getEndTime() {
		return endTime;
	}
	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}
	public void setEndTime(Data endTime) {
		this.endTime = endTime;
	}
	
	
}
