package indexing;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javazoom.jl.decoder.JavaLayerException;
import model.VoiceProcessor;

import org.apache.commons.lang.StringUtils;

/**
 * Servlet implementation class SearchController
 */
@WebServlet("/SearchController")
public class SearchController extends HttpServlet {
	//
	private static final long serialVersionUID = 1L;

	public void init() {
		// String term = request.getParameter("search");
		// Tf_Idf tfScore = new Tf_Idf(term);
		// tfScore.mongofrom();
		// //Tf_Idf.mongofrom(term);
		//
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		request.getRequestDispatcher("/WEB-INF/SearchView.jsp").forward(
				request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String term = request.getParameter("search");

		// VoiceProcessor voice = new VoiceProcessor();
		// try {
		// voice.playResponse("you said " + term);
		// } catch (JavaLayerException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// TODO Auto-generated method stub stopWords.add("a");

		if (term.equals("it") || term.equals("on") || term.equals("are")
				|| term.equalsIgnoreCase("is") || term.equalsIgnoreCase("wikipedia")
				|| term.equalsIgnoreCase("was") || term.equalsIgnoreCase("a") || term.equalsIgnoreCase("an")
				|| term.equalsIgnoreCase("and") || term.equalsIgnoreCase("the")
				|| term.equalsIgnoreCase("that")||term.equalsIgnoreCase("as") ||term.equalsIgnoreCase("by")) {
			request.setAttribute("result", "");
		} else {
			Tf_Idf tfScore = new Tf_Idf();
			Map<String, Double> sortedMapAsc = tfScore.mongofrom(term);

			request.setAttribute("result", sortedMapAsc);

		}
		request.getRequestDispatcher("/WEB-INF/SearchView.jsp").forward(
				request, response);

	}

}