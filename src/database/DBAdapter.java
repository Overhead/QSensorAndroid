package database;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import classes.Movie;

public class DBAdapter {

	private static final String DATABASE_NAME = "QSensorDB.db";
	private static final String DATABASE_TABLE = "Movies";
	private static final int DATABASE_VERSION = 3;
	
	// The index (key) column name for use in where clauses.
	public static final String MOVIE_ID = "_id";
	public static final int MOVIE_ID_NO = 0;
	// The name and column index of each column in your database.
	public static final String MOVIENAME = "movieName";
	public static final int MOVIENAME_NUM = 1;
	
	public static final String AGE = "age";
	public static final int AGE_NUM = 2;
	
	public static final String GENDER = "gender";
	public static final int GENDER_NUM = 3;
	
	public static final String EMOTIONVALUE = "emotionValue";
	public static final int EMOTIONVALUE_NUM = 4;
	
	// SQL Statement to create a new database.
	private static final String DATABASE_CREATE = "create table "
			+ DATABASE_TABLE + " (" + MOVIE_ID + " integer primary key autoincrement, " 
			+ MOVIENAME + " text not null,"
			+ AGE + " text not null," 
			+ GENDER + " text not null,"
			+ EMOTIONVALUE + " text not null);";
	
	// Variable to hold the database instance
	private SQLiteDatabase db;
	// Context of the application using the database.
	private final Context context;
	// Database open/upgrade helper
	private myDbHelper dbHelper;

	public DBAdapter(Context _context) {
		context = _context;
		dbHelper = new myDbHelper(context, DATABASE_NAME, null,
				DATABASE_VERSION);
	}

	public DBAdapter open() throws SQLException {
		try {
	          db = dbHelper.getWritableDatabase();
	      } catch (SQLiteException ex) {
	          db = dbHelper.getReadableDatabase();
	      }
		return this;
	}

	public void close() {
		db.close();
	}

	public long insertEntry(Movie newMovie) {

		ContentValues newValues = new ContentValues();

		newValues.put(MOVIENAME, newMovie.getMovieName());
		newValues.put(AGE, newMovie.getAge());
		newValues.put(GENDER, newMovie.getGender());
		newValues.put(EMOTIONVALUE, newMovie.getAverageEda());

		return db.insert(DATABASE_TABLE, null, newValues);
	}
	
	public Movie getEntry(long _rowIndex) {
		Movie objectInstance=null;

		Cursor myCursor = db.query(true, DATABASE_TABLE, new String[] { MOVIE_ID, MOVIENAME, GENDER, AGE, EMOTIONVALUE }, MOVIE_ID + '=' + _rowIndex, null , null, null, null, null);
		if (myCursor.getCount() == 0 || !myCursor.moveToFirst()) {
	          throw new SQLException("Did not find any movie with index: " + _rowIndex);
	      }

		String movieName = myCursor.getString(myCursor.getColumnIndex(MOVIENAME));
		String gender = myCursor.getString(myCursor.getColumnIndex(GENDER));
		int age = Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(AGE)));
		int emotionValue = Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(EMOTIONVALUE)));

		objectInstance = new Movie(movieName, gender, age, emotionValue);
		return objectInstance;
	}
	
	public List<Movie> getAllMovies(){
		List<Movie> movieList = new ArrayList<Movie>();
		
		Cursor myCursor = getAllEntries();
		String movieName, gender;
		int age, emotionValue;
		
		if(myCursor.moveToFirst())
			do{
				movieName = myCursor.getString(myCursor.getColumnIndex(MOVIENAME));
				gender = myCursor.getString(myCursor.getColumnIndex(GENDER));
				age = Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(AGE)));
				emotionValue = Integer.parseInt(myCursor.getString(myCursor.getColumnIndex(EMOTIONVALUE)));

				Movie newMovie = new Movie(movieName, gender, age, emotionValue);
				movieList.add(newMovie);
			}while(myCursor.moveToNext());
		
		return movieList;
		
	}
	
	public boolean removeEntry(long _rowIndex) {
		return db.delete(DATABASE_TABLE, MOVIE_ID + "=" + _rowIndex, null) > 0;
	}

	public Cursor getAllEntries() {
		return db.query(DATABASE_TABLE, new String[] { MOVIE_ID, MOVIENAME, AGE, GENDER, EMOTIONVALUE},
				null, null, null, null, null);
	}

	 public boolean removeAllEntries() {
	      return db.delete(DATABASE_TABLE, null, null) > 0;
	  }
	
	
	private static class myDbHelper extends SQLiteOpenHelper {
		public myDbHelper(Context context, String name, CursorFactory factory,
				int version) {
			super(context, name, factory, version);
		}

		// Called when no database exists in disk and the helper class needs
		// to create a new one.
		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(DATABASE_CREATE);
		}

		// Called when there is a database version mismatch meaning that the
		// version
		// of the database on disk needs to be upgraded to the current version.
		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion,
				int _newVersion) {
			// Log the version upgrade.
			Log.w("TaskDBAdapter", "Upgrading from version " + _oldVersion
					+ " to " + _newVersion
					+ ", which will destroy all old data");
			// Upgrade the existing database to conform to the new version.
			// Multiple
			// previous versions can be handled by comparing _oldVersion and
			// _newVersion
			// values.
			// The simplest case is to drop the old table and create a new one.
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			// Create a new one.
			onCreate(_db);
		}
	}
}
