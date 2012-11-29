package classes;

public class QSensorBTDevice {
	
	String name;
	String address;
	String uuid;
	
	public QSensorBTDevice(String name, String address, String uuid){
		this.name = name;
		this.address = address;
		this.uuid = uuid;
	}
	
	public QSensorBTDevice(String name, String address){
		this.name = name;
		this.address = address;
	}
	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public String getUuid() {
		return uuid;
	}
	
	

}
