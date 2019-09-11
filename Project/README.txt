Nuno Silva (up201404380) e Paulo Babo (up201404022) - T03G13 - SDIS 2016/2017


Source files location:
 - The source files are located in "src" folder.


Compile:
 - You can use javac commad or eclipse.
 - Eclipse:
	- 1º Select: Import...
	- 2º Select: Existing project
	- 3º Select: "Projeto_1" folder


Run:
 - After compiling, using a Terminal window, go to the "bin" folder. Now you can start the program.

   RMI
    - First of all, you need to start an rmiregistry instance.
	- you can double click the "1_startRMI.bat" or you can manually enter in the Terminal "start rmiregistry".

   PEER
    - You can double click the "2_startPeers.bat" and it will open 4 Peers or you can manually do it 
	by entering in the Terminal:
		"java Server.Peer <mcAddress> <mcPort> <mdbAddress> <mdbPort> <mdrAddress> <mdrPort> <peerID>"
		
		example: "java Server.Peer 224.0.0.0 4000 224.0.0.0 4001 224.0.0.0 4002 123"

   TestAPP
    - In the Terminal, you should invoke as follows:
		"java Client.TestApp <peer_ap> <sub_protocol> <opnd_1> <opnd_2>"
		
		example: "java Client.TestApp 123 BACKUP ../ficheirosTeste/teste.txt 1"



