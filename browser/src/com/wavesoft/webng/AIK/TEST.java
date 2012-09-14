/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wavesoft.webng.AIK;

import com.wavesoft.webng.AIK.Triggers.DownloadTrigger;
import com.wavesoft.webng.AIK.Triggers.TimedTrigger;
import gr.wavesoft.webng.io.DownloadManager.DownloadJob;

/**
 *
 * @author icharala
 */
public class TEST {
    
    public static class C3 {
        
        @Asynchronous()
        public void test() {
            System.out.println("Test!");
        }
        
        @Asynchronous
        public void shutdown() {
            Kernel.stop();
        }
        
        @Asynchronous
        public void downloaded(DownloadJob job) {
            if (job == null) {
                System.out.println("Download failed!");
            } else {
                System.out.println("Download completed: "+job.buffer);
            }
        }
        
        @AsyncronousConstructor
        public void start() {
            Kernel.yield("doit");
        }
        
        @Asynchronous(alias="doit")
        public void main_function() {
            TimedTrigger.delay(500, "test");
            TimedTrigger.delay(5000, "shutdown");
            System.out.println(Kernel.waitFor(Kernel.yield("hello")));
            Kernel.waitFor(DownloadTrigger.download("http://www.google.com", ""));
            System.out.println("Blocking!");
        }
        
        @Asynchronous(alias="hello")
        public String lala() {
            return "Yup, that's me babe!";
        }
        
    }
    
    public static class Thread1 implements Runnable {

        @Override
        public void run() {
            Kernel.register(this);
        }
        
        @AsyncronousConstructor
        public void main() {
            Kernel.waitFor(DownloadTrigger.download("http://www.google.com", ""));
            System.out.println("Blocking T1!");
        }
        
    
    }
    
    public static class Thread2 implements Runnable {

        @Override
        public void run() {
            Kernel.register(this);
        }
        
        @AsyncronousConstructor
        public void main() {
            Kernel.waitFor(DownloadTrigger.download("http://www.google.com", ""));
            System.out.println("Blocking T2!");
        }
        
    }
    
    public static void main(String[] args) {
        
        Thread t1 = new Thread(new Thread1());
        Thread t2 = new Thread(new Thread2());
        t1.start();
        t2.start();
        
        Kernel.register(new C3());
        Kernel.start();
        
    }
    
}
