package com.simulationFramework.RestService.implementation;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.simulationFramework.RestService.interfaces.ISystemStateRest;
import com.simulationFramework.Simulation.SimController;
import com.simulationFramework.SystemState.SITMFactory.SITMBus;
import com.simulationFramework.SystemState.SITMFactory.SITMStop;

@RequestMapping("simulation")
@RestController
@CrossOrigin(origins = "*")
public class SystemStateRest implements ISystemStateRest{

	@Autowired
	private SimController SimController;
	
	@GetMapping("/buses")
	public ArrayList<SITMBus> findAllBuses() {
		return SimController.getBusesByLine();
	}
	
	@GetMapping("/stops")
	public ArrayList<SITMStop> findAllStops() {
		return SimController.getStopsByLine();
	}
	
	@GetMapping("/test")
	public String findLineId() {
		return SimController.getLineID()+"";
	}
}
