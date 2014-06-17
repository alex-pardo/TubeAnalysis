package utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;


public class FileReader {

	

	public static Graph parseGraph(String filename, boolean debug) throws IOException{

		Graph graph = new TinkerGraph();

		GraphMLReader.inputGraph(graph, filename);
		// System.out.println("--> Graph loaded: " + graph);
		
		
		if(debug){

			System.out.println("Edges of " + graph);
			for (Edge edge : graph.getEdges()) {
				System.out.println("------");
				System.out.println(edge.getVertex(Direction.IN) +"->"+edge.getVertex(Direction.OUT));
				for(String key: edge.getPropertyKeys()){
					System.out.println(key+":" + edge.getProperty(key));
				}

			}

			for (Vertex vertex : graph.getVertices()){
				System.out.println("------");
				System.out.println(vertex);
				for(String key : vertex.getPropertyKeys()){
					System.out.println(key+":" + vertex.getProperty(key));
				}
			}
		}
		return graph;

	}

	public static HashMap<Key, String[]> parseShortestPaths(String filename, Graph g) throws IOException{

		HashMap<Key, String[]> paths = new HashMap<>();
		File f = new File(filename);
		FileInputStream fs = new FileInputStream(f);
		BufferedReader br = new BufferedReader( new InputStreamReader( fs ) );
		String line = null;
		boolean first = true;
		while( (line = br.readLine() ) != null ) {
			String[] values = line.split(",");
			if(values.length < 4) continue; // at least one node between origin and destination: <source, destination, path(source,destination)> 
			Key k = new Key((String) g.getVertex(values[0]).getId(), (String) g.getVertex(values[1]).getId());
			String [] path = new String[values.length-2];
			for(int i = 2; i < values.length; i++){
				path[i-2] = (String) g.getVertex(values[i]).getId();
			}
			paths.put(k, path);
			

			
		}
		

		return paths;

	}


	public static ArrayList<Journey> parseJourneys(String filename, HashMap<Key, String[]> paths, Graph g) throws IOException{
		ArrayList<Journey> journeys =  new ArrayList<>();
		File f = new File(filename);
		
		HashMap<String, Integer> stations = parseStations("../stations.csv");//parseStations(filename.replace("tube_journeys", "stations"));
		
		FileInputStream fs = new FileInputStream(f);
		BufferedReader br = new BufferedReader( new InputStreamReader( fs ) );
		String line = null;
		boolean first = true;
		Journey j;
		String [] path;
		while( (line = br.readLine() ) != null ) {
			String[] values = line.split(",");
			String src = ((Integer)stations.get(values[2])).toString(); 
			String dest = ((Integer)stations.get(values[3])).toString();	
			String init = values[4];
			if(src.equals(dest)) continue;
			path = paths.get(new Key(src, dest));
			
			Edge[] edge_path;
			try {	
				edge_path = getEdgePath(path, g);
				j = new Journey(src, dest, path, g, Integer.parseInt(init), edge_path);
				journeys.add(j);
			} catch (NullPointerException e) { // caused because of the deletion of a node
				continue;
			}
			
		}
		return journeys;
	}
	
	public static HashMap<String, Integer> parseStations(String filename) throws IOException{
		HashMap<String, Integer> stations = new HashMap<String, Integer>();
		ArrayList<Journey> journeys =  new ArrayList<>();
		File f = new File(filename);
		FileInputStream fs = new FileInputStream(f);
		BufferedReader br = new BufferedReader( new InputStreamReader( fs ) );
		String line = null;
		String name;
		Integer index;
		boolean first = true;
		while( (line = br.readLine() ) != null ) {
			if(first){ first = false; continue;}
			String[] values = line.split(",");
			if(values.length < 4) continue;
			name = values[3].substring(1, values[3].length()-1);  // remove quotes
			index = Integer.parseInt(values[0]);
			stations.put(name, index);
		}
		
		return stations;
	}
	
	private static Edge[] getEdgePath(String[] path, Graph g){
		
		Edge[] edge_list = new Edge[path.length-1];
		for(int i = 0; i < path.length-1; i++){
			for(Edge e : g.getVertex(path[i]).getEdges(Direction.BOTH)){
				if((e.getVertex(Direction.IN).getId().equals(path[i]) && 
						e.getVertex(Direction.OUT).getId().equals(path[i+1])) ||
						(e.getVertex(Direction.OUT).getId().equals(path[i]) && 
								e.getVertex(Direction.IN).getId().equals(path[i+1]))){
					edge_list[i] = e;
				}
			}
		}
		
		return edge_list;
	}
}

