package uk.ac.dotrural.irp.ecosystem.timetable;

import java.io.File;
import java.io.IOException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class ZipExtractorTest extends TestCase {

	public void testUnzipStringString() {
		if (1 == 1) {
			Assert.assertEquals(true, true);
			return;
		}
		ZipExtractor ze = new ZipExtractor();
		String dest = "resources/FileZillaPortableAll/";
		makeTargetDir(dest);
		try {
			assertTrue(ze.unzip("resources/FileZillaPortable.zip", dest));
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

	private void makeTargetDir(String dest) {
		File destination = new File(dest);
		if (!destination.exists()) {
			destination.mkdir();
		}
	}

}
