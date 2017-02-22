package com.automatics.utilities.git;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.Properties;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig.Host;

import com.jcraft.jsch.Session;

public class GitUtilities 
{
	private Repository localRepo;
	private Git git;
	private Properties gitProperties;
	
	public Properties loadAndSetProperties(String configProperties)
	{
		try
		{
			File file = new File(configProperties);
			FileInputStream fileInput = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fileInput);
			setGitProperties(properties);
			return properties;
		}
		catch(Exception e)
		{
			System.out.println("[GitLoadProperties : loadProperties()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public void setGitProperties(Properties gitProperties) {
		this.gitProperties = gitProperties;
	}

	public void init()
	{
		try
		{
			String local_path = this.gitProperties.getProperty("LOCAL_PATH");
			File dir = new File(local_path);
			//Initialize repository
//			this.localRepo = new FileRepository(local_path + "/.git");
//			this.git = new Git(this.localRepo);
			this.git = Git.init().setDirectory(dir).call();
		}
		catch(Exception e)
		{
			System.out.println("[GitUtilities : init()] - Exception  : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void createLocalRepositary() 
	{
		try
		{
	        Repository newRepo = new FileRepository(this.gitProperties.getProperty("LOCAL_PATH") + "/.git");
	        System.out.println("[" + new Date() + "] GIT : Repository created at " + newRepo.getDirectory().getAbsolutePath());
	        newRepo.create();
	        newRepo.close();
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() +" : createRepositary()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
    }
	
	public void cloneRepository()
	{
		try
		{
			SshSessionFactory.setInstance(new JschConfigSessionFactory() {
				@Override
				protected void configure(Host host, Session session) {
					session.setConfig("StrictHostKeyChecking","no");
					session.setPassword("admin");
				}
			});
			String remotePath = this.gitProperties.getProperty("REMOTE_PATH");
			String localPath = this.gitProperties.getProperty("LOCAL_PATH");
			Git.cloneRepository().setURI(remotePath)
								 .setProgressMonitor(monitor)
								 .setDirectory(new File(localPath)).call();
			System.out.println("[" + new Date() + "] GIT : Repository cloned at " + localPath);
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " - cloneRepository()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
 	}
	
	public void addToRepository(String filename)
	{
		try
		{
			this.git.add().addFilepattern(filename).call();
			System.out.println("[" + new Date() + "] GIT : Add to repository " + filename);
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " - addToRepository()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void performCommit()
	{
		try
		{
			this.git.commit().setMessage("Committed on " + new Date());
			System.out.println("[" + new Date() + "] GIT : Commit Performed ");
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : performCommit()] :  Exception - " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void performPush()
	{
		try
		{
			this.git.push().call();
			System.out.println("[" + new Date() + "] GIT : Push Performed ");
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : perfromPush()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void performPull()
	{
		try
		{
			this.git.pull().call();
			System.out.println("[" + new Date() + "] GIT : Pull Performed ");
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : perfromPull()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}

	
}
