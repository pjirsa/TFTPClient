import java.net.*;
import java.io.*;
import java.util.*;

public final class TFTPdata extends TFTPpacket {

  // Constructors

  protected TFTPdata() {}

  public TFTPdata(int blockNumber, FileInputStream in) throws IOException {	this.message=new byte[maxTftpPakLen];

    this.put(opOffset,tftpDATA);
    this.put(blkOffset,(short)blockNumber);
    length=in.read(message,dataOffset,maxTftpData)+4;
  }

  // Accessors

  public int blockNumber() {
    return this.get(blkOffset);
  }

  public void data(byte[] buffer) {
    buffer = new byte[length-4];

    for (int i=0; i<length-4; i++) buffer[i]=message[i+dataOffset];
  }

  // Direct File IO

  public int write(FileOutputStream out) throws IOException {
    out.write(message,dataOffset,length-4);

    return(length-4);
  }
}
