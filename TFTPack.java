import java.net.*;
import java.io.*;
import java.util.*;

public final class TFTPack extends TFTPpacket {

  // Ctors

  protected TFTPack() {}

  public TFTPack(int blockNumber) {
    length=4;
    this.message = new byte[length];

    put(opOffset,tftpACK);
    put(blkOffset,(short)blockNumber);
  }

  // Accessors

  public int blockNumber() {
    return this.get(blkOffset);
  }
}
