package de.unima.ki.pmmc.evaluator.validation;


import de.unima.ki.pmmc.evaluator.data.Solution;
import de.unima.ki.pmmc.evaluator.data.ValidationReport;

public class SizeValidator implements Validator {


	@Override
	public ValidationReport validate(Solution goldstandard, Solution matcher) {
		ValidationReport report = new ValidationReport();
		try {
			boolean test = matcher.getAlignments().size() == goldstandard.getAlignments().size();
			if(!test) {
				report.addInfo("Alignments have unequal size. Matcher : " 
						+ matcher.getAlignments().size() + " Goldstandard: " + goldstandard.getAlignments().size());
			}
		} catch(Exception e) {
			report.addError(e);
		}
		return report;
	}

}
