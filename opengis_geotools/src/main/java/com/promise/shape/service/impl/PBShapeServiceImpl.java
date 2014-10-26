/** @文件名: PBShapeServiceImpl.java @创建人：邢健  @创建日期： 2013-2-28 下午3:20:15 */
package com.promise.shape.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import org.geotools.data.shapefile.dbf.DbaseFileHeader;
import org.geotools.data.shapefile.dbf.DbaseFileReader;
import com.promise.shape.service.PBShapeService;

/**
 * @类名: PBShapeServiceImpl.java
 * @包名: com.promise.shape.service.impl
 * @描述: shape数据的操作
 * @作者: xingjian xingjian@yeah.net
 * @日期:2013-2-28 下午3:20:15
 * @版本: V1.0
 */
public class PBShapeServiceImpl implements PBShapeService {

	/**
	 * 读取shape数据
	 */
	@Override
	public <T> List<T> readShapeFile(File file, Class cla) {
		return null;
	}

	/**
	 * 读取shape数据
	 */
	@Override
	@SuppressWarnings("resource")
	public Map<String, String> readShapeFile(String fileURL, String charset) {
		try {
			FileChannel in = new FileInputStream(fileURL).getChannel();
			DbaseFileReader dr = new DbaseFileReader(in, true,Charset.forName(charset));
			DbaseFileHeader dh = dr.getHeader();
			int fields = dh.getNumFields();
			for (int i = 0; i < fields; i++) {
				System.out.print(dh.getFieldName(i) + " ");// 打印当前属性名
			}
			System.out.print("\n");
			while (dr.hasNext()) {
				DbaseFileReader.Row row = dr.readRow();
				for (int i = 0; i < fields; i++) {
					Object data = row.read(i);
					if (dh.getFieldName(i).equals("NAME")){
						System.out.print(data);
					} else {
						System.out.print(data);
					}
					System.out.print("\t");
				}
				System.out.println();
			}
			dr.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void writeShapeFile(String fileURL, String charset) {
		
	}
}
