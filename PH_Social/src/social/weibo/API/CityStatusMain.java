package social.weibo.API;

import java.io.IOException;
import java.text.ParseException;

import org.json.JSONException;


public class CityStatusMain {
	public static void main(String args[]){
		if(args.length!=1){
			System.out.println("Usage: java -jar PH_Social_fat.jar conf_file");
			System.exit(0);
		}
		String conf = args[0];
			CityStatuses s = new CityStatuses(conf);
			s.execute();
		

	}
}
