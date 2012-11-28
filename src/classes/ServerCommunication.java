package classes;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import activities.MainActivity;
import android.util.Log;


public class ServerCommunication {
	
	/** What ip has the Server */
	public static String SERVER_IP="";
	/**
	 * Set the current serverIP
	 * @param serverIP
	 */
	public static void setSERVER_IP(String serverIP)
	{
		SERVER_IP = serverIP;
	}
	
	/**
	 * TODO: Update the name of each table on post
	 */
	public static void SendMovieDataToDB(Movie movie){
		
		URI url;
		try{
			url = new URI(SERVER_IP + "/movie/movie/reception/");
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(url);
			
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			pairs.add(new BasicNameValuePair("moviename", movie.getMovieName()));
			if(!MainActivity.gender.equalsIgnoreCase("n/a"))
				pairs.add(new BasicNameValuePair("gender", movie.getGender()));
			else
				pairs.add(new BasicNameValuePair("gender", "unknown"));
			
			if(!MainActivity.age.equalsIgnoreCase("n/a"))
				pairs.add(new BasicNameValuePair("age", ""+movie.getAge()));
			else
				pairs.add(new BasicNameValuePair("age", "unknown"));
			
			pairs.add(new BasicNameValuePair("EDA", ""+movie.getAverageEda()));
			
			HttpResponse response = client.execute(post);
			
			if(response.getStatusLine().getStatusCode() == 200){
				Log.i("current","Sent the movie to database");
			}
			
			
		}catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
