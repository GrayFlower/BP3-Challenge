import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/*
 * Using the GSON package to read/write JSON
 * Assuming Gateways are supposed to be removed, since they aren't Human Tasks
 * Output with System.out.print, since no particular output was specified
 */
public class Main {
	//All-encompassing object used to read/write JSON
	public class Diagram {
		public ProcessNode[] nodes;
		public Edge[] edges;
		
	}
	
	public static void main(String [ ] args) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonReader reader;
		try {
			//read in the Json
			reader = new JsonReader(new FileReader("diagram.json"));
			Diagram diagram = gson.fromJson(reader, Diagram.class);
			
			/*
			 * We want each node to know it's own edges, so that when we remove a node, it's easy to "connect"
			 * its forward and backward edges, creating new "good" edges. But since we don't want to print
			 * node and edge information together, we need a large object that holds both node and edge information,
			 * instead of just putting the edge information in the node object. This object is the "NodeWrapper".
			 * We can use the wrappers to calculate new edges, and then print the lists of nodes and edges separately. 
			 */
			HashMap<Integer, NodeWrapper> node_map = new HashMap<Integer, NodeWrapper>(); //used to calculate edges
			ArrayList<ProcessNode> nodes = new ArrayList<ProcessNode>(); //final node list
			ArrayList<Edge> edges = new ArrayList<Edge>(); //final edge list
			//Add a wrapper for each node
			for(ProcessNode n : diagram.nodes){
				node_map.put(n.id, new NodeWrapper(n));
			}
			//Add edges into the map
			for(Edge e : diagram.edges){
				//stores edge in wrapper by making wrappers point to each other
				node_map.get(e.from).nextNodes.put(e.to, node_map.get(e.to));
				node_map.get(e.to).lastNodes.put(e.from, node_map.get(e.from));
			}
			//Remove bad edges, concatenate to make good ones, keep track of good nodes
			for( ProcessNode n : diagram.nodes){
				//node we want to remove
				if(!n.type.equals("HumanTask") && !n.type.equals("Start") && !n.type.equals("End")){
					//creates new edges between adjacent nodes, removes the edges connecting to this node
					node_map.get(n.id).remove();
				}
				else{
					//if node isn't going to be removed, add it to final list
					nodes.add(n);
				}
			}
			//Get good edges
			for( ProcessNode n : diagram.nodes){
				//if this a good node, and the front of an edge
				if(n.type.equals("HumanTask") || n.type.equals("Start") ){
					//Add an edge for each following adjacent node
					for(Integer w : node_map.get(n.id).nextNodes.keySet()){
						//these edges will be good, since references to bad nodes have been removed
						edges.add(new Edge(n.id, node_map.get(w).node.id));
					}
				}
			}
			//Print results
			diagram.nodes = nodes.toArray(new ProcessNode[nodes.size()]);
			diagram.edges = edges.toArray(new Edge[edges.size()]);
			String output = gson.toJson(diagram);
			System.out.println(output);
	
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
