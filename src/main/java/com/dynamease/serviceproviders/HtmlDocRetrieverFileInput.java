package com.dynamease.serviceproviders;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HtmlDocRetrieverFileInput implements HtmlDocRetriever {

	public HtmlDocRetrieverFileInput() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Document fetch(String path) {
		File input = new File(path);
		try {
			return Jsoup.parse(input, "UTF-8");
		} catch (IOException e) {
						e.printStackTrace();
		}
		return null;
	}

}
