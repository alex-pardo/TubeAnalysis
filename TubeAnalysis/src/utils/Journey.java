package utils;

import main.TubeAnalysis;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;

public class Journey {

	String source;
	String dest;
	String [] path;
	float expected_time;
	int [] turns;
	String current_pos;
	int current_idx;
	int turns_to_move;
	int total_distance;
	public int init_time;
	Edge[] edge_path;
	boolean waiting = true;
	public int total_time = 0; 
	
	
	public Journey(String src, String dst, String [] path, Graph g, int init, Edge[] edge_path){
		this.edge_path = edge_path;
		source = src;
		dest = dst;
		this.path = java.util.Arrays.copyOf(path, path.length);
		init_time = init;
		expected_time = 0;
		turns = new int[path.length];
		for(int i = 0; i < path.length-1; i++){
			
			String target = path[i+1];
			int distance = 0;
			for(Edge e : g.getVertex(path[i]).getEdges(Direction.BOTH)){
				if(((String)e.getVertex(Direction.OUT).getId()).equals(path[i]) && ((String)e.getVertex(Direction.IN).getId()).equals(target)
						|| ((String)e.getVertex(Direction.IN).getId()).equals(path[i]) && ((String)e.getVertex(Direction.OUT).getId()).equals(target)){
					distance = e.getProperty("weight");
					turns[i] = (int) Math.ceil(distance/(float)TubeAnalysis.M_PER_TURN);
					expected_time += turns[i];
					total_distance += distance;
					break;
				}
			}
			//expected_time += (int) Math.ceil(distance/(float)M_PER_TURN);
		}
		
		current_idx = 0;
		current_pos = path[current_idx];
	}

	public String getCurrentPos() {
		return path[current_idx];
	}

	public String getNextPos() {
		return path[current_idx+1];
	}

	public Edge getCurrentEdge() {
		return edge_path[current_idx];
	}

	public void setOnEdge() {
		waiting = false;
		current_idx = 0;
		turns_to_move = turns[current_idx];
	}

	public Edge move() {
		total_time++;
		if(!waiting){
			if(turns_to_move <= 1){
				current_idx += 1;
				if(current_idx >= edge_path.length){
					return null;
				}
				turns_to_move = turns[current_idx];
			} else{
				turns_to_move --;
			}
		}
		return edge_path[current_idx];
		
	}

	public float getExtraWaitingTime(){
		return total_time-expected_time;
	}
	
	@Override
	public String toString() {
		String out = "";
		out += Integer.toString(current_idx);
		out += "; "+Integer.toString(edge_path.length);
		return out;
	}

	public void increaseTurn() {
		total_time++;
	}

	
	
	
}
