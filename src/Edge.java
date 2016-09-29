//Edge class, contains edge information for output
public class Edge {
	public Integer from;
	public Integer to;
	
	public Edge(Integer prev, Integer next){
		from = prev;
		to = next;
	}
	
	public void print(){ //for debugging
		System.out.println(from + " " + to );
	}

}
