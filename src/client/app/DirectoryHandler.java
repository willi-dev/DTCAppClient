package client.app;

import java.io.File;

public class DirectoryHandler {

	private String pathExecJar = null;
	private String pathDirParams = null; // for client
	private String pathDirToken = null; // for client and server cluster
	private String pathDirResults = null; // for server cluster store a results of crawl
	private String pathDirCoordinator = null; // for coordinator directory
	private String pathDirMember = null; // for member directory
	
	public DirectoryHandler(){
		pathExecJar = ClientRun.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	}
	
	public String getDirParams(){
		pathDirParams = new File(pathExecJar).getParentFile().getPath()+"/params";
		return pathDirParams;
	}
	
	public String getDirToken(){
		pathDirToken = new File(pathExecJar).getParentFile().getPath()+"/token";
		return pathDirToken;
	}
	
	public String getDirResult(){
		pathDirResults = new File(pathExecJar).getParentFile().getPath()+"/results";
		return pathDirResults;
	}
	
	public String getDirCoordinator(){
		pathDirCoordinator = new File(pathExecJar).getParentFile().getPath()+"/coordinator";
		return pathDirCoordinator;
	}
	
	public String getDirMember(){
		pathDirMember = new File(pathExecJar).getParentFile().getPath()+"/member";
		return pathDirMember;
	}
	
	
	
}
