/**
 * 
 */
package com.dynamease.serviceproviders;

import java.util.List;

/**
 * Class meant to encapsulate several views of the same lists of result fetching.
 * @author Yves Nicolas
 *
 */
public class SPConnectionMatchesResults {

	
	private List <? extends Object> nameMatches;
	
	private List <? extends Object> veryLikelyMatches;
	
	/**
	 * 
	 */
	public SPConnectionMatchesResults() {
		
	}
	
	public SPConnectionMatchesResults(List<? extends Object> nameMatches, List<? extends Object> veryLikelyMatches) {
	    super();
	    this.nameMatches = nameMatches;
	    this.veryLikelyMatches = veryLikelyMatches;
    }

	public List<? extends Object> getNameMatches() {
		return nameMatches;
	}
	
	public List<? extends Object> getVeryLikelyMatches (){
		return veryLikelyMatches;
	}
	
		
	public void setNameMatches(List<? extends Object> nameMatches) {
		this.nameMatches = nameMatches;
	}

	public void setVeryLikelyMatches(List<? extends Object> veryLikelyMatches) {
		this.veryLikelyMatches = veryLikelyMatches;
	}

	public int nameMatchesCount() {
		if (nameMatches== null)
			return 0;
		else
			return nameMatches.size();
	}

	public int veryLikelyMatchesCount() {
		if (veryLikelyMatches== null)
			return 0;
		else
			return veryLikelyMatches.size();
	}
	
}
