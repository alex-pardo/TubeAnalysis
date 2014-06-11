package utils;

import java.util.ArrayList;

import main.TubeAnalysis;

public class CustomEdge {
	int max_capacity = TubeAnalysis.EDGE_CAPACITY;
	int current_travellers = 0;
	int label;
	ArrayList<Journey> travellers = new ArrayList<>();
	CustomEdge next;
	CustomEdge previous;

	public CustomEdge(int label){
		this.label = label;
	}
	
	
	
}
