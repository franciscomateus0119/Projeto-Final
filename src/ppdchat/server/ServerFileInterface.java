
package ppdchat.server;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.rmi.*;
import java.rmi.registry.*;

/**
 *
 * @author Matheus
 */
public interface ServerFileInterface extends Remote{
    public void uploadFileToServer(byte[] mybyte, String serverpath, int length) throws RemoteException;
}
