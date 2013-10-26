package social.weibo.API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

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

	public CityStatuses(Date beginTime, Date endTime, float lat1, float lon1,
			float lat2, float lon2, String confFile) {
		this.lat1 = lat1;
		this.lat2 = lat2;
		this.lon1 = lon1;
		this.lon2 = lon2;
		this.beginTime = beginTime;
		this.endTime = endTime;
		this.conf = ConfProperties.getProperties(confFile);
	}

	public int NearbyStatuses(float lat, float lon, int range)
			throws IOException, JSONException, ParseException, InterruptedException {
		int page = 1;
		int count = 0;
		int rest_time2 = Integer.parseInt(conf.getProperty("rest_time2"))*60*1000;
		String tmp = getData(lat, lon, range, page);
		while (!tmp.equals("[]")) {
			JSONObject obj = new JSONObject(tmp);
			JSONArray arr = obj.getJSONArray("statuses");
			for (int i = 0; i < arr.length(); i++) {
				if(!checkStatus(arr.getJSONObject(i))){
					String json = extractInfo(arr.getJSONObject(i));
					storeToMongodb(json);
					count++;
				}
			}
			page++;
			Thread.sleep(rest_time2);
			tmp = getData(lat, lon, range, page);
		}
		return count;
	}

	private boolean checkStatus(JSONObject obj) throws UnknownHostException,
			JSONException {
		
		long id = obj.getLong("id");
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB(conf.getProperty("db"));
		DBCollection coll = db.getCollection(conf.getProperty("collection"));
		BasicDBObject query = new BasicDBObject("id", id);
		DBCursor cursor = coll.find(query);
		if (cursor.hasNext()) {
			return true;
		}
		return false;
	}

	private String extractInfo(JSONObject obj) throws JSONException {
		String user_location = obj.getJSONObject("user").getString("location");
		String text = obj.getString("text");
		String geo = obj.getString("geo");
		String created_at = obj.getString("created_at");
		long id = obj.getLong("id");
		String json = "{\"id\":" + id + ",\"created_at\":\"" + created_at
				+ "\"," + "\"text\":\"" + text + "\",\"user\":{\"location\":\""
				+ user_location + "\"}," + "\"geo\":" + geo + "}";
		return json;
	}

	private void storeToMongodb(String json) throws UnknownHostException {
		MongoClient mongoClient = new MongoClient();
		DB db = mongoClient.getDB(conf.getProperty("db"));
		DBCollection coll = db.getCollection(conf.getProperty("collection"));
		DBObject dbObject = (DBObject) JSON.parse(json);
		coll.insert(dbObject);
	}

	private String getData(float lat, float lon, int range, int page) throws IOException, ParseException, InterruptedException{
		System.out.println("page: " + page);
		int rest_time1 = 60*1000*Integer.parseInt(conf.getProperty("rest_time1"));
		String tmp=CallAPI(lat, lon, range, page);
		System.out.println(tmp.substring(0, 2));
		if(tmp.equals("[]")){
			Thread.sleep(rest_time1);
			tmp=CallAPI(lat, lon, range, page);
			System.out.println(tmp.substring(0, 2));
		}
		return tmp;
	}
	private String CallAPI(float lat, float lon, int range, int page)
			throws IOException, ParseException {
		String line = "";
		String result = "";
		String startTime = conf.getProperty("startTime");
		String endTime = conf.getProperty("endTime");
		long startT = 0;
		long endT = 0;
		startT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime)
				.getTime() / 1000;
		endT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime)
				.getTime() / 1000;
		String url = conf.getProperty("nearby_timeline") + "?access_token="
				+ conf.getProperty("AccessToken") + "&lat=" + lat + "&long="
				+ lon + "&range=" + range + "&count="
				+ conf.getProperty("count") + "&starttime=" + startT
				+ "&endtime=" + endT + "&page=" + page;
		System.out.println(url);
		URL cityList = new URL(url);
		BufferedReader in = new BufferedReader(new InputStreamReader(
				cityList.openStream()));
		while ((line = in.readLine()) != null) {
			result += line;
		}
		
		return result;
	}

}
