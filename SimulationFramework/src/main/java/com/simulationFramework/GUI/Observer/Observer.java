package com.simulationFramework.GUI.Observer;

import java.util.ArrayList;

import com.simulationFramework.Simulation.SimState.Variable;

public interface Observer {

	public void updateVariables(ArrayList<Variable> variables);

}
