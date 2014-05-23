package com.serveme.saveme;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class GetConfirmationCode extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8908139000724631781L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		String email = req.getParameter("email");

		resp.setContentType("text/html; charset=utf-8");

		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		if (email != null && !"".equalsIgnoreCase(email)) {

			DatastoreService datastore = DatastoreServiceFactory
					.getDatastoreService();

			try {
				String generatedPass = new SessionIdentifierGenerator()
						.nextSessionId();
				Entity entity = new Entity("EmailVerfication");
				entity.setProperty("code", generatedPass);
				entity.setProperty("email", email);

				datastore.put(entity);
				if (!isExist(email, datastore)) {
					entity = new Entity("EmailList");
					entity.setProperty("email", email);
					datastore.put(entity);
				}
				Message msg = new MimeMessage(session);
				msg.setFrom(new InternetAddress(
						"saveme-verfication@appspot.gserviceaccount.com",
						"ServeMe"));
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
						email, "Mr. User"));
				msg.setSubject("Verfication Code");
				String msgBody = "Hi,\n\nThis is your temporary password: "
						+ generatedPass
						+ "\n\nNote: You can use it to unlock Save Me while you didn't closed the recovery screen\nIn case you have closed the recovery screen you need to open it again and wait until new temporary password sent to you!\n"
						+ "After that you can make a new pattern and continue to use your apps\n\nRegards\nServe Me Team\nhttps://play.google.com/store/apps/developer?id=ServeMe";
				msg.setText(msgBody);
				Transport.send(msg);

				resp.getWriter().print("Email sent to "+email);
			} catch (AddressException e) {
				resp.getWriter().print("Problem occured serveme.ps@gmail.com");
				Entity entity = new Entity("Error");
				entity.setProperty("exception", e.getMessage());
				entity.setProperty("email", email);
				datastore.put(entity);
				throw new RuntimeException(e);
			} catch (MessagingException e) {
				resp.getWriter().print("Problem occured contact serveme.ps@gmail.com");
				Entity entity = new Entity("Error");
				entity.setProperty("exception", e.getMessage());
				entity.setProperty("email", email);
				datastore.put(entity);
				throw new RuntimeException(e);
			}
		}
	}

	public final class SessionIdentifierGenerator {
		private SecureRandom random = new SecureRandom();

		public String nextSessionId() {
			return new BigInteger(60, random).toString(32);
		}
	}

	public boolean isExist(String email, DatastoreService datastore) {
		Query query = new Query("EmailList");
		Filter emailFilter = new FilterPredicate("email", FilterOperator.EQUAL,
				email);
		query = query.setFilter(emailFilter);
		PreparedQuery preparedQuery = datastore.prepare(query);
		List<Entity> entities = preparedQuery.asList(FetchOptions.Builder
				.withLimit(1000));
		return !entities.isEmpty();
	}

}
