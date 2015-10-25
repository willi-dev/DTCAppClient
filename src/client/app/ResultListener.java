/**
 * 
 */
package client.app;

//import java.util.Collection;

import java.util.Iterator;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
//import com.hazelcast.core.MultiMap;
import com.hazelcast.core.Member;

/**
 * @author dell
 *
 */
public class ResultListener implements EntryListener<String, String>{

	private static HazelcastInstance hzClient;
	private static IMap<String, String> mapCoordinator;
	//private MultiMap<String, String> multiMapResult;
	private static String coordinatorAddress;
	
	public ResultListener(HazelcastInstance hi){
		setHzInstance(hi);
	}
	
	public void setHzInstance(HazelcastInstance hi){
		hzClient = hi;
	}
	
	public static void clientTurnOff(){
		hzClient.shutdown();
	}
	
	
	
	@Override
	public void entryAdded(EntryEvent<String, String> event) {
		// TODO Auto-generated method stub
		mapCoordinator = hzClient.getMap("coordinator");
		//coordinatorSocketAddress = mapCoordinator.get("coordinator");
		coordinatorAddress = mapCoordinator.get("coordinator");
		System.out.println("Coordinator : " + coordinatorAddress);
		
		//multiMapResult = hzClient.getMultiMap("multimapresult");
		
		System.out.println("result : "+ event.getValue());
		
		Iterator<Member> it = hzClient.getCluster().getMembers().iterator();
		while(it.hasNext()){
			System.out.println("Member : " +it.next().getInetSocketAddress().getAddress().toString().substring(1));
		}
		
		/*System.out.println("replication of result : ");
		
		Collection<String> col = multiMapResult.get(event.getValue().toString());
		for(String s : col){
			//if(!s.equals(coordinatorAddress)){
				System.out.println("http://"+s+"/dtcapp/results/"+event.getValue());
			//}
		}*/
		
		clientTurnOff();
	}

	@Override
	public void entryEvicted(EntryEvent<String, String> event) {
		// TODO Auto-generated method stub
		System.out.println("result evicted");
	}

	@Override
	public void entryRemoved(EntryEvent<String, String> event) {
		// TODO Auto-generated method stub
		System.out.println("result removed");
	}

	@Override
	public void entryUpdated(EntryEvent<String, String> event) {
		// TODO Auto-generated method stub
		System.out.println("result updated");
	}

}
