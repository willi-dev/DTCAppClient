/**
 * 
 */
package client.app;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.HazelcastInstance;

/**
 * @author dell
 *
 */
public class StateResultListener implements EntryListener<String, String>{

	private static HazelcastInstance hzClient;
	
	public StateResultListener(HazelcastInstance hi){
		setHzInstance(hi);
	}
	
	public void setHzInstance(HazelcastInstance hi){
		hzClient = hi;
	}
	
	public static void clientTurnOff(){
		hzClient.shutdown();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.hazelcast.core.EntryListener#entryAdded(com.hazelcast.core.EntryEvent)
	 */
	@Override
	public void entryAdded(EntryEvent<String, String> event) {
		// TODO Auto-generated method stub
		if(event.getValue().equals("noresult")){
			System.out.println("state added : " + event.getValue());
			clientTurnOff();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.hazelcast.core.EntryListener#entryEvicted(com.hazelcast.core.EntryEvent)
	 */
	@Override
	public void entryEvicted(EntryEvent<String, String> event) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.hazelcast.core.EntryListener#entryRemoved(com.hazelcast.core.EntryEvent)
	 */
	@Override
	public void entryRemoved(EntryEvent<String, String> event) {
		// TODO Auto-generated method stub
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.hazelcast.core.EntryListener#entryUpdated(com.hazelcast.core.EntryEvent)
	 */
	@Override
	public void entryUpdated(EntryEvent<String, String> event) {
		// TODO Auto-generated method stub
		if(event.getValue().equals("noresult")){
			System.out.println("state updated: " + event.getValue());
			clientTurnOff();
		}
	}

}
