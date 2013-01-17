package helpers;

import java.util.EventListener;

public interface QSensorListener extends EventListener{

	void eventAppeared(QSensorEvent event);

}
