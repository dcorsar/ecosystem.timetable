/**
 * 
 */
package uk.ac.dotrural.irp.ecosystem.timetable;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Extracts the contents of a zip file
 * 
 * @author David Corsar
 * 
 */
public class ZipExtractor {

	/**
	 * Unzips the contents of zipFile
	 * 
	 * @param zipFile
	 * @param destination
	 *            Files will be unzipped here
	 * @return
	 * @throws IOException
	 */
	public boolean unzip(String zipFile, String destination) throws IOException {
		// The following is modified from
		// http://www.devx.com/getHelpOn/10MinuteSolution/20447
		try {
			ZipFile zipF = new ZipFile(zipFile);
			Enumeration<? extends ZipEntry> zipEntries = zipF.entries();

			while (zipEntries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) zipEntries.nextElement();

				if (entry.isDirectory()) {
					// Assume directories are stored parents first then
					// children.
					// This is not robust, just for demonstration
					// purposes.
					(new File(destination + entry.getName())).mkdir();
					continue;
				}
				copyInputStream(
						zipF.getInputStream(entry),
						new BufferedOutputStream(new FileOutputStream(destination + entry
								.getName())));
			}
			zipF.close();
		} catch (IOException ioe) {
			System.err.println("Unhandled exception:");
			ioe.printStackTrace();
			return false;
		}
		return true;
	}

	public static final void copyInputStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int len;

		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);

		in.close();
		out.close();
	}

}
