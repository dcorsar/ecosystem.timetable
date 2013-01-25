package uk.ac.dotrural.irp.ecosystem.timetable;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
//	private static final String BUNDLE_NAME = "uk.ac.dotrural.irp.ecosystem.timetable.messages"; //$NON-NLS-1$
	private static final String BUNDLE_NAME = "timetableMessages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
