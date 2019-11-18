import java.net.*;
import java.io.*;
import java.util.*;

class TftpException extends Exception {
  public TftpException() { super(); }
  public TftpException(String s) { super(s); }
}

class UseException extends Exception {
  public UseException() { super(); }
  public UseException(String s) { super(s); }
}

public class tftpClient {

  public static void main(String argv[]) throws TftpException, UseException {
    String host="";

    try {

      // Process command line

      if (argv.length==0) 
        throw new UseException("usage:  tftpClient [host] file");

      if (argv.length==1)
        host="localhost";
      else
        host=argv[0];

      String fileName=argv[argv.length-1];

      // Create socket and open output file

      InetAddress server = InetAddress.getByName(host);
      DatagramSocket sock = new DatagramSocket();

      FileOutputStream outFile = new FileOutputStream(fileName);

      // Send request to server

      TFTPread reqPak = new TFTPread(fileName);
      reqPak.send(server,sock);

      int pakLen=TFTPpacket.maxTftpPakLen;

      // Process the transfer
	
	  
      int bytesOut = 512,
		  pakCount = 0;
	  while(bytesOut == 512){		TFTPpacket inPak = TFTPpacket.receive(sock);

        if (inPak instanceof TFTPerror)  {
          TFTPerror p=(TFTPerror)inPak;
          throw new TftpException(p.message());
        }
        else if (inPak instanceof TFTPdata) {
          TFTPdata p=(TFTPdata)inPak;

          int blockNum=p.blockNumber();
          bytesOut=p.write(outFile);

          TFTPack ack=new TFTPack(blockNum);
          ack.send(p.getAddress(),p.getPort(),sock);
		}		else			throw new TftpException("Unexpected response from server");
		++pakCount;
	  }

      outFile.close();
      sock.close();
    }

    catch (UnknownHostException e) { System.out.println("Unknown host "+host); }
    catch (IOException e) { System.out.println("IO error, transfer aborted"); }
    catch (TftpException e) { System.out.println(e.getMessage()); }
    catch (UseException e) { System.out.println(e.getMessage()); }
  }
}
