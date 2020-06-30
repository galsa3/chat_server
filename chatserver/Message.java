package chatserver;

public interface Message <K, D> {
	
	public K getKey();
	public void setKey(K key);
	public D getData();
	public void setData(D data);
}
