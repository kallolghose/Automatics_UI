package com.automatics.utilities.git;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.RevWalkUtils;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.filter.PathFilter;

import com.jcraft.jsch.Session;

public class GitUtilities 
{
	public static String GIT_PROPERTY_PATH = "git_config.properties";
	private Repository localRepo;
	private static Git git = null;
	private Properties gitProperties;
	
	private static String ERR_MSG_GIT = "";
	
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

	
	public String getErrMsg() {
		return ERR_MSG_GIT;
	}

	public void init()
	{
		try
		{
			String local_path = this.gitProperties.getProperty("LOCAL_PATH");
			File dir = new File(local_path);
			if(this.git == null)
				this.git = Git.init().setDirectory(dir).call();
		}
		catch(Exception e)
		{
			System.out.println("[GitUtilities : init()] - Exception  : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void initExistingRepository()
	{
		try
		{
			String localRepoPath = this.gitProperties.getProperty("LOCAL_PATH");
			File file = new File(localRepoPath);
			//FileRepositoryBuilder builder = new FileRepositoryBuilder();
			//Repository repository = builder.setGitDir(file).readEnvironment().findGitDir().build();
			if(this.git == null)
				this.git = Git.open(file);
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : initExistingRepository()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public boolean createLocalRepositary() 
	{
		try
		{
	        Repository newRepo = new FileRepository(this.gitProperties.getProperty("LOCAL_PATH") + "/.git");
	        System.out.println("[" + new Date() + "] GIT : Repository created at " + newRepo.getDirectory().getAbsolutePath());
	        newRepo.create();
	        newRepo.close();
	        return true;
		}
		catch(Exception e)
		{
		
			ERR_MSG_GIT = e.getMessage();
			System.out.println("[" + getClass().getName() +" : createRepositary()] - Exception : " + e.getMessage());
			e.printStackTrace();
			return false;
		}
    }
	
	public boolean cloneRepository()
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
								 .setDirectory(new File(localPath)).call();
			System.out.println("[" + new Date() + "] GIT : Repository cloned at " + localPath);
			return true;
		}
		catch(Exception e)
		{
			if(e.getMessage().contains("Repository already exists"))
				return true;
			ERR_MSG_GIT = e.getMessage();
			System.out.println("[" + getClass().getName() + " - cloneRepository()] - Exception : " + e.getMessage());
			e.printStackTrace();
			return false;
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
			this.git.add().addFilepattern(".").call();
			this.git.commit().setMessage("Automatics Committed on : " + new Date()).call();
			System.out.println("[" + new Date() + "] GIT : Commit Performed");
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : performCommit()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void performSpecificCommit(String filename)
	{
		try
		{
			this.git.add().addFilepattern(filename).call();
			this.git.commit().setMessage("Automatics Committed Specifc on " + new Date()).call();
			System.out.println("[" + new Date() + "] GIT : Specfic Commit Performed for (" + filename + ")");
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
			SshSessionFactory.setInstance(new JschConfigSessionFactory() {
				@Override
				protected void configure(Host host, Session session) {
					session.setConfig("StrictHostKeyChecking","no");
					session.setPassword("admin");
				}
			});
			this.git.push().call();
			System.out.println("[" + new Date() + "] GIT : Push Performed ");
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : perfromPush()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public boolean performSpecificPull(String fileName)
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
			this.git.fetch().call();
			this.git.checkout().setName("origin/master").setStartPoint("origin/master").addPath(fileName).call();
			this.git.add().addFilepattern(fileName).call();
			System.out.println("[" + new Date() + "] GIT : Fetching specific file (" + fileName + ")");
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
	public void performPull()
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
			this.git.pull().call();
			System.out.println("[" + new Date() + "] GIT : Pull Performed ");
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : perfromPull()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public boolean performGITSyncOperation()
	{
		try
		{
			boolean ret_val = true;
			SshSessionFactory.setInstance(new JschConfigSessionFactory() {
				@Override
				protected void configure(Host host, Session session) {
					session.setConfig("StrictHostKeyChecking","no");
					session.setPassword("admin");
				}
			});
			this.git.pull().call();
			this.git.add().addFilepattern(".").call();
			this.git.commit().setMessage("Automatics Committed on : " + new Date()).call();
			for(int i=0;i<5;i++)
			{
				try
				{
					this.git.push().call();
					ERR_MSG_GIT = "";
					ret_val = true;
					break;
				}
				catch(Exception e)
				{
					try
					{
						this.git.pull().call();
					}
					catch(Exception e1)
					{
						ERR_MSG_GIT = e1.getMessage();
						ret_val = false;
					}
				}
			}
			return ret_val;
		}
		catch(Exception e)
		{
			ERR_MSG_GIT = e.getMessage();
			System.out.println("[" + getClass().getName() + " : performGITSyncOperation()] - Exception : " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	
	
	public boolean getSync(String fileLocation)
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
			FetchCommand fetchCommand = this.git.fetch();
			fetchCommand.call();
			
			List<Ref> call = this.git.branchList().setListMode(ListMode.ALL).call();
			
            Ref localRef = call.get(0);
            Ref remoteRef = call.get(1);
            
            Repository repo = this.git.getRepository();
            AbstractTreeIterator oldTreeParser = prepareTreeParser(repo, localRef.getObjectId().name());
            AbstractTreeIterator newTreeParser = prepareTreeParser(repo, remoteRef.getObjectId().name());
            List<DiffEntry> diffs = this.git.diff().setNewTree(newTreeParser).setPathFilter(PathFilter.create(fileLocation)).
            						call();
            for(DiffEntry entry : diffs)
            {
            	switch(entry.getChangeType())
            	{
            	case ADD:
            	case MODIFY:
            	case DELETE:
            	case COPY:
            	case RENAME:
            		return true;
            	}
            }
			return false;
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " : getDifference()] - Exception : " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	public Status getStatus()
	{
		try
		{
			Status status = this.git.status().call();
			return status;
		}
		catch(Exception e)
		{
			System.out.println("[" + getClass().getName() + " getStatus()] - Exception : " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	private AbstractTreeIterator prepareTreeParser(Repository repository, String objectId){
        
        try 
        {
        	RevWalk walk = new RevWalk(repository);
            RevCommit commit = walk.parseCommit(ObjectId.fromString(objectId));
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
            try  
            {
            	ObjectReader oldReader = repository.newObjectReader();
                oldTreeParser.reset(oldReader, tree.getId());
            }
            catch(Exception e){}

            walk.dispose();

            return oldTreeParser;
        }
        catch(Exception e)
        {
        	System.out.println("[" + getClass().getName() + " : prepareTreeParser()] - Exception : " + e.getMessage());
        	e.printStackTrace();
        	return null;
        }
    }
	
	public boolean getDiff(String filename)
	{
		try
		{
			List<DiffEntry> diffs = git.diff().setPathFilter(PathFilter.create(filename)).call();
			for(DiffEntry entry : diffs)
			{
				switch(entry.getChangeType())
            	{
            	case ADD:
            	case MODIFY:
            	case DELETE:
            	case COPY:
            	case RENAME:
            		return true;
            	}
			}
			return false;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	
}
