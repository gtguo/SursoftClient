/**
 * 
 */
package cn.sursoft.minicap;

public interface ScreenSubject {
	
	public void registerObserver(AndroidScreenObserver o);

	public void removeObserver(AndroidScreenObserver o);

	public void notifyObservers(byte[] imageData);

}
