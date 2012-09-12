
Demo page(s)
============

This project contains the demo pages that are hosted on my demo server. 
The *hello world* view has FQN:

    gr.wavesoft.demo.hello

I want to try to do the same!
=============================

Don't get too excited and implement your entire website in a single jar file! 
Remember that the project is not yet finished and it's main purpose is to split
the data from the view!

But if you are really, really, really in hurry and you absolutely want to try to make your own view, follow these steps:

1. Create a **webng** sub-domain for your domain - The browser will fetch the data from http://webng.<your domain>.<your tld>/ ...
2. Open the project and rename the basic package in order to reflect your domain name - For example com.example.demo for example.com
3. Rebuild the jar file and upload it in your webserver (ex. in the ng-views sub-folder).
3. Create an exports file - A plain textfile with the following lines:

    com.example.demo.hello: ng-views/demo.jar

Modify this file according to your needs and you are ready to go!
Keep in mind the following things:

1. You cannot use wildcards that point into a single jar file. You must type one-by-one all the FQN of the classes that you are exporting.
2. Currently the cache mechanism is in early state and has no TTL. If you want to clear your cache delete the data folder of the browser.

The data folder is found in the following locations:

* **Windows:** %appdata%/WebNGBrowser
* **OSX:** /Users/*user*/Library/ApplicationSupport/WebNGBrowser
* **Linux:** /home/*user*/.WebNGBrowser

Good luck!
