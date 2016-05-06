package main;

public class SLock {

	private int all = 0;
	private int wait = 0;

	public void setAll(int p_all) {
		all = p_all;
	}

	public synchronized void lock() {
		wait++;
		if(wait >= all){
			notifyAll();
		}else{
			try{
				wait();
			}catch (Exception e){

			}
		}
	}
}
