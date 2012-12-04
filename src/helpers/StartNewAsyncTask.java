package helpers;

import classes.Movie;
import activities.CommunityActivity;
import android.os.AsyncTask;
import android.util.Log;

public class StartNewAsyncTask extends AsyncTask<Integer, Void, Void>{

	Movie movie;
	String age, gender;
	public StartNewAsyncTask(Movie movie){
		this.movie = movie;
	}
	
	public StartNewAsyncTask(String gender, String age){
		this.gender = gender;
		this.age = age;
	}
	
	public StartNewAsyncTask(){
		
	}
	
		@Override
		protected Void doInBackground(Integer... params) {

			/** If first params is 1 Send movie to database */
			if(params[0]==1){
				ServerCommunication.SendMovieDataToDB(movie);
			}
			
			if(params[0]==2){
				CommunityActivity.communityMoviesList =  ServerCommunication.getCommunityMovies(gender, age);
			}
			return null;
		}
		
		protected Void onPostExecute(Void... in){
			Log.i("current","onPostExecute");
			return null;
		}
}
