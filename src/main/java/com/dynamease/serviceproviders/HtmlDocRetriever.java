/**
 * 
 */
package com.dynamease.serviceproviders;

import org.jsoup.nodes.Document;

/**
 * An interface to be used by web information retriever
 * purpose is to allow easy change between file html and real web access inputs for test solutions. 
 * @author yves
 *
 */
public interface HtmlDocRetriever {
	
	/**
	 * In direct web connections, result would be equivalent to Jsoup.connect(url).get();
	 * @param url
	 * @return
	 */
	public Document fetch(String url);

}
