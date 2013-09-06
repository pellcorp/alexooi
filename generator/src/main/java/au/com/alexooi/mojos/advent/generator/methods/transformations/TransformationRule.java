/*
 *
 */
package au.com.alexooi.mojos.advent.generator.methods.transformations;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class TransformationRule {

	protected final String expression;
	protected final Pattern expressionPattern;
	protected final String replacement;

	TransformationRule(String expression, String replacement) {
		this.expression = expression;
		this.replacement = replacement != null ? replacement : "";
		this.expressionPattern = Pattern.compile(this.expression, Pattern.CASE_INSENSITIVE);
	}

	/**
	 * Apply the rule against the input string, returning the modified string or
	 * null if the rule didn't apply (and no modifications were made)
	 * 
	 * @param input
	 *            the input string
	 * @return the modified string if this rule applied, or null if the input
	 *         was not modified by this rule
	 */
	protected String apply(String input) {
		Matcher matcher = this.expressionPattern.matcher(input);
		if (!matcher.find()) {
			return null;
		}
		return matcher.replaceAll(this.replacement);
	}

	@Override
	public int hashCode() {
		return expression.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj != null && obj.getClass() == this.getClass()) {
			final TransformationRule that = (TransformationRule) obj;
			if (this.expression.equalsIgnoreCase(that.expression)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return expression + ", " + replacement;
	}
}
