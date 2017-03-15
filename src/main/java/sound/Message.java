package sound;

import java.util.Date;

public class Message {
	
	String phoneNumber;
	String messageBody;
	boolean read;
	
	public Message(String phoneNumber, String messageBody) {
		super();
		this.phoneNumber = phoneNumber;
		this.messageBody = messageBody;
		this.read = false;
	}

		public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

		public String getPhoneNumber() {
		return phoneNumber;
	}
		
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getMessageBody() {
		return messageBody;
	}
	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}


	
}
