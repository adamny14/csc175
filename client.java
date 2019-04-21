/* tcp client skeleton - Java version */

import java.io.*;
import java.net.*;
import java.math.*;

public class client
{
  // argv[0] is server ip addr, argv[1] is server port
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

  public static void main(String[] argv)
    {
      Socket cfd;      // communication socket "fd" is carry-over from C
      DataInputStream din;   // binary communicators
      DataOutputStream dout;  
      byte[] buffer = new byte[128];  // data buffer
      int x, y;
      InetAddress ip = null;
      try{
      ip = InetAddress.getByName(argv[2]);
      }
      catch( Exception e)
	{ e.printStackTrace();}
      byte[] byteIP = ipbytes("10.0.10.0");
      byte[] port = BigInteger.valueOf(Integer.parseInt(argv[3])).toByteArray();
      byte[] newPort = BigInteger.valueOf(Integer.parseInt(argv[4])).toByteArray();
      byte[] protocal = BigInteger.valueOf(Integer.parseInt(argv[5])).toByteArray();
      try
      {
	  // connect to server:
	  cfd = new Socket(argv[0],Integer.parseInt(argv[1]));
 	  din = new DataInputStream(cfd.getInputStream());
	  dout = new DataOutputStream(cfd.getOutputStream());
	  
	  // now in ESTABLISHED state
	  
	  dout.write(byteIP, 0, 4);
	  //dout.write(port, 0, 2);
	  //dout.write(newPort, 0, 2);
	  //dout.write(protocal, 0, 1);
	  dout.writeShort((short)80);
	  dout.writeShort((short)8000);
	  dout.writeByte(6);
	   x = din.readInt();
          readFully(din,buffer,0,x);
          dout.write(buffer,120,8);
	  //dout.write(buffer,120,8);
	  System.out.println("complete");
	  cfd.close();
      } catch (Exception ee) {ee.printStackTrace(); System.exit(1);}
    } // main


    // the following function only returns after EXACTLY n bytes are read:

    static void readFully(DataInputStream din, byte[] buffer, int start, int n)
           throws IOException
    {
        int r = 0; // number of bytes read
	while (r<n)
	    { 
		r += din.read(buffer,start+r,n-r);
	    }
    }//readFully
}

