package social.weibo.API;

import java.io.IOException;
import java.text.ParseException;

import org.json.JSONException;


public class CityStatusTest {
	public static void main(String args[]){
		try {
			CityStatuses s = new CityStatuses("WeiboAPIconf");
			s.execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
