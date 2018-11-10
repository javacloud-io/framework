package javacloud.framework.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import javacloud.framework.util.Objects;


/**
 * Provide basic stuff, rich enough to talk to specific OS.
 * 
 * @author aimee
 *
 */
public final class Files {
	private Files() {
	}
	
	/**
	 * Make folder and make sure all the child are create too.
	 * @param folder
	 * @param childs
	 */
	public static void makeFolder(File folder, String...childs) {
		if(childs == null || childs.length == 0) {
			folder.mkdirs();
		} else {
			for(String child: childs) {
				new File(folder, child).mkdirs();
			}
		}
	}
	
	/**
	 * Need to empty out the FOLDER if it exist.
	 * @param folder
	 */
	public static void scrubFolder(File folder) {
		if(!folder.exists()) {
			folder.mkdirs();
		} else {
			//DELETE ALL BUT THE ROOT ONE!
			File[] childs = listFiles(folder);
			for(File child: childs) {
				deleteFolder(child);
			}
		}
	}
	
	/**
	 * Delete the file or recursive folder if it exist.
	 * @param file
	 */
	public static void deleteFolder(File folder) {
		//LOOP IF A DIRECTORY & DELETE THE SUBFOLDER
		if(folder.isDirectory()) {
			File[] childs = listFiles(folder);
			for(File child: childs) {
				deleteFolder(child);
			}
		}
		
		//DELETE THE FOLDER IF DOESN'T HAVE CHILD
		folder.delete();
	}
	
	/**
	 * Make sure always give back EMPTY FILE if NOT FOUND!
	 * @param foder
	 * @return
	 */
	public static File[] listFiles(File folder) {
		File[] childs = folder.listFiles();
		return (childs == null? new File[0]: childs);
	}
	
	/**
	 * 
	 * @param zipFile
	 * @param folder
	 * @throws ZipException
	 * @throws IOException
	 */
	public static void unzipFile(File zipFile, File folder) throws ZipException, IOException {
		ZipInputStream zipStream = new ZipInputStream(new FileInputStream(zipFile));
		try {
			unzipFile(zipStream, folder, 8192);
		}finally {
			Objects.closeQuietly(zipStream);
		}
	}
	
	/**
	 * Unzip all the entry to FOLDER. Use chunk size to control the BUFFER.
	 * 
	 * @param zipStream
	 * @param folder
	 * @param chunk
	 * @throws ZipException
	 * @throws IOException
	 */
	public static void unzipFile(ZipInputStream zipStream, File folder, int chunk) throws ZipException, IOException {
		byte buffer[] = new byte[chunk];
		ZipEntry ze = null;
		while((ze = zipStream.getNextEntry()) != null) {
			File zf = new File(folder, ze.getName());
			
			//CREATE DIRECTORY
			if(ze.isDirectory()) {
				Files.makeFolder(zf);
			} else { //COPY ALL DATA To NEW FILE.
				FileOutputStream fos = new FileOutputStream(zf);
				int count;
				while((count = zipStream.read(buffer, 0, buffer.length)) != -1) {
					fos.write(buffer, 0, count);
				}
				//CLOSE OUT STREAM
				fos.close();
			}
			
			//CLOSE ZIP ENTRY
			zipStream.closeEntry();
		}
	}
	
	/**
	 * Copy stream data from 
	 * 
	 * @param src
	 * @param dest
	 * @param chunk
	 * 
	 * @return
	 * @throws IOException
	 */
	public static long copyFile(InputStream src, OutputStream dest, int chunk)
		throws IOException {
		byte[] buf = new byte[chunk];
		long size = 0;
		while(true) {
			int read = src.read(buf);
			//EOF
			if(read < 0) {
				break;
			} else if(read > 0){
				dest.write(buf, 0, read);
				size += read;
			}
		}
		return size;
	}
	
	/**
	 * Copy file from source to destination
	 * 
	 * @param src
	 * @param dest
	 * @param chunk
	 * 
	 * @return
	 * @throws IOException
	 */
	public static long copyFile(File src, File dest, int chunk) throws IOException {
		FileInputStream fis = new FileInputStream(src);
		FileOutputStream fos= new FileOutputStream(dest);
		try {
			return	copyFile(fis, fos, chunk);
		} finally {
			Objects.closeQuietly(fis, fos);
		}
	}
}
