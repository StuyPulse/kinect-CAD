KinectCAD:

Installation:
	 To use the client, put libs, native, and the jar itself into one folder. To run from the command line, type 'java -jar kinectcad.jar'. 
	 To install the server, make sure 'application files', kinectCad-Server.application, and setup.exe are in the same folder. Then run setup.exe.

Client:
	 The client is completely independent of the server and can be used without the Kinect. It can load models in the wavefront .obj format and the accompanying .mtl libraries.
	 For additional command line options, type 'java -jar kinectcad.jar -?'.

Server:
	 The server can be used independently of the client, but accomplishes absolutely nothing.
	 If the server is running when a client is started with the '-k' option, the client will connect to and begin accepting input from the server.
	 When a person is tracked by the kinect, their right wrist and elbow will be marked on the camera feed.
	 When that person is lost, the kinect will attempt to choose a different tracking target.