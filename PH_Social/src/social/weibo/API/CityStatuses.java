package social.weibo.API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
	private Properties conf = null;
	public CityStatuses(String confFile) {
		this.conf = ConfProperties.getProperties(confFile);
	}

	public void execute() throws IOException, JSONException, ParseException, InterruptedException{
		float lat_north=Float.parseFloat(conf.getProperty("lat_north"));
		float lat_south=Float.parseFloat(conf.getProperty("lat_south"));
		float lon_west=Float.parseFloat(conf.getProperty("lon_west"));
		float lon_east=Float.parseFloat(conf.getProperty("lon_east"));
		int range = Integer.parseInt(conf.getProperty("range"));
		float step = Float.parseFloat(conf.getProperty("step"));
		for(float y=lat_south;y<=lat_north;y=y+step){
			for(float x=lon_west;x<=lon_east;x=x+step){
				int count = NearbyStatuses(y, x, range);
				System.out.println("("+x+","+y+"): " + count + " records added");
			}
		}
	}
	
	private int NearbyStatuses(float lat, float lon, int range)
			throws IOException, JSONException, ParseException, InterruptedException {
		int page = 1;
		int count = 0;
		int rest_time2 = Integer.parseInt(conf.getProperty("rest_time2"))*1000;
		String tmp = getPage(lat, lon, range, page);
		while (!tmp.equals("[]")) {
			JSONObject obj = new JSONObject(tmp);
			JSONArray arr = obj.getJSONArray("statuses");
			int num=0;
			for (int i = 0; i < arr.length(); i++) {
				if(!checkStatus(arr.getJSONObject(i))){
					String json = extractInfo(arr.getJSONObject(i));
					storeToMongodb(json);
					num++;
				}
			}
			System.out.println("page " + page + ": " + num + " new statuses added");
			count = count + num;
			page++;
			Thread.sleep(rest_time2);
			tmp = getPage(lat, lon, range, page);
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

	private String getPage(float lat, float lon, int range, int page) throws IOException, ParseException, InterruptedException{
		int rest_time1 = 1000*Integer.parseInt(conf.getProperty("rest_time1"));
		String tmp=CallAPI(lat, lon, range, page);
		if(tmp.equals("[]")){
			Thread.sleep(rest_time1);
			tmp=CallAPI(lat, lon, range, page);
		}
		if(tmp.equals("[]")){
			System.out.println("page " + page + ": empty");
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
		URL cityList = new URL(url);
		BufferedReader in = new BufferedReader(new InputStreamReader(
				cityList.openStream()));
		while ((line = in.readLine()) != null) {
			result += line;
		}
		
		return result;
	}

}
