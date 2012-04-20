package com.byto.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.io.FileUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;

class SequenceVO {
	private String seq;
	
	public SequenceVO(String seq) {
		this.seq = seq;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}
}

public class Sequence {
	
	public static String seq() throws IOException  {
		XStream seqStream = new XStream();
		File seqFile = BytoUtils.getFileFromProps("SEQUENCE");
		seqStream.alias("sequences", SequenceVO.class);
		SequenceVO seqVO = null;

		try {
			seqVO = (SequenceVO) seqStream.fromXML(seqFile);
		} catch(StreamException ex) {
			String data = seqStream.toXML(new SequenceVO("0"));
			VOHelper.writeStringToFile(seqFile, data);
			return "0";
		}

		Class<? extends SequenceVO> seqClass = seqVO.getClass();
		
		String currentSeq = seqVO.getSeq();
		String nextSeq = String.valueOf(new Integer(currentSeq) + 1);
		
		seqVO.setSeq(nextSeq);
		
		String data = seqStream.toXML(seqVO);
		VOHelper.writeStringToFile(seqFile, data);

		return seqVO.getSeq();
	}
}
