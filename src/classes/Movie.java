package classes;

public class Movie {

	private String movieName, gender, age, imdbId;
	private double averageEda;
	private int productionYear;
	
	public Movie(String imdbId, String movieName, String gender, String age, double averageEda, int productionYear) {
		this.imdbId = imdbId;
		this.movieName = movieName;
		this.gender = gender;
		this.age = age;
		this.averageEda = averageEda;
		this.productionYear = productionYear;
	}
	
	public Movie(){
		
	}

	
	
	public int getProductionYear() {
		return productionYear;
	}

	public void setProductionYear(int productionYear) {
		this.productionYear = productionYear;
	}

	public String getImdbId() {
		return imdbId;
	}

	public void setImdbId(String imdbId) {
		this.imdbId = imdbId;
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
	
	public String imdbToString(){
		String result;
		
		result = "Title: " + movieName + "\n" + "Year: " + productionYear;
		
		return result;
	}
	
	
}
