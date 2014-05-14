package cn.dayutianfei.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

/**
 * 把一个文件转化为byte[]数据,然后把字节写入一个新文件里面
 * 
 * @author spring sky <br>
 *         Email:vipa1888@163.com <br>
 *         QQ:840950105
 * 
 */
public class FileToByte {
	protected final static Logger LOG = Logger.getLogger(FileToByte.class);

	public static void main(String[] args) throws Exception {
		File file = new File("D:/temp/test-2.txt");
		byte[] b = getByte(file);
		/***
		 * 打印出字节 每一行10个字节
		 */
		for (int i = 0; i < b.length; i++) {
			System.out.print(b[i]);
			if (i % 10 == 0 && i != 0) {
				System.out.print("\n");
			}
		}
		/**
		 * 把得到的字节写到一个新的文件里面
		 */
		File newnewFile = new File("D:/temp/test-1.txt");
		OutputStream os = new FileOutputStream(newnewFile);
		os.write(b); // 把流一次性写入一个文件里面
		os.flush();
		os.close();

	}

	/**
	 * 把一个文件转化为字节,并删除该文件
	 * 
	 * @param file
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] getByte(File file) throws IOException {
		byte[] bytes = null;
		if (file != null) {
			InputStream is = new FileInputStream(file);
			int length = (int) file.length();
			if (length > Integer.MAX_VALUE) // 当文件的长度超过了int的最大值
			{
				LOG.error("this file is max ");
				is.close();
				return null;
			}
			bytes = new byte[length];
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
			// 如果得到的字节长度和file实际的长度不一致就可能出错了
			if (offset < bytes.length) {
				LOG.error("file length is error");
				is.close();
				bytes = new byte[0];
				return null;
			}
			is.close();
		}

		// file.deleteOnExit();
		return bytes;
	}

	/**
	 * 把一个文件转化为字节,并删除该文件
	 * 
	 * @param file
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] getByteAndDel(File file) throws IOException {
		byte[] bytes = null;
		if (file != null) {
			InputStream is = new FileInputStream(file);
			int length = (int) file.length();
			if (length > Integer.MAX_VALUE) // 当文件的长度超过了int的最大值
			{
				LOG.error("this file is max ");
				is.close();
				return null;
			}
			bytes = new byte[length];
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length
					&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}
			// 如果得到的字节长度和file实际的长度不一致就可能出错了
			if (offset < bytes.length) {
				LOG.error("file length is error");
				is.close();
				bytes = new byte[0];
				return null;
			}
			is.close();
		}

		file.deleteOnExit();
		return bytes;
	}

	public static File tofile(String tempDir, byte[] data) throws IOException {
		File newnewFile = new File(tempDir);
		OutputStream os = new FileOutputStream(newnewFile);
		os.write(data); // 把流一次性写入一个文件里面
		os.flush();
		os.close();
		return newnewFile;
	}
}
