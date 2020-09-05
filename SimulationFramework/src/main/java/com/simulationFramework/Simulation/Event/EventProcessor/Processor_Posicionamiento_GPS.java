package com.simulationFramework.Simulation.Event.EventProcessor;

import com.simulationFramework.Simulation.Event.Event;
import com.simulationFramework.SystemState.TargetSystem;

public class Processor_Posicionamiento_GPS implements IEventProcessor {

	@Override
	public void processEvent(Event event, TargetSystem TargetSystem) {

		long busID = Long.parseLong(event.getContext().get("busID"));
		long lineID = Long.parseLong(event.getContext().get("lineID"));
		double longitude = Double.parseDouble(event.getContext().get("longitude"));
		double latitude = Double.parseDouble(event.getContext().get("latitude"));
		
		if (longitude != -1 && latitude != -1) {
			
			/*
			System.out.println("Processor_Posicionamiento_GPS ===> "+
					"BusID="+event.getContext().get("busID")+" "+
					"GPS_X="+event.getContext().get("longitude")+" "+
					"GPS_Y="+event.getContext().get("latitude"));
			*/
			
			TargetSystem.moveBus(busID, lineID, longitude / 10000000, latitude / 10000000);
		}

	}

}