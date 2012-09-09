web-ng
======

An attempt to create a better information browsing on the internet, by redefining most
of the components of the current system. That's the New Generation of the web.

You can usually find a development build of the project in the broser/app/ folder.

Description
-----------

This project consists of creating a new data representation language and a web browser that can render it. 
This new language contains only pure data and some hinting attributes that will define how the data are presented. 
In short, this project aims to **separate the data from the view**.

Components
==========

WebNL YAML Language
-------------------

WebNG fetches it's content over HTTP/HTTPS like a regular web browser. The data in the new web have no strict syntax 
but are structured. I picked YAML (Yet Another Markup Language) for this purpose, because it's human-readable yet fast 
in parsing. This gives a big bonus on the development side.

Here is an example of a small blog:

    ~view    : gr.wavesoft.views.blog
    posts    :
        - Title: New website
          Date: 2001-11-23 15:03:17 -5
          Body: |
              This is a demonstration on how easy you can create websites using
              the new generation of the web!
              
        - Title: Testing
          Date: 2001-11-23 15:03:17 -5
          Body: |
              Proof that <b>HTML</b> can still be used! <img src="test" style="float: right" />

Straight-forward right? Anyone can write (and even maintain by hand) this blog!

Additionally YAML provides node referencing and other cool features in the data language itself.
And of course, remote referencing of nodes is also possible:

    ~view    : gr.wavesoft.themes.personal
    title    : Welcome to my personal homepage
    sections :
        
        - section: Blog
          ~src  : blog.yml
          
        - section: About
          ~view 	: gr.wavesoft.views.about
          name      : Ioannis Charalampidis
          contact   : <nospam>@mailserver.com
		  photo     : images/profile.jpg
        
        - section: Big data
          ~src: big.yml


Ok, what about the view for this data set?

WebNG Views
-----------

Each data node, defined by the WebNG YAML Language is renderable. The author can pick any third-party rendering view available
on the internet to render it's node. It is not required to download or host that view!

WebNG Views are Java panels (Swing or JavaFX) that are referenced by their Fully-Qualified class name. I call them *Universal Views*
because they can be located and fetched no matter where they are if you know their name.

Here are some details...

### Introduction - Reasoning

The biggest problem with HTML is that it merged data and view in a problematic way. Even though there are revisions over
revisions, we still haven't seen the Web of semantics widely spread. Additionally, the aesthetics of the world has changed
so much that the simple HTML is not enough. That's why we have HTML5 right? CSS3 is indeed an awesome tool, Javascript with 
HTML5 has amazing new features... why then we have so many Javascript-assisting libraries? Why do we have boilerplates and
CSS themes just to get started? 

Additionally more and more people ask for security in the code and fast-executing javascript code. Yet we are still sending
plaintext javascript *source* code to the client.

In my opinion all of these problems, plus many more can be solved with what I call *Universal Views*.

### Universal Views

Universal Views are nothing more than Java Swing or JavaFX panels. That's it! We have a structured, visual representation of 
data with a technology that is already out there that has all the advantages we are asking for:

 * *Execution speed* - Java is compiled bytecode. It is transferred quickly and executed fast by the JVM.
 * *Code security* - We are transmitting bytecode. Reverse engineering (even if it is still possible) is now much more difficult.
 * *Browsing speed* - Downloading bytecode is much faster than downloading source code and compiling it.
 * *Powerful control over the view* - Forget about all the nasty javascript that you are using right now, just to make an image fade out. You now have full control over your view.
 * *Code security* - Java provides already sandboxing and protection over privileged code execution.

Now think the following: You have deployed your own Wordpress portal and you are maintaining your own blog. What about updates?
The *Universal Views* are meant not to be stored in the same server with the data. This means that they are maintained and updated
independently. This automatically means that the user is now (almost) free of security/server updates! (Almost means that the user
must still update the CMS back-end - if he is using any)


Data Structure
--------------

Since the language is not strictly-structured, The data layout depends on the view used. 
However, as a good practice it is recommended to define an abstract set of rules that the views will use.

For example, there might be many blog implementations. However the core syntax of all of them is the same:

    /posts = ARRAY[
		/title
		/body
		/comments = ARRAY[
				/author
				/text
			]
	]

Other implementations might add additional nodes, but all of them must implement these basic rules.

The language for this ruleset is not yet defined. 
You are free to submit your own ideas! :)

License
=======

This project is licensed under GNU General Public License 3
For more details see here: http://www.gnu.org/licenses/

Author
======

Main author and developer of this project is *Ioannis Charalampidis*
Contact me via Github if you have any suggestion or recommendation :)
