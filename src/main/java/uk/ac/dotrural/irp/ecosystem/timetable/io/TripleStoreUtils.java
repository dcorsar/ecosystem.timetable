package uk.ac.dotrural.irp.ecosystem.timetable.io;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.update.UpdateAction;
import com.hp.hpl.jena.update.UpdateRequest;

public class TripleStoreUtils {

	private static TripleStoreUtils utils = new TripleStoreUtils();
	private Map<String, Model> models;

	private TripleStoreUtils() {
		super();
		models = new HashMap<String, Model>();
	}

	public static TripleStoreUtils getTripleStoreUtils() {
		return TripleStoreUtils.utils;
	}

	public boolean createModel(String name) {
		if (!(this.models.containsKey(name))) {
			Model m = ModelFactory.createDefaultModel();
			this.models.put(name, m);
			return true;
		}
		return false;
	}

	public void performUpdates(String modelName, Collection<String> updates) {
		Model model = getModel(modelName);

		UpdateRequest ur = new UpdateRequest();
		for (String update : updates) {
			ur.add(update);
		}
		UpdateAction.execute(ur, model);
	}

//	public ResultSet performSelect(String modelName, String query) {
//		Model model = getModel(modelName);
//
//		QueryExecution q = QueryExecutionFactory.create(query, model);
//		ResultSet rs = q.execSelect();
//		return rs;
//	}

	private Model getModel(String modelName) {
		Model model = models.get(modelName);
		if (model == null) {
			throw new IllegalArgumentException("No model with name "
					+ modelName + " exists");
		}
		return model;
	}

	public boolean toTdbModel(String modelName, String path) {
		Model model = getModel(modelName);

		Model tdbModel = TDBFactory.createModel(path);
		tdbModel.add(model);
		tdbModel.close();

		return true;
	}
}
