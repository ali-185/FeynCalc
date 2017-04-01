package standardModel;

/**
 * Standard model particle types.
 * @see <a href="https://en.wikipedia.org/wiki/Standard_Model#/media/File:Standard_Model_of_Elementary_Particles.svg">The Standard Model</a>
 * @author Alastair Crowe
 */
@SuppressWarnings("javadoc")
public enum Particle {
	// Quarks
	UP(false, Group.QUARK),
	DOWN(false, Group.QUARK),
	CHARM(false, Group.QUARK), 
	STRANGE(false, Group.QUARK), 
	TOP(false, Group.QUARK),
	BOTTOM(false, Group.QUARK), 
	// Leptons
	ELECTRON(false, Group.LEPTON),
	MUON(false, Group.LEPTON),
	TAU(false, Group.LEPTON),
	// Lepton neutrinos
	ELECTRON_NEUTRINO(false, Group.LEPTON),
	MUON_NEUTRINO(false, Group.LEPTON),
	TAU_NEUTRINO(false, Group.LEPTON),
	// Guage bosons
	GLUON(false, Group.GUAGE_BOSON),
	PHOTON(false, Group.GUAGE_BOSON),
	Z_BOSON(false, Group.GUAGE_BOSON),
	W_BOSON(false, Group.GUAGE_BOSON),
	// Anti quarks
	ANTI_UP(true, Group.QUARK),
	ANTI_DOWN(true, Group.QUARK),
	ANTI_CHARM(true, Group.QUARK),
	ANTI_STRANGE(true, Group.QUARK),
	ANTI_TOP(true, Group.QUARK),
	ANTI_BOTTOM(true, Group.QUARK),
	// Anti leptons
	ANTI_ELECTRON(true, Group.LEPTON),
	ANTI_MUON(true, Group.LEPTON),
	ANTI_TAU(true, Group.LEPTON),
	// Anti neutrinos
	ANTI_ELECTRON_NEUTRINO(true, Group.LEPTON),
	ANTI_MUON_NEUTRINO(true, Group.LEPTON),
	ANTI_TAU_NEUTRINO(true, Group.LEPTON),
	// Anti gauge bosons
	ANTI_W_BOSON(true, Group.GUAGE_BOSON);
	/**
	 * Group type.
	 */
	public final Group group;
	private boolean anti;
	private Particle(boolean anti, Group group) {
		this.anti = anti;
		this.group = group;
	}
	/**
	 * @param particle Particle of which to find the anti-particle.
	 * @return The anti-particle of the specified particle.
	 */
	public static Particle getAntiParticle(Particle particle) {
		switch (particle) {
			case UP: return ANTI_UP;
			case CHARM: return ANTI_CHARM;
			case TOP: return ANTI_TOP;
			case GLUON: return GLUON;
			case DOWN: return ANTI_DOWN;
			case STRANGE: return ANTI_STRANGE;
			case BOTTOM: return ANTI_BOTTOM;
			case PHOTON: return PHOTON;
			case ELECTRON: return ANTI_ELECTRON;
			case MUON: return ANTI_MUON;
			case TAU: return ANTI_TAU;
			case Z_BOSON: return Z_BOSON;
			case ELECTRON_NEUTRINO: return ANTI_ELECTRON_NEUTRINO;
			case MUON_NEUTRINO: return ANTI_MUON_NEUTRINO;
			case TAU_NEUTRINO: return ANTI_TAU_NEUTRINO;
			case W_BOSON: return ANTI_W_BOSON;
			case ANTI_UP: return UP;
			case ANTI_CHARM: return CHARM;
			case ANTI_TOP: return TOP;
			case ANTI_DOWN: return DOWN;
			case ANTI_STRANGE: return STRANGE;
			case ANTI_BOTTOM: return BOTTOM;
			case ANTI_ELECTRON: return ELECTRON;
			case ANTI_MUON: return MUON;
			case ANTI_TAU: return TAU;
			case ANTI_ELECTRON_NEUTRINO: return ELECTRON_NEUTRINO;
			case ANTI_MUON_NEUTRINO: return MUON_NEUTRINO;
			case ANTI_TAU_NEUTRINO: return TAU_NEUTRINO;
			case ANTI_W_BOSON: return W_BOSON;
		}
		throw new IllegalStateException("Unreachable statement"); 
	}
	/**
	 * @param partcile Particle of which to test is an anti-particle.
	 * @return true if this particle is an anti-particle. Particles
	 * that are their own particles return false.
	 */
	public static boolean isAnti(Particle partcile) {
		return partcile.anti;
	}
}
