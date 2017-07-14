package de.unima.ki.pmmc.evaluator.metrics;

public enum Metrics {

	PRECISION_MICRO("prec-mic"),
	PRECISION_MACRO("pre-mac"),
	PRECISION_STD("pre-std"),
	NB_PRECISION_MICRO("nb-prec-mic"),
	NB_PRECISION_MACRO("nb-prec-mac"),
	NB_PRECISION_STD("nb-prec-std"),
	RECALL_MICRO("rec-mic"),
	RECALL_MACRO("rec-mac"),
	RECALL_STD("rec-std"),
	NB_RECALL_MICRO("nb-rec-mic"),
	NB_RECALL_MACRO("nb-rec-mac"),
	NB_RECALL_STD("nb-rec-std"),
	FMEASURE_MICRO("fm-mic"),
	FMEASURE_MACRO("fm-mac"),
	FMEASURE_STD("fm-std"),
	NB_FMEASURE_MICRO("nb-fm-mic"),
	NB_FMEASURE_MACRO("nb-fm-mac"),
	NB_FMEASURE_STD("nb-fm-std"),
	ACCURACY("acc"),
	SPEAR_RANG_CORR("spear-rang"),
	REALTIVE_DIST("rel-dist"),
	//Multiple goldstandards metrics
	NB_PRECISION_MICRO_GS("nb-prec-gs-mic"),
	NB_PRECISION_MACRO_GS("nb-prec-gs-mac"),
	NB_PRECISION_STD_GS("nb-prec"),
	NB_RECALL_MICRO_GS("nb-rec-gs-mic"),
	NB_RECALL_MACRO_GS("nb-rec-gs-mac"),
	NB_RECALL_STD_GS("nb-rec-gs-std"),
	NB_FMEASURE_MICRO_GS("nb-fm-gs-mic"),
	NB_FMEASURE_MACRO_GS("nb-fm-gs-mac"),
	NB_FMEASURE_STD_GS("nb-fm-gs-std"),
	ALL("all");
	
	private String name;
	
	Metrics(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
