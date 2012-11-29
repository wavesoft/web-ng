/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.wavesoft.webng.io.repos;

import gr.wavesoft.webng.io.repos.wagons.SharedHTTPWagon;
import gr.wavesoft.webng.io.repos.wagons.WebNGHTTP;
import org.apache.maven.wagon.Wagon;
import org.sonatype.aether.connector.wagon.WagonProvider;

/**
 *
 * @author icharala
 */
public class WNGWagonProvider implements WagonProvider {

    public Wagon lookup(String string) throws Exception {
        System.out.println("WNGWagonProvider: Looking for "+string);
        if ("http".equals(string)) {
            return new SharedHTTPWagon();
        } else {
            return null;
        }
    }

    public void release(Wagon wagon) {
        System.out.println("WNGWagonProvider: Release "+wagon.toString());

    }
    
}
