package classes;

public class Movie {

	private String movieName, gender, age;
	private double averageEda;
	
	public Movie(String movieName, String gender, String age, double averageEda) {
		this.movieName = movieName;
		this.gender = gender;
		this.age = age;
		this.averageEda = averageEda;
	}
	
	public Movie(){
		
	}

	public String getMovieName() {
		return movieName;
	}

	public String getGender() {
		return gender;
	}

	public String getAge() {
		return age;
	}

	public double getAverageEda() {
		return averageEda;
	}
	
	
	
	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public void setAverageEda(double averageEda) {
		this.averageEda = averageEda;
	}

	public String toString(){
		String result;
		
		result = "MovieName: " + movieName + "\n" + "Gender: " + gender + "\n" + "Age: " + age + "\n" + "EDA: " + averageEda;
		
		return result;
	}
	
	
}
