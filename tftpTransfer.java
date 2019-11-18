import java.net.*;
import java.io.*;
import java.util.*;

class TftpException extends Exception {
  TftpException() { super(); }
  TftpException(String s) { super(s); }
}
  
class tftpTransfer extends Thread {

  protected DatagramSocket sock;
  protected InetAddress host;
  protected int port;
  protected FileInputStream source;

  public tftpTransfer(TFTPread request) throws TftpException {
    try {
      sock=new DatagramSocket();

      host=request.getAddress();
      port=request.getPort();

      File srcFile = new File(request.fileName());

      if (srcFile.exists() && srcFile.isFile() && srcFile.canRead()) {
        source = new FileInputStream(srcFile);
        this.start();
      }
      else
        throw new TftpException("access violation");

    }
    catch (Exception e) { 
      TFTPerror ePak = new TFTPerror(1,e.getMessage());

      try { ePak.send(host,port,sock); } catch (Exception f) {}

      System.out.println("Client start failed:  "+e.getMessage()); 
    }
  }

  public void run() {
    int bytesRead = TFTPpacket.maxTftpPakLen;

    try {
      for (int blkNum=0; bytesRead==TFTPpacket.maxTftpPakLen; blkNum++) {
        TFTPdata outPak=new TFTPdata(blkNum,source);
        bytesRead=outPak.getLength();
        outPak.send(host,port,sock);

        TFTPpacket ack=TFTPpacket.receive(sock);
        if (!(ack instanceof TFTPack)) break;
      }
    }
    catch (Exception e) {
      TFTPerror ePak = new TFTPerror(1,e.getMessage());

      try { ePak.send(host,port,sock); } catch (Exception f) {}

      System.out.println("Client failed:  "+e.getMessage());
    } 

    System.out.println("Client terminated");
  }
}
