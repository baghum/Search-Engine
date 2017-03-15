package finalProject;

import indexing.Tf_Idf;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
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
@WebServlet("/SearchProject")
public class SearchProject extends HttpServlet {
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

		VoiceProcessor voice = new VoiceProcessor();
		try {
			voice.playResponse("State the term you're looking for.");
		} catch (JavaLayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("State the term you're looking for.");

		LocalTime time1 = LocalTime.now();
		Duration fiveSeconds = Duration.ofSeconds(5);

		voice.captureAudio();
		System.out.println("Capturing audio");

		while (LocalTime.now().isBefore(time1.plus(fiveSeconds))) {
		}

		voice.stopCapture();
		System.out.println("Finished audio capture");

		String term = voice.getVoiceResult();

		// Tell the user what he/she said
		try {
			System.out.println("You said " + voice.getVoiceResult()
					+ ".Is this correct?Please say " + "yes or no.");

			voice.playResponse("You said " + voice.getVoiceResult()
					+ ".Is this correct?Please say " + "yes or no.");

			LocalTime time = LocalTime.now();
			Duration fiveSecondsAgain = Duration.ofSeconds(5);

			voice.captureAudio();
			System.out.println("Capturing audio");

			while (LocalTime.now().isBefore(time.plus(fiveSecondsAgain))) {
			}

			voice.stopCapture();

			if (voice.getVoiceResult().equalsIgnoreCase("no")) {
				voice.playResponse("Ok.Let's try again.");
				doGet(request, response);
			} else
				voice.playResponse("Ok.Searching results.");

		} catch (JavaLayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		request.setAttribute("term", term);
		// System.out.println("The term is " + term);

		request.getRequestDispatcher("/WEB-INF/SearchViewProject.jsp").forward(
				request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String term = request.getParameter("search");

		VoiceProcessor voice = new VoiceProcessor();
		try {
			voice.playResponse("you said " + term);
		} catch (JavaLayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO Auto-generated method stub
		if (term.equals("it") || term.equals("on") || term.equals("are")
				|| term.equalsIgnoreCase("is")
				|| term.equalsIgnoreCase("wikipedia")
				|| term.equalsIgnoreCase("was") || term.equalsIgnoreCase("a")
				|| term.equalsIgnoreCase("an") || term.equalsIgnoreCase("and")
				|| term.equalsIgnoreCase("the")
				|| term.equalsIgnoreCase("that") || term.equalsIgnoreCase("as")
				|| term.equalsIgnoreCase("by")) {
			request.setAttribute("result", "");
		} else {
			Tf_Idf tfScore = new Tf_Idf();
			Map<String, Double> sortedMapAsc = tfScore.mongofrom(term);

			request.setAttribute("result", sortedMapAsc);

		}

		request.getRequestDispatcher("/WEB-INF/SearchViewProject.jsp").forward(
				request, response);

	}

}