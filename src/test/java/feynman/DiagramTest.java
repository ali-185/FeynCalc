package feynman;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import standardModel.Particle;

@SuppressWarnings("javadoc")
public class DiagramTest {
	private Diagram diagram;
	
	@Before
	public void setUp() {
		List<String> incomingElectrons = Arrays.asList("i1");
		List<String> incomingPositrons = Arrays.asList("i2");
		List<String> incomingPhotons   = Arrays.asList();
		List<String> outgoingElectrons = Arrays.asList();
		List<String> outgoingPositrons = Arrays.asList();
		List<String> outgoingPhotons   = Arrays.asList("o1");
		List<String> vertices          = Arrays.asList("v1", "v2", "v3");
        diagram = new Diagram(incomingElectrons, incomingPositrons, incomingPhotons,
                                      outgoingElectrons, outgoingPositrons, outgoingPhotons,
                                      vertices);	}

	@Test
	public void testGetSubDiagrams() {
		class Connections {
			HashMap<String, String> electronConnections;
			HashMap<String, String> photonConnections;
			Connections(Diagram diagram) {
				electronConnections = diagram.getConnections(Particle.ELECTRON);
				photonConnections   = diagram.getConnections(Particle.PHOTON);
			}
			public String toString() {
				return "Electrons:" + electronConnections + ", Photons:" + photonConnections;
			}
		}
		Iterator<Diagram> itr = diagram.getSubDiagrams(true);
		ArrayList<String> connections = new ArrayList<String>();
		while(itr.hasNext()) {
			Diagram subdiagram = itr.next();
			connections.add(new Connections(subdiagram).toString());
		}
		
		String[] result = {"Electrons:{i1=v2, v1=i2, v2=v3, v3=v1}, Photons:{o1=v1, v2=v3}",
						   "Electrons:{i1=v1, v1=v2, v2=v3, v3=i2}, Photons:{o1=v1, v2=v3}",
						   "Electrons:{i1=v1, v1=v3, v2=i2, v3=v2}, Photons:{o1=v1, v2=v3}",
						   "Electrons:{i1=v2, v1=v3, v2=i2, v3=v1}, Photons:{o1=v1, v2=v3}",
						   "Electrons:{i1=v2, v1=v3, v2=v1, v3=i2}, Photons:{o1=v1, v2=v3}",
						   "Electrons:{i1=v3, v1=i2, v2=v1, v3=v2}, Photons:{o1=v1, v2=v3}",
						   "Electrons:{i1=v3, v1=v2, v2=i2, v3=v1}, Photons:{o1=v1, v2=v3}",
						   "Electrons:{i1=v3, v1=v2, v2=v1, v3=i2}, Photons:{o1=v1, v2=v3}",
						   "Electrons:{i1=v1, v1=i2, v2=v3, v3=v2}, Photons:{o1=v2, v1=v3}",
						   "Electrons:{i1=v2, v1=i2, v2=v3, v3=v1}, Photons:{o1=v2, v1=v3}",
						   "Electrons:{i1=v1, v1=v2, v2=v3, v3=i2}, Photons:{o1=v2, v1=v3}",
						   "Electrons:{i1=v1, v1=v3, v2=i2, v3=v2}, Photons:{o1=v2, v1=v3}",
						   "Electrons:{i1=v2, v1=v3, v2=v1, v3=i2}, Photons:{o1=v2, v1=v3}",
						   "Electrons:{i1=v3, v1=i2, v2=v1, v3=v2}, Photons:{o1=v2, v1=v3}",
						   "Electrons:{i1=v3, v1=v2, v2=i2, v3=v1}, Photons:{o1=v2, v1=v3}",
						   "Electrons:{i1=v3, v1=v2, v2=v1, v3=i2}, Photons:{o1=v2, v1=v3}",
						   "Electrons:{i1=v1, v1=i2, v2=v3, v3=v2}, Photons:{o1=v3, v1=v2}",
						   "Electrons:{i1=v2, v1=i2, v2=v3, v3=v1}, Photons:{o1=v3, v1=v2}",
						   "Electrons:{i1=v1, v1=v2, v2=v3, v3=i2}, Photons:{o1=v3, v1=v2}",
						   "Electrons:{i1=v1, v1=v3, v2=i2, v3=v2}, Photons:{o1=v3, v1=v2}",
						   "Electrons:{i1=v2, v1=v3, v2=i2, v3=v1}, Photons:{o1=v3, v1=v2}",
						   "Electrons:{i1=v2, v1=v3, v2=v1, v3=i2}, Photons:{o1=v3, v1=v2}",
						   "Electrons:{i1=v3, v1=i2, v2=v1, v3=v2}, Photons:{o1=v3, v1=v2}",
						   "Electrons:{i1=v3, v1=v2, v2=i2, v3=v1}, Photons:{o1=v3, v1=v2}"};
		assertEquals(24, connections.size());
		connections.removeAll(Arrays.asList(result));
		assertEquals(0, connections.size());
	}
}
