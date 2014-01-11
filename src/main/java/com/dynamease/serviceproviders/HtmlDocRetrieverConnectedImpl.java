package com.dynamease.serviceproviders;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class HtmlDocRetrieverConnectedImpl implements HtmlDocRetriever {

	public HtmlDocRetrieverConnectedImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Document fetch(String url) {
		
		try {
			return Jsoup.connect(url).get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
