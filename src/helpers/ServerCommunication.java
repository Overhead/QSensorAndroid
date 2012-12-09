package helpers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
	
	/** name for the URL = SERVER_URL_START + main.getIP() + SERVER_URL_END*/
	public final static String SERVER_URL_START = "http://";
	
	/**
	 * TODO: Update the name of each table on post
	 */
	public static void SendMovieDataToDB(Movie movie){
		
		String serverURL = getServerURL() + "/movie/movie/reception";
		try{
			HttpParams httpParams = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is
			// established.
			// The default value is zero, that means the timeout is not used.
			int timeoutConnection = TIMEOUT;
			HttpConnectionParams.setConnectionTimeout(httpParams,
					timeoutConnection);
			// Set the timeout in milliseconds until a connection is
			// established.
			// The default value is zero, that means the timeout is not used.
			int timeoutSocket = TIMEOUT;
			HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);

			HttpClient httpClient = new DefaultHttpClient(httpParams);
			HttpPost httppost = new HttpPost(serverURL);
	
			//Add the movie parameters to post
			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			
			pairs.add(new BasicNameValuePair("movie", movie.getMovieName()));
			pairs.add(new BasicNameValuePair("imdbId", movie.getImdbId()));
			
			MainActivity main = MainActivity.getCurrentMainActivity();
			if (main == null) {
				
			} else {
				String age = main.getAge();
				if(!age.equalsIgnoreCase("n/a"))
					pairs.add(new BasicNameValuePair("age", movie.getAge()));
				else
					pairs.add(new BasicNameValuePair("age", "unknown"));
				
				String gender = main.getGender();
				if(!gender.equalsIgnoreCase("n/a"))
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

			}			
			
			
			
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}catch(Exception e){
			Log.e("Database", serverURL +" URL is Wrong");
		}
	}
	
	private static String getServerURL() {
		MainActivity main = MainActivity.getCurrentMainActivity();
		String serverURL = "";
		if (main != null) {
			String serverIP = main.getServerIP();
			serverURL = SERVER_URL_START+serverIP;
		}
		
		return serverURL;
	}

	public static List<Movie> getCommunityMovies(String gender, String age){
		List<Movie> communityMovies = new ArrayList<Movie>();
		String serverURL = getServerURL();
		try {
			HttpParams httpParams = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is
			// established.
			// The default value is zero, that means the timeout is not used.
			int timeoutConnection = TIMEOUT;
			HttpConnectionParams.setConnectionTimeout(httpParams,
					timeoutConnection);
			// Set the timeout in milliseconds until a connection is
			// established.
			// The default value is zero, that means the timeout is not used.
			int timeoutSocket = TIMEOUT;
			HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);
			HttpClient httpClient = new DefaultHttpClient(httpParams);

			String url;
			//Formats url 
			if(gender.equalsIgnoreCase("male"))
				url = serverURL+"/movie/movie/request?age="+age+"&gender=M";
			else
				url = serverURL+"/movie/movie/request?age="+age+"&gender=F";
					

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
		}catch(Exception e){
			Log.e("Database", serverURL +" URL is Wrong");
		}

		return communityMovies;
	}
	
	
	public static List<Movie> getIMDBMovies(String movieName){
		List<Movie> imdbMovies = new ArrayList<Movie>();
		String serverURL = getServerURL();
		try{
			HttpParams httpParams = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is
			// established.
			// The default value is zero, that means the timeout is not used.
			int timeoutConnection = TIMEOUT;
			HttpConnectionParams.setConnectionTimeout(httpParams,
					timeoutConnection);
			// Set the timeout in milliseconds until a connection is
			// established.
			// The default value is zero, that means the timeout is not used.
			int timeoutSocket = TIMEOUT;
			HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);
			HttpClient httpClient = new DefaultHttpClient(httpParams);

			String url;
			url = serverURL+"/movie/movie/imdbCheck?movie="+movieName;
					

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
			Log.e("Database", "Something wrong with the database connection");
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("Database", "Something wrong with the database connection");
		}catch(Exception e){
			Log.e("Database", serverURL +" URL is Wrong");
		}

		return imdbMovies;
	}
	
}
