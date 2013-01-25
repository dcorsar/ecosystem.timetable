package uk.ac.dotrural.irp.ecosystem.timetable;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * Downloads a file via SFTP
 * 
 * @author David Corsar
 *
 */
public class SftpDownloader {

	public static final int PORT_DEFAULT = 22;

	public boolean download(String username, String host, String password, String remoteFile, String localFile){
		return download(username, host, PORT_DEFAULT, password, remoteFile, localFile);
	}

	public boolean download(String username, String host, int port,
			String password, String remoteFile, String localFile) {

		JSch jsch = new JSch();
		Session session = null;
		boolean success = true;
		try {
			session = jsch.getSession(username, host, port);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(password);
			session.connect();

			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftpChannel = (ChannelSftp) channel;
			sftpChannel.get(remoteFile, localFile);
			sftpChannel.exit();
			session.disconnect();
		} catch (JSchException e) {
			success = false;
			e.printStackTrace(); 
		} catch (SftpException e) {
			success = false;
			e.printStackTrace();
		}
		return success;
	}

}
