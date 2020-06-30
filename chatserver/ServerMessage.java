package chatserver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class ServerMessage implements Serializable, Message<ProtocolType, Message<?, ?>> {
	
	private static final long serialVersionUID = 1L;
	private ProtocolType protocolKey;	
	public Message<?, ?> innerMessage;
	
	public ServerMessage(ProtocolType protocolKey, Message<?, ?> innerMessage) {
		this.protocolKey = protocolKey;
		this.innerMessage = innerMessage;
	}
	
	public void setKey(ProtocolType key) {
		this.protocolKey = key;
	}

	@Override
	public ProtocolType getKey() {
		return protocolKey;
	}
	
	public void setData(Message<?, ?> innerMessage) {
		this.innerMessage = innerMessage;
	}	

	@Override
	public Message<?, ?> getData() {		
		return innerMessage;
	}	

	@SuppressWarnings("unchecked")
	public static Message<Integer, Message<?, ?>> toMessageObject(ByteBuffer buffer) 
		throws IOException, ClassNotFoundException {
		Message<Integer, Message<?, ?>> message = null;
		ByteArrayInputStream bufferInput = null;
		ObjectInputStream objectInput = null;
		byte[] bufferMessage = buffer.array();
		
		try {
			bufferInput = new ByteArrayInputStream(bufferMessage);
			objectInput = new ObjectInputStream(bufferInput);
			
			message = (Message<Integer, Message<?, ?>>) objectInput.readObject();
		} 
		finally {
			if (null != bufferInput) {
				bufferInput.close();
			}
			
			if (null != objectInput) {
				objectInput.close();
			}
		}
		
		return message;
	}
	public static ByteBuffer toByteBuffer(Message<Integer, Message<?, ?>> message)
			throws IOException {
		byte[] bufferMessage = null;
		ByteArrayOutputStream bufferOutput = null;
		ObjectOutputStream objectOutput = null;
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		
		try {
		
			bufferOutput = new ByteArrayOutputStream();
			objectOutput = new ObjectOutputStream(bufferOutput);
			objectOutput.writeObject(message);
			objectOutput.flush();
			bufferMessage = bufferOutput.toByteArray();
		}
		finally {
			if (null != objectOutput) {
				objectOutput.close();
			}
			
			if (null != bufferOutput) {
				bufferOutput.close();
			}
		}
		
		return buffer.put(bufferMessage);
	}
	
	public static byte[] toByteArray(Object obj) throws IOException {
		byte[] bytes = null;
        try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
        	ObjectOutputStream oos = new ObjectOutputStream(bos)){
            oos.writeObject(obj);
            bytes = bos.toByteArray();
        }
        
        return bytes;
    }
	
	public static Object toObject(byte[] bytes) throws ClassNotFoundException, IOException {
		Object obj = null;
        try(ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        	ObjectInputStream ois = new ObjectInputStream(bis);) {
            obj = ois.readObject();
        }

        return obj;
	}
	@Override
	public String toString() {
		return ("protocol key: " + protocolKey + "\t innerMessage: " + innerMessage);
	}
}

class ChatMessage implements Message<ChatProtocolKeys, String> , Serializable{
	private static final long serialVersionUID = 1L;
	private ChatProtocolKeys key;
	private String data;
	
	public ChatMessage (ChatProtocolKeys key, String data) {
		this.key = key;
		this.data = data;
	}
	
	@Override
	public ChatProtocolKeys getKey() {
		return key;
	}
	
	@Override
	public String getData() {
		return data;
	}


	@Override
	public String toString() {
		return "ChatMessage [key=" + key + ", data=" + data + "]";
	}

	@Override
	public void setKey(ChatProtocolKeys key) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setData(String data) {
		// TODO Auto-generated method stub
		
	}	
}