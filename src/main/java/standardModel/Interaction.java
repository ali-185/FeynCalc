package standardModel;

/**
 * A standard model interation.
 * @see <a href="https://en.wikipedia.org/wiki/Standard_Model#/media/File:Standard_Model_Feynman_Diagram_Vertices.png">The Standard Model Interactions</a>
 * @author Alastair Crowe
 */
@SuppressWarnings("javadoc")
public enum Interaction {
	// TODO Other interactions
	ELECTROMAGNETIC(Particle.PHOTON, Particle.ELECTRON, Particle.ANTI_ELECTRON);
	public final Particle[] particles;
	private Interaction(Particle... particles) {
		this.particles = particles;
	}
}
