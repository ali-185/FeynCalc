package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import feynman.Diagram;
import standardModel.Particle;

/**
 * Creates and forwards to Diagrams.jsp, a page of Feynman Diagrams. 
 * @author Alastair Crowe
 */
public class DiagramsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/**
	 * A java object representation of the JSON request parameter.
	 */
	static class DiagramRequest {
		List<String> incomingElectrons;
		List<String> incomingPositrons;
		List<String> incomingPhotons;
		List<String> outgoingElectrons;
		List<String> outgoingPositrons;
		List<String> outgoingPhotons;
		List<String> interactions;
	}
	/**
	 * @return List of "key-value" Strings
	 */
	private List<String> map2list(Map<String, String> map) {
		List<String> list = new ArrayList<String>(map.size());
		for (Map.Entry<String, String> entry : map.entrySet()) {
		    String key = entry.getKey();
		    String value = entry.getValue();
		    list.add(key + "-" + value);
		}
		return list;
	}
	/**
	 * @return
	 * A Diagram from a JSON representation.
	 */
	private Diagram parseJson(String json) {
		Gson gson = new Gson();
		DiagramRequest diagReq = gson.fromJson(json, DiagramRequest.class);
		return new Diagram(diagReq.incomingElectrons, diagReq.incomingPositrons, 
	  					   diagReq.incomingPhotons,   diagReq.outgoingElectrons, 
		  				   diagReq.outgoingPositrons, diagReq.outgoingPhotons,
						   diagReq.interactions);
	}
	/**
	 * @return
	 * A JSON ConnectionBean of the diagram.
	 */
	private ConnectionBean toConnections(Diagram diagram) {
		Gson gson = new Gson();
		ConnectionBean connections = new ConnectionBean();
		String electronConnections = gson.toJson(map2list(diagram.getConnections(Particle.ELECTRON)));
		String positronConnections = gson.toJson(Arrays.asList());
		String photonConnections   = gson.toJson(map2list(diagram.getConnections(Particle.PHOTON)));
		connections.setElectronConnections(electronConnections);
		connections.setPositronConnections(positronConnections);
		connections.setPhotonConnections(photonConnections);
		return connections;
	}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * @param request
	 * required http request parameters:
	 * request.uid - a unique identifier.
	 * request.data - JSON incomplete diagram object, @see DiagramRequest
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		// Create/Retrieve the iterator
		String uid  = request.getParameter("uid");
		HttpSession session = request.getSession();
		@SuppressWarnings("unchecked")
		Iterator<Diagram> itr = (Iterator<Diagram>) session.getAttribute(uid);
		int index;
		if(itr == null) {
			String json = request.getParameter("data");
			Diagram diagram = parseJson(json);
			itr = diagram.getSubDiagrams(true);
			session.setAttribute(uid, itr);
			index = 0;
		} else {
			index = Integer.parseInt(request.getParameter("index")) * 9;
		}
		// Calculate more diagrams
		ArrayList<ConnectionBean> diagrams = new ArrayList<ConnectionBean>(9);
		for(int i = 0; i < 9 && itr.hasNext(); i++) {
			Diagram subDiagram = itr.next();
			diagrams.add(toConnections(subDiagram));
		}
		request.setAttribute("index", index);
		request.setAttribute("diagramList", diagrams);
		request.setAttribute("more", itr.hasNext());
		// Forward to the JSP page
		RequestDispatcher RequetsDispatcherObj = 
				request.getRequestDispatcher("/WEB-INF/Diagrams.jsp");
		RequetsDispatcherObj.forward(request, response);
	}
}
