package de.unima.ki.pmmc.evaluator.validation;


import de.unima.ki.pmmc.evaluator.data.Solution;
import de.unima.ki.pmmc.evaluator.data.ValidationReport;

@FunctionalInterface
public interface Validator {

	
	/**
	 * Validates an complete alignment collection against the goldstandard
	 * @param alignment to validate
	 * @return ValidationReport including validation information
	 */
	public ValidationReport validate(Solution goldstandard, Solution matcher);
}
