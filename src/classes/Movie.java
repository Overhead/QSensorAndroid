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
	
	public String toString(){
		String result;
		
		result = "MovieName: " + movieName + "\n" + "Gender: " + gender + "\n" + "Age: " + age + "\n" + "EDA: " + averageEda;
		
		return result;
	}
	
	
}
