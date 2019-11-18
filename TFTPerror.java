import java.net.*;
import java.io.*;
import java.util.*;

public final class TFTPerror extends TFTPpacket {

  // Ctors

  protected TFTPerror() {}

  public TFTPerror(int number, String message) {
    length=4+message.length()+1;
    this.message = new byte[length];

    put(opOffset,tftpERROR);
    put(numOffset,(short)number);
    put(msgOffset,message,(byte)0);
  }

  // Accessors

  public int number() {
    return this.get(numOffset);
  }

  public String message() {
    return this.get(msgOffset,(byte)0);
  }
}
