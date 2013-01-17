package helpers;

import java.util.EventObject;

import classes.Emotion;

public class QSensorEvent extends EventObject {

	/**
	 * default serial version UID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * the detected emotion
	 */
	private Emotion emotion;

	
	/** Constructor that wants the source
	 * 
	 * @param source the manager where the event was appearing, cannot be null
	 * @param emotion the detected emotion, cannot be null
	 * 
	 * @throws IllegalArgumentException if the source manager or the emotion is null
	 */
	public QSensorEvent(QSensorBluetoothManager source, Emotion emotion) {
		super(source);
		
		//check if the parameters are null
		if (source == null || emotion == null) {
			throw new IllegalArgumentException();
		}
		
		//set the emotion object
		this.emotion=emotion;
	}

	/**
	 * 
	 * @return A copy of the Emotion detected in this event
	 */
	public Emotion getEmotion() {
		// create a copy and give this back
		
		//first get all values
		double z = this.emotion.getzAxis();
		double y = this.emotion.getyAxis();
		double x=this.emotion.getyAxis();
		double time=this.emotion.getReceivedTime();
		double eda=this.emotion.getEDA();
		double battery = this.emotion.getBattery();
		double temperature=this.emotion.getCelsius();
		
		//create a new Emotion object with all this data and return it
		Emotion emotionCopy = new Emotion(z,y,x,battery,temperature,eda,time);
		return emotionCopy;
	}


}
