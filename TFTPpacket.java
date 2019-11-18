import java.net.*;
import java.io.*;
import java.util.*;

public class TFTPpacket {

  // TFTP constants

  public static int tftpPort = 69;
  public static int maxTftpPakLen=516;
  public static int maxTftpData=512;

  // Tftp opcodes (RFC 1350)

  protected static final short tftpRRQ=1;
  protected static final short tftpWRQ=2;
  protected static final short tftpDATA=3;
  protected static final short tftpACK=4;
  protected static final short tftpERROR=5;

  // Packet Offsets

  protected static final int opOffset=0;
  protected static final int fileOffset=2;
  protected static final int blkOffset=2;
  protected static final int dataOffset=4;
  protected static final int numOffset=2;
  protected static final int msgOffset=4;

  // The packet

  protected int length;
  protected byte [] message;

  // Address info (requried for replies)

  protected InetAddress host;
  protected int port;

  // Constructor 
 
  public TFTPpacket() {
    message=new byte[maxTftpPakLen]; 
    length=maxTftpPakLen; 
  } 

  // Methods to send and receive packets

  public static TFTPpacket receive(DatagramSocket sock) throws IOException {
    TFTPpacket in=new TFTPpacket(), retPak=null;
    DatagramPacket inPak = new DatagramPacket(in.message,in.length);

    sock.receive(inPak);

    switch (in.get(0)) {
      case tftpRRQ:
        retPak=new TFTPread();
        break;
      case tftpWRQ:
        retPak=new TFTPwrite();
        break;
      case tftpDATA:
        retPak=new TFTPdata();
        break;
      case tftpACK:
        retPak=new TFTPack();
        break;
      case tftpERROR:
        retPak=new TFTPerror();
        break;
    }

    retPak.message=in.message;
    retPak.length=inPak.getLength();
    retPak.host=inPak.getAddress();
    retPak.port=inPak.getPort();

    return retPak;
  }

  public void send(InetAddress ip, DatagramSocket sock) throws IOException {
    sock.send(new DatagramPacket(message,length,ip,tftpPort));
  }

  public void send(InetAddress ip, int p, DatagramSocket s) throws IOException {
    s.send(new DatagramPacket(message,length,ip,p));
  }

  // DatagramPacket like methods

  public InetAddress getAddress() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public int getLength() {
    return length;
  }

  // Utility methods to manipulate the packets

  protected void put(int at, short value) {
    message[at++] = (byte)(value >>> 8);  // high before low
    message[at] = (byte)(value % 256);
  }

  protected void put(int at, String value, byte del) {
    value.getBytes(0, value.length(), message, at);
    message[at + value.length()] = del;
  }

  protected int get(int at) {
    return (message[at] & 0xff) << 8 | message[at+1] & 0xff;
  }

  protected String get (int at, byte del) {
    StringBuffer result = new StringBuffer();

    while (message[at] != del) result.append((char)message[at++]);

    return result.toString();
  }
}
