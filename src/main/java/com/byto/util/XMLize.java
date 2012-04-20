package com.byto.util;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.byto.vo.EntryContainer;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.StreamException;

public class XMLize extends XStream {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	public XMLize(Class entryClass) {
		super();
		alias("xml", EntryContainer.class);
		aliasField(entryClass.getSimpleName().toLowerCase()+"s", EntryContainer.class, "entries"); //수정
		alias(entryClass.getSimpleName().toLowerCase(), entryClass);
		useAttributeFor(entryClass, "seq");
	}

	@Override
	public Object fromXML(File file) {
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		EntryContainer ec = null;
		try {
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis, "MS949"); //한글처리
			br = new BufferedReader(isr);
			ec = (EntryContainer) super.fromXML(br);
		} catch(Exception ex) {
			return new EntryContainer(); //xml 파일이 없을경우 신규 생성
		} finally {
			try {
				if(fis != null) fis.close();
				if(isr != null) isr.close(); 
				if(br != null) br.close(); 
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ec; 
	}
}