import java.net.*;
import java.io.*;
import java.util.*;

public final class TFTPwrite extends TFTPpacket {

  private void initialize(String filename, String reqType) {
    length=2+filename.length()+1+reqType.length()+1;
    message = new byte[length];

    put(opOffset,tftpWRQ);
    put(fileOffset,filename,(byte)0);
    put(fileOffset+filename.length()+1,reqType,(byte)0);
  }

  // Constructors

  protected TFTPwrite() {}

  public TFTPwrite(String filename) { 
    initialize(filename,"netascii");
  }

  public TFTPwrite(String filename, String reqType) {
    initialize(filename,reqType);
  }

  // Accessors

  public String fileName() {
    return this.get(fileOffset,(byte)0);
  }

  public String requestType() {
    String fname = fileName();
    return this.get(fileOffset+fname.length()+1,(byte)0);
  }
}
