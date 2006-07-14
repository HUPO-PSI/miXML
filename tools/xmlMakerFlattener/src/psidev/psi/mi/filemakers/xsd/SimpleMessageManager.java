package psidev.psi.mi.filemakers.xsd;


public class SimpleMessageManager implements MessageManagerInt {

	public void sendMessage(String message, int type) {
		if (type == errorMessage) {
			message = "[ERROR]   " + message;
		} else if (type == warningMessage) {
			message = "[WARNING] " + message;
		} 
		System.out.println(message);
	}
}
