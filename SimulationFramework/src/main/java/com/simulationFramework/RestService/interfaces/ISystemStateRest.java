package com.simulationFramework.RestService.interfaces;

import java.util.ArrayList;

import com.simulationFramework.SystemState.SITMFactory.SITMBus;
import com.simulationFramework.SystemState.SITMFactory.SITMStop;

public interface ISystemStateRest {

	public ArrayList<SITMBus> findAllBuses();
	public ArrayList<SITMStop> findAllStops();
	public String findLineId();
}
