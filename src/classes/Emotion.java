package classes;

public class Emotion {
	
	/*
	 *Data structure:
	  Z-axis,Y-axis,X-axis,Battery,∞Celsius,EDA(uS)
	 * */
	
	private double zAxis,yAxis,xAxis, battery, celsius, EDA;
	
	public Emotion(double zAxis, double yAxis, double xAxis, double battery, double celsius, double EDA){
		this.zAxis = zAxis;
		this.yAxis = yAxis;
		this.xAxis = xAxis;
		this.battery = battery;
		this.celsius = celsius;
		this.EDA = EDA;
	}

	public double getzAxis() {
		return zAxis;
	}

	public double getyAxis() {
		return yAxis;
	}

	public double getxAxis() {
		return xAxis;
	}

	public double getBattery() {
		return battery;
	}

	public double getCelsius() {
		return celsius;
	}

	public double getEDA() {
		return EDA;
	}
	
	

}
