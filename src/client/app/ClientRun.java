/**
 * 
 */
package client.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
//import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
//import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
//import java.util.Set;

//import client.app.Oauth;
import client.app.DirectoryHandler;
import client.app.ResultListener;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

/**
 * @author Willi
 *
 */
public class ClientRun {
	/**
	 * @param args
	 */
	private static HazelcastInstance hzClient;
	private static ClientConfig clientConf;
	private static Socket client = null;
	private static OutputStreamWriter outStreamWrite = null;
	static boolean connected = false;
	private static IMap<String, String> mapCoordinator;
	private static IMap<String, String> mapResult;
	private static IMap<String, String> mapStateResult;
	private static String coordinatorAddress = null;
	private static String mapCoordinatorAddress = null;
	private static Integer paramsPort = 4001;
	private static DirectoryHandler dirHandler = new DirectoryHandler();
	//private static String thisMachine = null;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		 * set consumer key and consumer secret key of twitter API
		*/
		// Oauth oauth = new Oauth();
		// oauth.setConsumerKey("nYIIxPgqsT10rV2112zM2Q"); // set consumer key of twitter API
		// oauth.setConsumerSecret("IoWDuwpYmbwIS4qvUCHRZbl0XikzPvHtBHVg8vYAC4"); // set consumer key secret of twitter API
		// String endPointLimit = "https://api.twitter.com/1.1/application/rate_limit_status.json?resources=search";
		
		try{
			System.setProperty("hazelcast.logging.type", "none"); // do not print logging of client
			/*
			 * call method that return string addresses of cluster 
			 */
			
			clientConf = new ClientConfig();
			clientTurnOn(clientConf);
			
			mapCoordinatorAddress = getCoordinator(hzClient);
			
			ResultListener resultListener = new ResultListener(hzClient);
			StateResultListener stateResultListener = new StateResultListener(hzClient);
			
			File file = new File(dirHandler.getDirParams()+"./parameters.json");
			Boolean fileExist = file.exists();
			if(fileExist == true){
				JSONObject objparam = (JSONObject)JSONValue.parse(new FileReader(dirHandler.getDirParams()+"./parameters.json"));
				String params = objparam.toString();
				sendParamsToCoordinator(mapCoordinatorAddress, params);
			}
			//System.out.println("Coordinator : " + getCoordinator(hzClient));
			
			/*
			 * rate limiting of twitter API
			 */
			//String ratelimit = oauth.rateLimitStatus(endPointLimit);
			//System.out.println(ratelimit);
			
			mapStateResult = hzClient.getMap("mapstateresult");
			mapStateResult.addEntryListener(stateResultListener, true);
			
			mapResult = hzClient.getMap("mapResult");
			mapResult.addEntryListener(resultListener, true);
			
			//System.out.println("result: " + mapResult.get("result"));
			//System.out.println("Coordinator connected status:" + connected);
			//clientTurnOff();
			
		}catch(IOException ioEx){
			ioEx.printStackTrace();
		}
	}
	
	/*
	 * Method setHzClient
	 * set hazelcast instance client
	 */
	public static void setHzClient(ClientConfig clientConf){
		hzClient = HazelcastClient.newHazelcastClient(clientConf);
	}
	
	/*
	 * Method clientTurnOn
	 * running hazelcast client instance
	 */
	public static void clientTurnOn(ClientConfig config) throws FileNotFoundException{
		String[] stringAddresses = getStringFileMember();
		config.addAddress(stringAddresses);
		setHzClient(config);
	}
	
	/*
	 * Method clientTurnOff
	 * shutdown hazelcast client instance
	 */
	public static void clientTurnOff(){
		hzClient.shutdown();
	}
	
	/*
	 * Method getCoordinator
	 * get coordinator IP address
	 */
	public static String getCoordinator(HazelcastInstance hzclient){
		mapCoordinator = hzclient.getMap("coordinator");
		//coordinatorSocketAddress = mapCoordinator.get("coordinator");
		coordinatorAddress = mapCoordinator.get("coordinator");
		return coordinatorAddress;
	}
	
	/*
	 * Method getFileCoordinator
	 * get coordinator from coordinator.json
	 */
	public String getFileCoordinator() throws FileNotFoundException{
		File file = new File(dirHandler.getDirCoordinator()+"./coordinator.json");
		Boolean fileExist = file.exists();
		if(fileExist == true){
			// if task.json & task available
			JSONObject obj = (JSONObject)JSONValue.parse(new FileReader(dirHandler.getDirCoordinator()+"./coordinator.json"));
			String task = (String)obj.get("coordinator");
			return task;
		}else{
			return null;
		}
	}
	
	/*
	 * Method getThisMachine
	 * get this machine address
	 */
	public String getThisMachine(HazelcastInstance hzclient){
		return hzclient.getCluster().getLocalMember().getInetSocketAddress().getAddress().toString().substring(1);
	}
	
	/*
	 * Method getFileMember
	 * get list members from member.json
	 */
	//@SuppressWarnings("unchecked")
	public Queue<String> getQueueFileMember() throws FileNotFoundException{
		Queue<String> queuemember = new LinkedList<String>();
		Map<Integer, String> mapmember = new HashMap<Integer, String>();
		File file = new File(dirHandler.getDirMember()+"./member.json");
		Boolean fileExist = file.exists();
		if(fileExist == true){
			// get member
			JSONObject obj = (JSONObject)JSONValue.parse(new FileReader(dirHandler.getDirMember()+"./member.json"));
			JSONArray listmember = (JSONArray)obj.get("member");
			int sizeMembers = listmember.size();
			for(int i=0; i<sizeMembers; i++){
				JSONObject objarr = (JSONObject)JSONValue.parse(listmember.get(i).toString());
				int counter = i+1;
				mapmember.put(counter, objarr.get(String.valueOf(counter)).toString());
			}
			/*
			Iterator<JSONObject> it = listmember.iterator();
			int count = 1;
			while(it.hasNext()){
				mapmember.put(count, it.next().toString());
			}*/
			for(Map.Entry<Integer, String> entry : mapmember.entrySet()){
				queuemember.add(entry.getValue());
			}
			return queuemember;
		}else{
			return null;
		}
	}
	
	/*
	 * Method getStringFileMember
	 * get string format of member from file member.json
	 */
	//@SuppressWarnings("unchecked")
	public static String[] getStringFileMember() throws FileNotFoundException{
		File file = new File(dirHandler.getDirMember()+"./member.json");
		Boolean fileExist = file.exists();
		if(fileExist == true){
			JSONObject obj = (JSONObject)JSONValue.parse(new FileReader(dirHandler.getDirMember()+"./member.json"));
			JSONArray listmember = (JSONArray)obj.get("member");
			
			//addresses = listmember.toJSONString();
			
			int sizeMembers = listmember.size();
			String[] addresses = new String[sizeMembers];
			for(int i=0; i<sizeMembers; i++){
				JSONObject objarr = (JSONObject)JSONValue.parse(listmember.get(i).toString());
				int counter = i+1;
				addresses[i] = objarr.get(String.valueOf(counter)).toString();
				
				/*Set<String> keys = objarr.keySet();
				Iterator<String> it = keys.iterator(); 
				int counter = 0;
				while(it.hasNext()){
					String key = (String)it.next();
					String value = (String)objarr.get(key);
						addresses[counter] = it.next().toString();
				}
				*/
				
			}
			
			//Iterator<String> it = listmember.iterator();
			//int counter = 0;
			//int countarray = listmember.size();
			//String[] addresses = new String[countarray];
			/*while(it.hasNext()){
				addresses[counter] = it.next().toString();
				counter++;
			}*/
			return addresses;
		}else{
			return null;
		}
	}
	
	/*
	 * Method sendToCoordinator
	 * sending parameters of search to Coordinator of cluster
	 */
	public static void sendParamsToCoordinator(String coordinatorAddress, String params) throws IOException{
		String toSend = params;
		try{
			client = new Socket(coordinatorAddress, paramsPort);
			connected = true;
			outStreamWrite = new OutputStreamWriter(client.getOutputStream());
			outStreamWrite.write(toSend,0, toSend.length());
		}catch(UnknownHostException e){
			System.err.println(e.getMessage());
		}catch(IOException e){
			System.err.println(e.getMessage());
		}finally{
			outStreamWrite.close();
			client.close();
			connected = false;
		}
	}
	
	/*
	 * Method saveResult
	 * save result of Search
	 */
	public static void saveResult(String getSearch){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		String dateSimpan = dateFormat.format(date);
		String namefile = "search result_"+ dateSimpan;
		saveToJSON(getSearch, namefile);
	}
	
	/*
	 * Method saveResultJSON
	 * process save string result of search method to JSON file
	 */
	private static void saveToJSON(String stringContent, String nameFile){
		FileWriter fileWriter = null;
        try {
            String content = stringContent;
            File newTextFile = new File("./"+ nameFile +".json");
            fileWriter = new FileWriter(newTextFile);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException ex) {
        	ex.getStackTrace();
        } finally {
            try {
                fileWriter.close();
            } catch (IOException ex) {
               ex.getStackTrace();
            }
        }
	}
	
}
