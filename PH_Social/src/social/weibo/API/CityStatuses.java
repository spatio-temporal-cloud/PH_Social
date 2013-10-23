package social.weibo.API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import social.conf.ConfProperties;


public class CityStatuses {
	private float lat1;
	private float lon1;
	private float lat2;
	private float lon2;
	public float getLat1() {
		return lat1;
	}
	public float getLon1() {
		return lon1;
	}
	public float getLat2() {
		return lat2;
	}
	public float getLon2() {
		return lon2;
	}
	public void setLat1(float lat1) {
		this.lat1 = lat1;
	}
	public void setLon1(float lon1) {
		this.lon1 = lon1;
	}
	public void setLat2(float lat2) {
		this.lat2 = lat2;
	}
	public void setLon2(float lon2) {
		this.lon2 = lon2;
	}

	private Date beginTime;
	private Date endTime;
	public Date getBeginTime() {
		return beginTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	private String cityName;
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	
	private Properties conf = null;
	
	
	
	public Properties getConf() {
		return conf;
	}
	public void setConf(Properties conf) {
		this.conf = conf;
	}
	
	public CityStatuses (Date beginTime, Date endTime, 
			float lat1, float lon1, float lat2, float lon2, String confFile){
		this.lat1 = lat1;
		this.lat2 = lat2;
		this.lon1 = lon1;
		this.lon2 = lon2;
		this.beginTime = beginTime;
		this.endTime = endTime;
		this.conf = ConfProperties.getProperties(confFile);
	}
	
	private ArrayList<String> NearbyStatuses(float lat, float lon, int range){
		return null;
	}
	
	private String CallAPI(float lat, float lon, int range,int page) throws IOException{
		String line="";
		String result="";
		String url = conf.getProperty("nearby_timeline") + 
				"?access_token=" + conf.getProperty("AccessToken") + 
				"&lat="+lat+"&long="+lon+"&range="+range+"&page="+page+
				"count="+conf.getProperty("count");
		URL cityList = 
				new URL(url);
		BufferedReader in = new BufferedReader(new InputStreamReader(
				cityList.openStream()));
		while ((line = in.readLine()) != null) {
			result += line;
		}
		return result;
	}
	
}
