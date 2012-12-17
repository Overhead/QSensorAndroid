package classes;

/**
 * Class the representes the QSensor
 * @author Tjarb
 *
 */
public class BTDevice {
	
	String name;
	String address;
	String uuid;
	
	public BTDevice(String name, String address, String uuid){
		this.name = name;
		this.address = address;
		this.uuid = uuid;
	}
	
	public BTDevice(String name, String address){
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
