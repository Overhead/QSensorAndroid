package classes;

public class Movie {

	private String movieName, gender;
	private int age, averageEda;
	
	public Movie(String movieName, String gender, int age, int averageEda) {
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

	public int getAge() {
		return age;
	}

	public int getAverageEda() {
		return averageEda;
	}
	
	public String toString(){
		String result;
		
		result = "MovieName: " + movieName + "\n" + "Gender: " + gender + "\n" + "Age: " + age + "\n" + "EDA: " + averageEda;
		
		return result;
	}
	
	
}
