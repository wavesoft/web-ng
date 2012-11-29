/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.wavesoft.webng.io.repos;

import gr.wavesoft.webng.io.JarLoader;
import gr.wavesoft.webng.io.SystemConsole;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;

import org.sonatype.aether.repository.MirrorSelector;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.graph.PreorderNodeListGenerator;


/**
 *
 * @author icharala
 */
public class AetherCore {

    private static SystemConsole.Logger systemLogger = new SystemConsole.Logger(Aether.class, "Aether");
    private static String localRepository;
    private static RepositorySystem repoSystem;
    private static RepositorySystemSession repoSession;

    public static void Initialize(String localRepository) {
        AetherCore.localRepository = localRepository;
        
        repoSystem = newRepositorySystem();
        repoSession = newSession(repoSystem, localRepository);
        
    }
    
    public static RepositorySystem newRepositorySystem() {
        return WNGRepositorySystemFactory.newRepositorySystem();
    }
    
    
    private static RepositorySystemSession newSession( RepositorySystem system, String folder ) {
        MavenRepositorySystemSession session = new MavenRepositorySystemSession();

        LocalRepository localRepo = new LocalRepository( folder );
        session.setLocalRepositoryManager( system.newLocalRepositoryManager( localRepo ) );
        
        return session;
    }

    public static RepositorySystemSession newRepositorySystemSession( RepositorySystem system ) {
        MavenRepositorySystemSession session = new MavenRepositorySystemSession();

        LocalRepository localRepo = new LocalRepository( "target/local-repo" );
        session.setLocalRepositoryManager( system.newLocalRepositoryManager( localRepo ) );

        //session.setTransferListener( new ConsoleTransferListener() );
        //session.setRepositoryListener( new ConsoleRepositoryListener() );

        // uncomment to generate dirty trees
        // session.setDependencyGraphTransformer( null );

        return session;
    }

    public static RemoteRepository newCentralRepository() {
        return new RemoteRepository( "central", "default", "http://repo1.maven.org/maven2/" );
    }

    
    public static RemoteRepository getPeerRepository(String group) {
        
        // Guess domain by flipping the group name
        String[] parts = group.split("\\.");
        String domain = "";
        for (int i=parts.length-1; i>=0; i--) {
            if (!domain.isEmpty()) domain += ".";
            domain += parts[i];
        }
        
        // Build a url
        String url = "http://"+domain+"/WEBNG";
        return new RemoteRepository( "private", "default", url );
        
    }
    
    
        public static void resolveDependency(String group, String artifact, String version) throws IOException {
        try {
            
            // Prepare the request
            CollectRequest collectRequest = new CollectRequest();
            
            // Create the run-time artifact dependency
            Dependency dependency = new Dependency( new DefaultArtifact( group + ":"+artifact+":"+version ), "runtime" );
            collectRequest.setRoot( dependency );
            
            // Pick default repository
            RemoteRepository central = new RemoteRepository( "central", "runtime", "http://repo1.maven.org/maven2/" );
            collectRequest.addRepository( central );
            
            // Pick custom repository
            collectRequest.addRepository( AetherCore.getPeerRepository(group) );
                        
            // Stert collecting dependencies
            DependencyNode node = repoSystem.collectDependencies( repoSession, collectRequest ).getRoot();
            DependencyRequest dependencyRequest = new DependencyRequest( node, null );
            repoSystem.resolveDependencies( repoSession, dependencyRequest  );
            
            // Generate a visitor node
            PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
            node.accept( nlg );
            
            // Load libraries
            for (File file: nlg.getFiles()) {
                systemLogger.info("Loading library "+file.getAbsolutePath());
                JarLoader.includeJar(file.getAbsolutePath());
            }
            
        } catch (MalformedURLException ex) {
            systemLogger.except(ex);
            throw new IOException("Unable to locate the dependency URL", ex);
        } catch (DependencyResolutionException ex) {
            systemLogger.except(ex);
            throw new IOException("An I/O error occured", ex);
        } catch (DependencyCollectionException ex) {
            systemLogger.except(ex);
            throw new IOException("Unable to collect dependencies", ex);
        }

    }
    
}
