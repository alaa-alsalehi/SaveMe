package com.serveme.saveme;

import java.io.IOException;
import java.util.List;
//import org.apache.commons.lang.StringEscapeUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;

//import org.apache.commons.lang

public class VerifyConfirmationCode extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6268993192022832161L;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		String email = req.getParameter("email");
		String code = req.getParameter("code");
		resp.setContentType("text/html; ");
		resp.setCharacterEncoding("UTF-8");
		if (email != null && !"".equalsIgnoreCase(email) && code != null
				&& !"".equalsIgnoreCase(code)) {
			DatastoreService datastore = DatastoreServiceFactory
					.getDatastoreService();
			List<Entity> entities = verifyConfirmationCode(email, code,
					datastore);

			if (entities.size() != 0) {
				deleteConfirmationCodeForEmail(email, datastore);
				resp.getWriter().print("success");
			} else {
				resp.getWriter().print("fail");
			}
		} else {
			resp.getWriter().print("fail");
		}
	}

	public List<Entity> verifyConfirmationCode(String email, String code,
			DatastoreService datastore) {
		Query query = new Query("EmailVerfication");
		Filter emailFilter = new FilterPredicate("email", FilterOperator.EQUAL,
				email);
		Filter codeFilter = new FilterPredicate("code", FilterOperator.EQUAL,
				code);
		Filter emailCodeFilter = CompositeFilterOperator.and(emailFilter,
				codeFilter);
		query = query.setFilter(emailCodeFilter);
		PreparedQuery preparedQuery = datastore.prepare(query);
		List<Entity> entities = preparedQuery.asList(FetchOptions.Builder
				.withLimit(1000));

		return entities;
	}

	public void deleteConfirmationCodeForEmail(String email,
			DatastoreService datastore) {
		Query query = new Query("EmailVerfication");
		Filter emailFilter = new FilterPredicate("email", FilterOperator.EQUAL,
				email);
		query = query.setFilter(emailFilter);
		PreparedQuery preparedQuery = datastore.prepare(query);
		List<Entity> entities = preparedQuery.asList(FetchOptions.Builder.withDefaults());
		for (Entity entity : entities) {
			datastore.delete(entity.getKey());
		}
	}
}
