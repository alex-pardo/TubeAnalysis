package main;

import gui.TubeAnalysisGUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import utils.FileReader;
import utils.Journey;
import utils.Key;
import utils.Station;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
//import org.jfree.chart.ChartFactory;




public class TubeAnalysis {




	//	public static final int EDGE_CAPACITY = 959;
	//	public static final int M_PER_TURN = 833;

	public static int EDGE_CAPACITY;
	public static int M_PER_TURN;

	private JFrame tubeFrame;
	private static JTextArea text_out;
	private JPanel plot;

	private int mode = 0;
	
	private DefaultCategoryDataset dataset = new DefaultCategoryDataset(); 
	

	private String mode_name;

	public TubeAnalysis() {
		//		EDGE_CAPACITY = edge;
		//		M_PER_TURN = m;
		tubeFrame = new JFrame();
		text_out = new JTextArea();
		plot = new JPanel();

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(text_out);

		DefaultCaret caret = (DefaultCaret)text_out.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		JTabbedPane jtp = new JTabbedPane();

		jtp.addTab("Output", scrollPane);
		jtp.addTab("Plot", plot);
		tubeFrame.add(jtp);

		tubeFrame.setSize(800, 800);
		tubeFrame.setVisible(true);
	}



	private static void printText(String s){
		String text = text_out.getText();
		text += "\n"+s;
		text_out.setText(text);
	}


	public void setValues(int edge, int m) {
		EDGE_CAPACITY = edge;
		M_PER_TURN = m;
	}


	public void run(){

		try {

			String [] files = getFiles(mode);

			printText(mode_name);
			printText("--> Loading data:");

			Graph graph = FileReader.parseGraph(files[0], false);

			HashMap<Key, String []> paths = FileReader.parseShortestPaths(files[1], graph);
			printText("      " + paths.size() + " paths loaded");
			ArrayList<Journey> journeys = FileReader.parseJourneys(files[2], paths, graph);
			printText("      " + journeys.size() + " journeys loaded");

			printText("--> Beginning execution:");


			basicTest(graph, paths, journeys, 1000);

		} catch (IOException e) {
			printText("Graph cannot be loaded. Quiting the app.");
			e.printStackTrace();
		}
	}

	private String[] getFiles(int mode2) {
		switch (mode) {
		case 0:
			mode_name = "real_graph";
			String [] f1 = {"../data/tube_map.graphml", "../data/tube_map_paths.csv", "../data/tube_journeys.csv"};
			return f1;
		case 1:
			mode_name = "1node_betweenness";
			String [] f2 = {"../data/tube_map_btw1.graphml", "../data/tube_map_btw1_paths.csv", "../data/tube_journeys_btw1.csv"};
			return f2;
		case 2:
			mode_name = "2node_betweenness";
			String [] f3 = {"../data/tube_map_btw2.graphml", "../data/tube_map_btw2_paths.csv", "../data/tube_journeys_btw2.csv"};
			return f3;
		case 3:
			mode_name = "1node_nieghbour";
			String [] f4 = {"../data/tube_map_neigh1.graphml", "../data/tube_map_neigh1_paths.csv", "../data/tube_journeys_neigh1.csv"};
			return f4;
		case 4:
			mode_name = "node_in_traffic";
			String [] f5 = {"../data/tube_map_intraffic.graphml", "../data/tube_map_intraffic_paths.csv", "../data/tube_journeys_intraffic.csv"};
			return f5;
		case 5:
			mode_name = "node_out_traffic";
			String [] f6 = {"../data/tube_map_outtraffic.graphml", "../data/tube_map_outtraffic_paths.csv", "../data/tube_journeys_outtraffic.csv"};
			return f6;
		default:
			break;
		}
		return null;
	}



	public void basicTest(Graph graph, HashMap<Key, String []> paths, ArrayList<Journey> journeys, int t_max){
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
		int t = 0;
		while(add_idx < journeys.size() || moving_travellers.size() > 0){
			//for(int t = 0; t < t_max; t++){


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

			// add the travellers in the stations
			for(Station stat : stations.values()){
				ArrayList<Journey> jor_delete = new ArrayList<>();
				for(Journey jor : stat.getJourneys()){
					Edge e = jor.getCurrentEdge();
					if(edge_capacity.get(e) > 0){
						int tmp = edge_capacity.get(e);
						edge_capacity.remove(e);
						edge_capacity.put(e, tmp-1);
						jor.setOnEdge();
						moving_travellers.add(jor);	
						jor_delete.add(jor);
					} else{
						jor.increaseTurn();
					}
				}
				for(Journey jor : jor_delete) stat.removeJourney(jor);
			}

			if(add_idx < journeys.size()){
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
					if(add_idx >= journeys.size()) break;
				}
			}
			t++;

		}
		int total = 0;
		int count = 0;
		float extra = 0;
		double [] x = new double[journeys.size()];
		double [] y = new double[journeys.size()];
		double max_time = 0;
		for(Journey k : journeys){
			if(k.total_time > 0){
				total += k.total_time;
				extra += k.getExtraWaitingTime();
				x[count] = count;
				y[count] = k.getExtraWaitingTime();
				if(max_time < k.getExtraWaitingTime()) max_time = k.getExtraWaitingTime();
				count++;
			}
		}

		printText("      MEAN TRAVEL TIME = " + total/(float)count + " minutes");
		printText("      MEAN EXTRA WAITING TIME = " + extra/(float)count + " minutes");
		printText("      MAX TIMEe = " + max_time);
		printText("      Number of journeys tested = " + count);
		printText("      Number of journeys in the database = " + count);


		TubeAnalysisGUI.setResults(Float.toString(total/(float)count), Float.toString(extra/(float)count), Integer.toString(count));

		
		
		
		//plot.addLinePlot(mode_name, tmpx1, tmpy);
		//plot.addLinePlot(mode_name, tmpx2, tmpy);
		//plot.paintImmediately(new Rectangle((int)tmpx[0], 0, 1, (int)tmpy[0]));
		//plot.addBarPlot(, tmpx, tmpy);
		
		dataset.addValue(extra/(float)count, "Mean extra time", mode_name);
		dataset.addValue(total/(float)count, "Mean time", mode_name);
		
		//JFreeChart chart = ChartFactory.createBarChart("", "", "", dataset);
		
		
		JFreeChart chart = ChartFactory.createBarChart(
	            "Travel Time",         // chart title
	            "Graph",               // domain axis label
	            "Minutes",                  // range axis label
	            dataset,                  // data
	            PlotOrientation.VERTICAL, // orientation
	            true,                     // include legend
	            true,                     // tooltips?
	            false                     // URLs?
	        );


		CategoryPlot p = chart.getCategoryPlot();
		CategoryAxis domainAxis = p.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
            CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
        );
        
        
		
		ChartPanel cp = new ChartPanel(chart);
		
		cp.setPreferredSize(new Dimension(plot.getWidth(), plot.getHeight()));
		plot.removeAll();
		plot.add(cp);
		
		BorderLayout bl = new BorderLayout();
		
		plot.setLayout(bl);
		Dimension panelSize = plot.getSize();
		plot.add(cp,BorderLayout.CENTER);
		cp.setSize(panelSize);
		
		printText("_______________________________");

	}



	public static void printGreeting(){
		printText("###########################");
		printText("       Tube Analysis       ");
		printText("###########################");
		printText("author: Alex Pardo");

	}

	public Histogram createHistogram(double[] data_x, double[] data_y, int bars, double max_y){

		double[] x = linspace(1, max_y, bars);
		double[] y = new double[x.length];
		java.util.Arrays.sort(data_x);
		int j = 0;
		int last_j = 0;
		for(int i = 0; i < x.length; i++){
			while(j < data_y.length && data_y[j] < x[i]) j++;
			y[i] = (j - last_j);
			last_j = j;
		}
		Histogram hist = new Histogram();
		hist.x = x;
		hist.y = y;
		return hist;
	}

	class Histogram{
		public double [] x;
		public double [] y;

	}

	public static double [] linspace(double start, double stop, int n)
	{

		double [] result = new double[n];

		double step = (stop-start)/(n-1);

		for(int i = 0; i <= n-2; i++)
		{
			result[i] = start + (i * step);
		}
		result[n-1] = stop;

		return result;
	}



	public void setMode(String selected) {
		mode = Integer.parseInt(selected);
	}




}
