import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;


public class Test {
	public static void main(String args[]) throws Exception {
		String line="";
		String result="";
		URL cityList = 
				new URL("https://api.weibo.com/2/statuses/public_timeline.json?access_token=2.00J3GbOCfOW51D6243214cf0aDRpWE&count=1");
		BufferedReader in = new BufferedReader(new InputStreamReader(
				cityList.openStream()));
		while ((line = in.readLine()) != null) {
			result += line;
		}
		System.out.println(result);
    }
}
