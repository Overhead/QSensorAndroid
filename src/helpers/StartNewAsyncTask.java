package helpers;

import classes.Movie;
import android.os.AsyncTask;
import android.util.Log;

public class StartNewAsyncTask extends AsyncTask<Integer, Void, Void>{

	Movie movie;
	
	public StartNewAsyncTask(Movie movie){
		this.movie = movie;
	}
	
		@Override
		protected Void doInBackground(Integer... params) {

			/** If first params is 1 Send movie to database */
			if(params[0]==1){
				ServerCommunication.SendMovieDataToDB(movie);
			}

			return null;
		}
		
		protected Void onPostExecute(Void... in){
			Log.i("current","onPostExecute");
			return null;
		}
}
