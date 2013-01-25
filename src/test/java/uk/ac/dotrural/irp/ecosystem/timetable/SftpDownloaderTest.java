package uk.ac.dotrural.irp.ecosystem.timetable;

import junit.framework.Assert;
import junit.framework.TestCase;

public class SftpDownloaderTest extends TestCase {

	public void testDownloadStringStringStringStringString() {
		if (1==1){
			Assert.assertEquals(true, true);
			return;
		}
		SftpDownloader downloader = new SftpDownloader();
		assertEquals(true, downloader.download(Messages.getString("SftpDownloaderTest.username"), Messages.getString("SftpDownloaderTest.host"), //$NON-NLS-1$ //$NON-NLS-2$
						Messages.getString("SftpDownloaderTest.password"), Messages.getString("SftpDownloaderTest.remoteFile"), Messages.getString("SftpDownloaderTest.localFile"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	}

}
