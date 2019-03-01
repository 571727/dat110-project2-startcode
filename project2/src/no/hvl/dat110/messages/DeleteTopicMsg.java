package no.hvl.dat110.messages;

public class DeleteTopicMsg extends Message {
	
	private String topic;
	// Implement objectvariables, constructor, get/set-methods, and toString method
	
	public DeleteTopicMsg(String user, String topic) {
		super(MessageType.DELETETOPIC, user);
		this.topic = topic;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	@Override
	public String toString() {
		return "Message [type=" + super.getType() + ", user=" + super.getUser() + "topic=" + topic + "]";
	};
}
