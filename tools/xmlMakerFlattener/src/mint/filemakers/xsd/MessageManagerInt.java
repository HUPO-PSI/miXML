package mint.filemakers.xsd;

public interface MessageManagerInt {

	public static final int errorMessage = 0;
	public static final int simpleMessage = 1;
	public static final int warningMessage = 2;

	public void sendMessage(String message, int type);
	
}
