package utils;

public class Key {
	String source;
	String destination;
	
	public Key(String arg1, String arg2){
		source = arg1;
		destination = arg2;
	}

	public String getSrc(){
		return source;
	}
	
	public String getDest(){
		return destination;
	}
	@Override
	public boolean equals(Object obj) {
		return source.equals(((Key) obj).getSrc()) && destination.equals(((Key) obj).getDest());
		
	}

	@Override
	public String toString() {
		String out = source + ", " + destination;
		return out;
	}

	@Override
	public int hashCode() {
		return source.hashCode() + destination.hashCode();
	}
	
	
	
	

}
