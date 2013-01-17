package classes;

public class Emotion {
	
	/**
	 * Data structure:
	 * Z-axis,Y-axis,X-axis,Battery,∞Celsius,EDA(uS)
	 */
	
	private double zAxis,yAxis,xAxis, battery, celsius, EDA, time;
	
	public Emotion(double zAxis, double yAxis, double xAxis, double battery, double celsius, double EDA, double time){
		this.zAxis = zAxis;
		this.yAxis = yAxis;
		this.xAxis = xAxis;
		this.battery = battery;
		this.celsius = celsius;
		this.EDA = EDA;
		this.time=time;
	}

	/** Get the time when this emotion was received on the android phone.
	 * Since the bluetooth doesn't send a timestamp from the qsensor has the time to be set on receiving time
	 * 
	 * @return the time of this emotion
	 */
	public double getReceivedTime(){
		return time;
	}
	
	/**
	 * 
	 * @return the zAxis of this emotion element
	 */
	public double getzAxis() {
		return zAxis;
	}

	/**
	 * 
	 * @return the yAxis of this emotion element
	 */
	public double getyAxis() {
		return yAxis;
	}

	/**
	 * 
	 * @return the xAxis of this emotion element
	 */
	public double getxAxis() {
		return xAxis;
	}

	/**
	 * 
	 * @return the battery level as percent of this emotion element
	 */
	public double getBattery() {
		return battery;
	}

	/**
	 * 
	 * @return the temperature from this emotion element in celsius
	 */
	public double getCelsius() {
		return celsius;
	}

	/**
	 * 
	 * @return the EDA value of this emotion element
	 */
	public double getEDA() {
		return EDA;
	}

}
