package org.ukiuni.pngsizeparser;

import static org.ukiuni.pngsizeparser.Parser.DATA_IHDR_CUNK_TYPE;
import static org.ukiuni.pngsizeparser.Parser.DATA_PNG_FILE_SIGNATURE;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateSeveralPngData {
	private static final byte[] BYTES_INT4 = ByteBuffer.allocate(4).putInt(4).array();
	private static final byte[] BYTES_INT10 = ByteBuffer.allocate(4).putInt(10).array();
	private static final byte[] BYTES_INT13 = ByteBuffer.allocate(4).putInt(13).array();
	private static final byte[] BYTES_INT0 = ByteBuffer.allocate(4).putInt(4).array();

	public static void main(String[] args) throws FileNotFoundException, IOException {
		dump("src/test/resources/ZeroByte.png", new byte[0]);
		dump("src/test/resources/onlySignature.png", DATA_PNG_FILE_SIGNATURE);
		dump("src/test/resources/onlySignatureAndIHDR_NoSize.png", DATA_PNG_FILE_SIGNATURE, BYTES_INT0, DATA_IHDR_CUNK_TYPE);
		dump("src/test/resources/onlySignatureAndIHDR_NoSizeButIHDRLlengthSets13.png", DATA_PNG_FILE_SIGNATURE, BYTES_INT13, DATA_IHDR_CUNK_TYPE);
		dump("src/test/resources/noIHDRafterSignature_IDAT.png", DATA_PNG_FILE_SIGNATURE, BYTES_INT10, new byte[] { (byte) 'I', (byte) 'D', (byte) 'A', (byte) 'T' }, new byte[6]);
		dump("src/test/resources/noIHDRafterSignature_IEND.png", DATA_PNG_FILE_SIGNATURE, BYTES_INT0, new byte[] { (byte) 'I', (byte) 'E', (byte) 'N', (byte) 'D' });
		List<Chunk> chunkList = divideToChank(new FileInputStream("src/test/resources/mac_capture.png"));
		for (Chunk chunk : chunkList) {
			System.out.println("chunk " + chunk.typeName());
		}
		int index = 1;
		dump("src/test/resources/IHDRAfteriDOT.png", DATA_PNG_FILE_SIGNATURE, chunkList.get(index++).toBytes(), chunkList.get(index++).toBytes(), chunkList.get(index++).toBytes(), chunkList.get(0).toBytes(), chunkList.get(index++).toBytes(), chunkList.get(index++).toBytes(), chunkList.get(index++).toBytes(), chunkList.get(index++).toBytes(), chunkList.get(index++).toBytes(), chunkList.get(index++).toBytes());
		index = 0;
		dump("src/test/resources/org.png", DATA_PNG_FILE_SIGNATURE, chunkList.get(index++).toBytes(), chunkList.get(index++).toBytes(), chunkList.get(index++).toBytes(), chunkList.get(index++).toBytes(), chunkList.get(index++).toBytes(), chunkList.get(index++).toBytes(), chunkList.get(index++).toBytes(), chunkList.get(index++).toBytes(), chunkList.get(index++).toBytes(), chunkList.get(index++).toBytes());
	}

	public static void dump(String path, byte[]... datas) throws FileNotFoundException, IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		for (byte[] data : datas) {
			bout.write(data);
		}
		try (FileOutputStream out = new FileOutputStream(path)) {
			out.write(bout.toByteArray());
		}
	}

	public static List<Chunk> divideToChank(InputStream in) throws IOException {
		List<Chunk> result = new ArrayList<Chunk>();
		byte[] signature = new byte[DATA_PNG_FILE_SIGNATURE.length];
		int readed = in.read(signature);
		if (signature.length != readed) {
			throw new IOException();
		}
		while (true) {
			Chunk chunk = new Chunk();
			byte[] sizeBytes = new byte[4];
			readed = in.read(sizeBytes);
			if (0 > readed) {
				break;
			}
			chunk.size = sizeBytes;

			byte[] typeBytes = new byte[4];
			readed = in.read(typeBytes);
			if (typeBytes.length != readed) {
				throw new IOException();
			}
			chunk.type = typeBytes;
			int dataSize = ByteBuffer.wrap(Arrays.copyOfRange(sizeBytes, 0, 4)).getInt();

			byte[] data = new byte[dataSize];
			readed = in.read(data);
			if (data.length != readed) {
				throw new IOException();
			}
			chunk.data = data;

			byte[] crc = new byte[4];
			readed = in.read(crc);
			if (crc.length != readed) {
				throw new IOException();
			}
			chunk.crc = crc;

			result.add(chunk);
		}
		return result;
	}

	public static class Chunk {
		public byte[] size;
		public byte[] type;
		public byte[] data;
		public byte[] crc;

		public byte[] toBytes() throws IOException {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			bout.write(size);
			bout.write(type);
			bout.write(data);
			bout.write(crc);
			return bout.toByteArray();
		}

		public String typeName() {
			return new String(type);
		}
	}
}
