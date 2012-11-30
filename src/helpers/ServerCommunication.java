package helpers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import classes.Movie;

import activities.MainActivity;
import android.util.Log;


public class ServerCommunication {
	
	/** What ip has the Server */
	public static String SERVER_IP="http://130.240.99.19";
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
		
		HttpParams httpParams = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used. 
		int timeoutConnection = 50000;
		HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used. 
		int timeoutSocket = 50000;
		HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);
		
		HttpClient httpClient = new DefaultHttpClient(httpParams);
		HttpPost httppost = new HttpPost(SERVER_IP+"/movie/movie/reception");

		try{
		
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			
			pairs.add(new BasicNameValuePair("movie", movie.getMovieName()));
			
			if(!MainActivity.age.equalsIgnoreCase("n/a"))
				pairs.add(new BasicNameValuePair("age", ""+10));
			else
				pairs.add(new BasicNameValuePair("age", "unknown"));
			
			if(!MainActivity.gender.equalsIgnoreCase("n/a"))
				pairs.add(new BasicNameValuePair("gender", movie.getGender()));
			else
				pairs.add(new BasicNameValuePair("gender", "unknown"));
			
			pairs.add(new BasicNameValuePair("EmoLvl", ""+movie.getAverageEda()));
			
			httppost.setEntity(new UrlEncodedFormEntity(pairs));
			
			httpClient.getConnectionManager();
			HttpResponse response = httpClient.execute(httppost);
			
			if(response.getStatusLine().getStatusCode() == 200){
				Log.i("Database", "Movie Name "+movie.getMovieName());
				Log.i("Database", "Age: " + movie.getAge());
				Log.i("Database", "Gender: " + movie.getGender());
				Log.i("Database", "Eda " +movie.getAverageEda());
				Log.i("Database","Sent the movie to database");
			}
			
			
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
