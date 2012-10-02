/*
 * Aether.java
 *
 * BrowserNG - A workbench for the browser of the new generation
 * Copyright (C) 2012 Ioannis Charalampidis
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package gr.wavesoft.webng.io.dlib;

import gr.wavesoft.webng.io.JarLoader;
import gr.wavesoft.webng.io.SystemConsole;
import java.io.File;
import java.net.MalformedURLException;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.codehaus.plexus.DefaultPlexusContainer;
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
 * Using the code snippets from https://docs.sonatype.org/display/AETHER/Home
 * @author icharala
 */
public class Aether {
    
    private static SystemConsole.Logger systemLogger = new SystemConsole.Logger(Aether.class, "Aether");
    private static RepositorySystem repoSystem;
    private static RepositorySystemSession repoSession;
    
    private static RepositorySystem newRepositorySystem()
        throws Exception
    {
        return new DefaultPlexusContainer().lookup( RepositorySystem.class );
    }

    private static RepositorySystemSession newSession( RepositorySystem system, String folder )
    {
        MavenRepositorySystemSession session = new MavenRepositorySystemSession();

        LocalRepository localRepo = new LocalRepository( folder );
        session.setLocalRepositoryManager( system.newLocalRepositoryManager( localRepo ) );

        return session;
    }

    public static void Initialize(String folder) {
        try {
            repoSystem = newRepositorySystem();
            repoSession = newSession(repoSystem, folder);
        } catch (Exception ex) {
            systemLogger.except(ex);
        }
    }
    
    public static void resolveDependency(String group, String artifact, String version) {
        try {
            Dependency dependency =
                new Dependency( new DefaultArtifact( group + ":"+artifact+":"+version ), "runtime" );
            RemoteRepository central = new RemoteRepository( "central", "default", "http://repo1.maven.org/maven2/" );

            CollectRequest collectRequest = new CollectRequest();
            collectRequest.setRoot( dependency );
            collectRequest.addRepository( central );
            
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
        } catch (DependencyResolutionException ex) {
            systemLogger.except(ex);
        } catch (DependencyCollectionException ex) {
            systemLogger.except(ex);
        }

    }
    
}
