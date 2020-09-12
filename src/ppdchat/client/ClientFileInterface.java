package ppdchat.client;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.rmi.*;
import java.rmi.registry.*;

/**
 *
 * @author Matheus
 */
public interface ClientFileInterface extends Remote {
    public void uploadFileToServer(byte[] mybyte, String serverpath, int length) throws RemoteException;
}
