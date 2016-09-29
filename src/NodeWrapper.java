import java.util.HashMap;
import java.util.Map;

/*
 * Used to track and update edges as unwanted nodes are "removed"
 * Contains information that isn't supposed to be printed
 */
public class NodeWrapper {
	public ProcessNode node;
	public Map<Integer, NodeWrapper> lastNodes = new HashMap<Integer, NodeWrapper>();
	public Map<Integer, NodeWrapper> nextNodes = new HashMap<Integer, NodeWrapper>();
	
	public NodeWrapper(ProcessNode n){
		node = n;
	}
	
	//"Removes" this node from the chain, making adjacent nodes point to each other instead
	public void remove(){
		try{
			//Make adjacent nodes point to each other
			for(Integer next: nextNodes.keySet()){ //for each next node
				for(Integer last: lastNodes.keySet()){ //and for each last node
					//Make the next node point back to the last node
					nextNodes.get(next).lastNodes.put(last, lastNodes.get(last));
					//And make the last node point forward to the next node
					lastNodes.get(last).nextNodes.put(next, nextNodes.get(next));
				}
			}
			//remove references to this node
			for(Integer next : nextNodes.keySet()){
				nextNodes.get(next).lastNodes.remove(node.id);
			}
			for(Integer last : lastNodes.keySet()){
				lastNodes.get(last).nextNodes.remove(node.id);
			}
		}catch (NullPointerException e) {
			System.out.print("No next nodes");
		}
	}
}
