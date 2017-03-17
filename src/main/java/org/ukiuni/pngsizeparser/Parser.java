/**
PNG Size Parser

Copyright (c) 2017 ukiuni

This software is released under the MIT License.
http://opensource.org/licenses/mit-license.php
*/
package org.ukiuni.pngsizeparser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * PNGデータからサイズを取得します。 フォーマットは http://www.setsuki.com/hsp/ext/png.htm を参照しました。
 * 
 * @author ukiuni
 *
 */
public class Parser {
	public static byte[] DATA_PNG_FILE_SIGNATURE = new byte[] { (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47,
			(byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A };
	public static int LENGTH_PNG_FILE_SIGNATURE = DATA_PNG_FILE_SIGNATURE.length;
	public static int LENGTH_IHDR = 25;
	public static byte[] DATA_IHDR_CUNK_TYPE = new byte[] { (byte) 'I', (byte) 'H', (byte) 'D', (byte) 'R', };

	public static Size parse(String path) throws NotPNGException, FileNotFoundException, IOException {
		try (FileInputStream in = new FileInputStream(path)) {
			byte[] data = new byte[LENGTH_PNG_FILE_SIGNATURE + LENGTH_IHDR];
			int readed = in.read(data);
			if ((LENGTH_PNG_FILE_SIGNATURE + LENGTH_IHDR) != readed) {
				throw new NotPNGException("PNG size have to larger than " + (LENGTH_PNG_FILE_SIGNATURE + LENGTH_IHDR));
			}
			return parse(data);
		}
	}

	public static Size parse(byte[] data) throws NotPNGException {
		if ((LENGTH_PNG_FILE_SIGNATURE + LENGTH_IHDR) > data.length) {
			throw new NotPNGException("PNG size have to larger than " + (LENGTH_PNG_FILE_SIGNATURE + LENGTH_IHDR));
		}
		if (!Arrays.equals(DATA_PNG_FILE_SIGNATURE, Arrays.copyOf(data, LENGTH_PNG_FILE_SIGNATURE))) {
			throw new NotPNGException("File signature is not PNG");
		}
		byte[] ihdrChunk = Arrays.copyOfRange(data, LENGTH_PNG_FILE_SIGNATURE, LENGTH_IHDR);
		if (((byte) 13) != ihdrChunk[3]) {
			throw new NotPNGException("IHDR chunk size must be 13 but was " + ihdrChunk[3]);
		}
		if (!Arrays.equals(DATA_IHDR_CUNK_TYPE, Arrays.copyOfRange(ihdrChunk, 4, 4 + DATA_IHDR_CUNK_TYPE.length))) {
			throw new NotPNGException("Chunk IHDR not found");
		}
		return new Size(ByteBuffer.wrap(Arrays.copyOfRange(ihdrChunk, 8, 12)).getInt(),
				ByteBuffer.wrap(Arrays.copyOfRange(ihdrChunk, 12, 16)).getInt());
	}

	public static class Size {
		public Size(int width, int height) {
			this.width = width;
			this.height = height;
		}

		public final int width;
		public final int height;
	}

	@SuppressWarnings("serial")
	public static class NotPNGException extends Exception {
		public NotPNGException(String message) {
			super(message);
		}
	}

	private Parser() {
	}
}
