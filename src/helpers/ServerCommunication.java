package helpers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import activities.MainActivity;
import android.util.Log;
import classes.Movie;


public class ServerCommunication {
	
	/** A given timeout used when connecting to the server */
	private static final int TIMEOUT = 5000;
	
	/** What ip has the Server */
	public static String SERVER_IP="http://130.240.98.42";
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
		int timeoutConnection = TIMEOUT;
		HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used. 
		int timeoutSocket = TIMEOUT;
		HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);
		
		HttpClient httpClient = new DefaultHttpClient(httpParams);
		HttpPost httppost = new HttpPost(SERVER_IP+"/movie/movie/reception");

		try{
			
			//Add the movie parameters to post
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			
			pairs.add(new BasicNameValuePair("movie", movie.getMovieName()));
			pairs.add(new BasicNameValuePair("imdbId", movie.getImdbId()));
			
			if(!MainActivity.age.equalsIgnoreCase("n/a"))
				pairs.add(new BasicNameValuePair("age", movie.getAge()));
			else
				pairs.add(new BasicNameValuePair("age", "unknown"));
			
			if(!MainActivity.gender.equalsIgnoreCase("n/a"))
				pairs.add(new BasicNameValuePair("gender", movie.getGender()));
			else
				pairs.add(new BasicNameValuePair("gender", "unknown"));
			
			pairs.add(new BasicNameValuePair("EmoLvl", ""+movie.getAverageEda()));
			
			httppost.setEntity(new UrlEncodedFormEntity(pairs));
			
			//Prints out the post
			HttpEntity before = httppost.getEntity();
			String beforetxt = EntityUtils.toString(before);
			Log.i("Database", beforetxt);
			
			//Do the post
			httpClient.getConnectionManager();
			HttpResponse response = httpClient.execute(httppost);
			
			//Prints out the result after post
			HttpEntity entity = response.getEntity();
			String responseText = EntityUtils.toString(entity);
			
			//If Post was OK
			if(response.getStatusLine().getStatusCode() == 200){
				Log.i("Database", "Movie Name "+movie.getMovieName());
				Log.i("Database", "Age: " + movie.getAge());
				Log.i("Database", "Gender: " + movie.getGender());
				Log.i("Database", "Eda " +movie.getAverageEda());
				Log.i("Database","Sent the movie to database");
				Log.i("Database", responseText);
			}
			
			
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static List<Movie> getCommunityMovies(String gender, String age){
		List<Movie> communityMovies = new ArrayList<Movie>();
		
		HttpParams httpParams = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used. 
		int timeoutConnection = TIMEOUT;
		HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used. 
		int timeoutSocket = TIMEOUT;
		HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);
		HttpClient httpClient = new DefaultHttpClient(httpParams);

		try {
			String url;
			//Formats url 
			if(gender.equalsIgnoreCase("male"))
				url = SERVER_IP+"/movie/movie/request?age="+age+"&gender=M";
			else
				url = SERVER_IP+"/movie/movie/request?age="+age+"&gender=F";
					

			Log.i("Database", url);
			HttpResponse response = httpClient.execute(new HttpGet(url));
			httpClient.getConnectionManager().closeExpiredConnections();
	
			HttpEntity entity = response.getEntity();
			String responseText = EntityUtils.toString(entity);

			communityMovies = ParseXMLStringToList.getMoviesForAgeAndGender(responseText, gender, age);
			
		
			return communityMovies;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return communityMovies;
	}
	
	
	public static List<Movie> getIMDBMovies(String movieName){
		List<Movie> imdbMovies = new ArrayList<Movie>();
		
		HttpParams httpParams = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used. 
		int timeoutConnection = TIMEOUT;
		HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used. 
		int timeoutSocket = TIMEOUT;
		HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);
		HttpClient httpClient = new DefaultHttpClient(httpParams);

		try {
			String url;
			url = SERVER_IP+"/movie/movie/imdbCheck?movie="+movieName;
					

			Log.i("Database", url);
			HttpResponse response = httpClient.execute(new HttpGet(url));
			httpClient.getConnectionManager().closeExpiredConnections();
	
			HttpEntity entity = response.getEntity();
			String responseText = EntityUtils.toString(entity);
			Log.i("Database", responseText);
			imdbMovies = ParseXMLStringToList.getMoviesFromImdbXMLByName(responseText, movieName);
			
			return imdbMovies;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return imdbMovies;
	}
	
}
