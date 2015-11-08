package tup.main;

public class TUPKey {
	
	private int game1;
	private int game2;
	
	public TUPKey(int game1, int game2){
		this.game1 = game1;
		this.game2 = game2;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + game1;
		result = prime * result + game2;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TUPKey other = (TUPKey) obj;
		if (game1 != other.getGame1())
			return false;
		if (game2 != other.getGame2())
			return false;
		return true;
	}
	
	public int getGame1() {
		return game1;
	}

	public int getGame2() {
		return game2;
	}
	
	@Override
	public String toString(){
		return "["+game1+","+game2+"]";
	}
	

}
