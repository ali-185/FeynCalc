package server;

import java.io.Serializable;

/**
 * A JSON list for each particle connection.
 * @author Alastair Crowe
 */
@SuppressWarnings("javadoc")
public class ConnectionBean implements Serializable{
	private static final long serialVersionUID = 1L;
	private String electronConnections;
	private String positronConnections;
	private String photonConnections;
	public String getElectronConnections() {
		return electronConnections;
	}
	public void setElectronConnections(String electronConnections) {
		this.electronConnections = electronConnections;
	}
	public String getPositronConnections() {
		return positronConnections;
	}
	public void setPositronConnections(String positronConnections) {
		this.positronConnections = positronConnections;
	}
	public String getPhotonConnections() {
		return photonConnections;
	}
	public void setPhotonConnections(String photonConnections) {
		this.photonConnections = photonConnections;
	}
}
