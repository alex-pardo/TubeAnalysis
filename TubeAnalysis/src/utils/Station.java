package utils;

import java.nio.Buffer;
import java.util.ArrayList;

import main.TubeAnalysis;

public class Station {
	ArrayList<Journey> queue = new ArrayList<>();
	int max_capacity = 0;
	
	public void processTurn(){
		
	}

	public void setMaxCapacity(int count) {
		max_capacity = count*TubeAnalysis.EDGE_CAPACITY;
		
	}

	public void add(Journey j) {
		queue.add(j);
	}

	public ArrayList<Journey> getJourneys() {
		return queue;
	}
	
	public void removeJourney(Journey j){
		queue.remove(j);
	}

}
