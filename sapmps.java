// Super Awesome Port Mapping Protocol - Server // must run as root

import java.io.*;
import java.net.*;

public class sapmps
{

    public static final int SRVPORT = 6391; //designated SAPMP server port

    // return available port starting with p as default.
    public static int getport(int p)
    {   
	ServerSocket sc;
	boolean stop = false;
        try
	    {
		sc = new ServerSocket(p); // pick p if available
		sc.close();
	    }
            catch (IOException e) 
   	        {
		    try{
		    sc = new ServerSocket(0); // else pick random port
		    p = sc.getLocalPort();
		    sc.close();
		    } catch (IOException ee){ /*oops*/ }
		}
	return p;
    }//getport

    // function to convert string format IP into 4-byte array
    public static byte[] ipbytes(String addr)
    {
	byte[] B =  new byte[4]; // array to be constructed
	String[] S = addr.split("\\p{Punct}",4); // splits string into 4 parts
	//System.out.printf("after split: %s:%s:%s:%s\n",S[0],S[1],S[2],S[3]);
	for(int i=0;i<4;i++) 
	    B[i] = (byte)Integer.parseInt(S[i]);
	return B;
    } //ipbytes

    //function to return string-format IP address given byte array:
    public static String ipstring(byte[] B)
    {
	String addr = ""; // string to be returned
	for(int i=0;i<4;i++)
	    {
		int x = B[i];
		if (x<0) x = x+256;  // signed to unsigned conversion
		addr = addr + ""+ x;
		if (i<3) addr = addr+".";
	    }
	return addr;
    }// ipstring
    // in C, use inet_aton and inet_ntoa functions.


    public static void main(String[] args) throws Exception
    {
	String nataddr = args[0]; // natbox external ip
	byte[] NATADDR = ipbytes(nataddr);

	// info received from client:
	byte[] internalip = new byte[4];
	int internalport = 0;
	int externalport = 0;  // requested natbox port to be mapped
	byte protocol = 6;    // 17=udp, 6=tcp
	Runtime shell = Runtime.getRuntime();

	ServerSocket sfd = new ServerSocket(SRVPORT);
	Socket cfd = null; // comm socket
	sfd.setReuseAddress(true); // for devlopment use only
	while (true)
	    {
		cfd =  sfd.accept();
	        System.out.println("sapmp connect from "+cfd.getInetAddress());
		cfd.setSoTimeout(4000); // timeout on reads
		DataInputStream din;   // binary communicators
		DataOutputStream dout;
		din = new DataInputStream(cfd.getInputStream());
		dout = new DataOutputStream(cfd.getOutputStream());
		boolean ok = true;
		try
		    {   int c = 0; // counts bytes read
			while (c!=4)
			  c += din.read(internalip,c,4-c);
			internalport = din.readUnsignedShort();
			externalport = din.readUnsignedShort();
			protocol = din.readByte();
		    }
		catch (SocketTimeoutException ste)
		    {
			ok = false;
		    }
		if (externalport<1024) ok=false;
		if (protocol!=6 && protocol!=17) ok = false;
		if (internalip[0]!=10) ok = false; // only redirect inward.
		if (ok)
		{
		    String internip = ipstring(internalip);
		    int eport = getport(externalport);
		    // issue iptables command
		    String cmd = "/sbin/iptables -t nat -A PREROUTING ";
		    cmd += "-p "+protocol+" --dport "+eport+" ";
		    cmd += "-j DNAT --to "+internip+":"+internalport;
		    shell.exec(cmd); // execute iptables command
		    System.out.println("--issuing command: "+cmd); //echo
		    // send back mapped port and ip:
		    dout.writeShort((short)eport);
		    dout.write(NATADDR,0,4);
		}//ok
		else 
		    {
		      System.out.println("bad input from socket");
		      dout.writeShort(0);  // indicates failed map.
		      dout.write(NATADDR,0,4);
		    }
	        din.close();
		dout.close();
		cfd.close();
	    } // server loop
    }//main

}//sapmps

