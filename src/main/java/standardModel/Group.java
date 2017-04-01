package standardModel;

/**
 * Standard model groups type.
 * @see <a href="https://en.wikipedia.org/wiki/Standard_Model#/media/File:Standard_Model_of_Elementary_Particles.svg">The Standard Model</a>
 * @author Alastair Crowe
 */
@SuppressWarnings("javadoc")
public enum Group {
	QUARK(Spin.FERMION), LEPTON(Spin.FERMION), GUAGE_BOSON(Spin.BOSON);
	/**
	 * Spin type.
	 */
	public Spin spin;
	Group(Spin spin) {
		this.spin = spin;
	}
}
