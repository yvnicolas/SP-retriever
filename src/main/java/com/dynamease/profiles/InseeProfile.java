package com.dynamease.profiles;

import com.dynamease.entities.PersonBasic;

public class InseeProfile extends PersonBasic{
	
	int totalInhab;
	int sup65to79Inhab;
	int sup65to79pct;
	int sup80Inhab;
	int sup80pct;

	public InseeProfile() {
		
	}

	public int getTotalInhab() {
		return totalInhab;
	}

	public void setTotalInhab(int totalInhab) {
		this.totalInhab = totalInhab;
	}

	public int getSup65to79Inhab() {
		return sup65to79Inhab;
	}

	public void setSup65to79Inhab(int sup65to79Inhab) {
		this.sup65to79Inhab = sup65to79Inhab;
	}

	public int getSup65to79pct() {
		return sup65to79pct;
	}

	public void setSup65to79pct(int sup65to79pct) {
		this.sup65to79pct = sup65to79pct;
	}

	public int getSup80Inhab() {
		return sup80Inhab;
	}

	public void setSup80Inhab(int sup80Inhab) {
		this.sup80Inhab = sup80Inhab;
	}

	public int getSup80pct() {
		return sup80pct;
	}

	public void setSup80pct(int sup80pct) {
		this.sup80pct = sup80pct;
	}

}
