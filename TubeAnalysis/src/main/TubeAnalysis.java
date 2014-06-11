package main;

import java.awt.PrintGraphics;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import utils.FileReader;
import utils.Journey;
import utils.Key;
import utils.Station;




public class TubeAnalysis {

	public static final int EDGE_CAPACITY = 959;
	
	public static void main(String[] args) {
		printGreeting();
		try {
			System.out.println("---------------------------");
			System.out.println("       Loading data        ");
			System.out.println("---------------------------");
			Graph graph = FileReader.parseGraph("/Users/alexpardo/Dropbox/Uni/master/2nd Sem/CN/CN-Project/tube_map.graphml", false);
//			int e = 0;
//			for edge
//			System.out.println();
			HashMap<Key, String []> paths = FileReader.parseShortestPaths("/Users/alexpardo/Dropbox/Uni/master/2nd Sem/CN/CN-Project/paths.csv", graph);
			System.out.println("--> " + paths.size() + " paths loaded");
			ArrayList<Journey> journeys = FileReader.parseJourneys("/Users/alexpardo/Dropbox/Uni/master/2nd Sem/CN/CN-Project/tube_journeys.csv", paths, graph);
			System.out.println("--> " + journeys.size() + " journeys loaded");
			System.out.println("---------------------------");
			System.out.println("     Beginning execution    ");
			System.out.println("---------------------------");
			
			basicTest(graph, paths, journeys, 1000);

		} catch (IOException e) {
			System.out.println("Graph cannot be loaded. Quiting the app.");
			e.printStackTrace();
		}
		
		
		

	}
	
	
	public static void basicTest(Graph graph, HashMap<Key, String []> paths, ArrayList<Journey> journeys, int t_max){
		Collections.sort(journeys, new Comparator<Journey>(){
		     public int compare(Journey o1, Journey o2){
		         if(o1.init_time == o2.init_time)
		             return 0;
		         return o1.init_time < o2.init_time ? -1 : 1;
		     }
		});
		HashMap<String, Station> stations = new HashMap<>();
		HashMap<Edge, Integer> edge_capacity = new HashMap<>();
		Station s;
		for(Edge e : graph.getEdges()){
			edge_capacity.put(e, TubeAnalysis.EDGE_CAPACITY);
		}
		for(Vertex v : graph.getVertices()){
			s = new Station();
			stations.put((String) v.getId(), s);
		}
		
		
		int add_idx = 0;
		Journey j;
		ArrayList<Integer> items_to_remove = new ArrayList<>();
		ArrayList<Journey> moving_travellers = new ArrayList<>();
		
		Journey mt;
		
		for(int t = 0; t < t_max; t++){
			
			
			// move the previous journeys
			for(int k = 0; k < moving_travellers.size(); k++){
				mt = moving_travellers.get(k);
				Edge prev = mt.getCurrentEdge();
				Edge result = mt.move();
				
				if(result == null){
					items_to_remove.add(k);
				}else if(result.getId() != prev.getId()){ // the traveller has moved
					int tmp = edge_capacity.get(prev);
					edge_capacity.remove(prev);
					edge_capacity.put(prev, tmp+1);
					
					tmp = edge_capacity.get(result);
					if(tmp > 0){
						edge_capacity.remove(result);
						edge_capacity.put(result, tmp-1);
					}
				}
			}
			for(int k = items_to_remove.size()-1; k >= 0; k--){
				moving_travellers.remove(moving_travellers.get(items_to_remove.get(k)));
			}
			items_to_remove.clear();
			
			// add new ones
			while(journeys.get(add_idx).init_time <= t){
				// get the journey
				j = journeys.get(add_idx);
				// put the journey in the edge (if it is available) or in the station if it is not
				Edge e = j.getCurrentEdge();
				if(edge_capacity.get(e) > 0){
					int tmp = edge_capacity.get(e);
					edge_capacity.remove(e);
					edge_capacity.put(e, tmp-1);
					j.setOnEdge();
					moving_travellers.add(j);
				}else{
					stations.get(j.getCurrentPos()).add(j);
				}
				
				add_idx ++;
			}
		}
		int total = 0;
		int count = 0;
		for(Journey k : journeys){
			if(k.total_time > 0){
				total += k.total_time;
				count++;
			}
		}
		System.out.println("MEAN WAITING TIME = " + total/(float)count);
		System.out.println("Number of journeys = " + count);
		
//		File file = new File("example.txt");
//        try {
//			BufferedWriter output = new BufferedWriter(new FileWriter(file));
//			for(Journey k : journeys){
//				output.write(Integer.toString(k.total_time)+"\n");
//			}
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
		
	}
	
	public static void printGreeting(){
		System.out.println("###########################");
		System.out.println("       Tube Analysis       ");
		System.out.println("###########################");
		System.out.println("author: Alex Pardo");
		
	}
	

}
