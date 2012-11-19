package classes;

public class Emotion {
	
	/*
	 *Data structure:
	  Z-axis,Y-axis,X-axis,Battery,∞Celsius,EDA(uS)
	 * */
	
	private int zAxis,yAxis,xAxis, battery, celsius, EDA;
	
	public Emotion(int zAxis, int yAxis, int xAxis, int battery, int celsius, int EDA){
		this.zAxis = zAxis;
		this.yAxis = yAxis;
		this.xAxis = xAxis;
		this.battery = battery;
		this.celsius = celsius;
		this.EDA = EDA;
	}

	public int getzAxis() {
		return zAxis;
	}

	public int getyAxis() {
		return yAxis;
	}

	public int getxAxis() {
		return xAxis;
	}

	public int getBattery() {
		return battery;
	}

	public int getCelsius() {
		return celsius;
	}

	public int getEDA() {
		return EDA;
	}
	
	

}
