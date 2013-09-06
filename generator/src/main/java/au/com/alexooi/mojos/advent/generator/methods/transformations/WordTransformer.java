/*
 *
 */
package au.com.alexooi.mojos.advent.generator.methods.transformations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class WordTransformer {
	private List<String> uncountables = new ArrayList<String>();
	private LinkedList<TransformationRule> singulars = new LinkedList<TransformationRule>();

	public WordTransformer() {
		this.uncountables.addAll(Arrays.asList("equipment", "information", "rice", "money", "species", "series",
				"fish", "sheep"));
		this.singulars.addFirst(new TransformationRule("s$", ""));
		this.singulars.addFirst(new TransformationRule("(s|si|u)s$", "$1s"));
		this.singulars.addFirst(new TransformationRule("(n)ews$", "$1ews"));
		this.singulars.addFirst(new TransformationRule("([ti])a$", "$1um"));
		this.singulars.addFirst(new TransformationRule("((a)naly|(b)a|(d)iagno|(p)arenthe|(p)rogno|(s)ynop|(t)he)ses$",
				"$1$2sis"));
		this.singulars.addFirst(new TransformationRule("(^analy)ses$", "$1sis"));
		this.singulars.addFirst(new TransformationRule("(^analy)sis$", "$1sis"));
		this.singulars.addFirst(new TransformationRule("([^f])ves$", "$1fe"));
		this.singulars.addFirst(new TransformationRule("(hive)s$", "$1"));
		this.singulars.addFirst(new TransformationRule("(tive)s$", "$1"));
		this.singulars.addFirst(new TransformationRule("([lr])ves$", "$1f"));
		this.singulars.addFirst(new TransformationRule("([^aeiouy]|qu)ies$", "$1y"));
		this.singulars.addFirst(new TransformationRule("(s)eries$", "$1eries"));
		this.singulars.addFirst(new TransformationRule("(m)ovies$", "$1ovie"));
		this.singulars.addFirst(new TransformationRule("(x|ch|ss|sh)es$", "$1"));
		this.singulars.addFirst(new TransformationRule("([m|l])ice$", "$1ouse"));
		this.singulars.addFirst(new TransformationRule("(bus)es$", "$1"));
		this.singulars.addFirst(new TransformationRule("(o)es$", "$1"));
		this.singulars.addFirst(new TransformationRule("(shoe)s$", "$1"));
		this.singulars.addFirst(new TransformationRule("(cris|ax|test)is$", "$1is"));
		this.singulars.addFirst(new TransformationRule("(cris|ax|test)es$", "$1is"));
		this.singulars.addFirst(new TransformationRule("(octop|vir)i$", "$1us"));
		this.singulars.addFirst(new TransformationRule("(octop|vir)us$", "$1us"));
		this.singulars.addFirst(new TransformationRule("(alias|status)es$", "$1"));
		this.singulars.addFirst(new TransformationRule("(alias|status)$", "$1"));
		this.singulars.addFirst(new TransformationRule("^(ox)en", "$1"));
		this.singulars.addFirst(new TransformationRule("(vert|ind)ices$", "$1ex"));
		this.singulars.addFirst(new TransformationRule("(matr)ices$", "$1ix"));
		this.singulars.addFirst(new TransformationRule("(quiz)zes$", "$1"));
		addIrregular("person", "people");
		addIrregular("man", "men");
		addIrregular("child", "children");
		addIrregular("sex", "sexes");
		addIrregular("move", "moves");
		addIrregular("stadium", "stadiums");
	}

	public void addIrregular(String singular, String plural) {
		// CheckArg.isNotEmpty(singular, "singular rule");
		// CheckArg.isNotEmpty(plural, "plural rule");
		String singularRemainder = singular.length() > 1 ? singular.substring(1) : "";
		String pluralRemainder = plural.length() > 1 ? plural.substring(1) : "";
		this.singulars.addFirst(new TransformationRule("(" + plural.charAt(0) + ")" + pluralRemainder + "$", "$1"
				+ singularRemainder));
	}

	/**
	 * Returns the singular form of the word in the string.
	 * <p/>
	 * Examples:
	 * <p/>
	 * 
	 * <pre>
	 *   inflector.singularize(&quot;posts&quot;)             #=&gt; &quot;post&quot;
	 *   inflector.singularize(&quot;octopi&quot;)            #=&gt; &quot;octopus&quot;
	 *   inflector.singularize(&quot;sheep&quot;)             #=&gt; &quot;sheep&quot;
	 *   inflector.singularize(&quot;words&quot;)             #=&gt; &quot;word&quot;
	 *   inflector.singularize(&quot;the blue mailmen&quot;)  #=&gt; &quot;the blue mailman&quot;
	 *   inflector.singularize(&quot;CamelOctopi&quot;)       #=&gt; &quot;CamelOctopus&quot;
	 * </pre>
	 * <p/>
	 * <p/>
	 * <p/>
	 * Note that if the {@link Object#toString()} is called on the supplied
	 * object, so this method works for non-strings, too.
	 * 
	 * @param word
	 *            the word that is to be pluralized.
	 * @return the pluralized form of the word, or the word itself if it could
	 *         not be pluralized
	 */
	public String singularize(String word) {
		if (word == null) {
			return null;
		}
		String wordStr = word.trim();
		if (wordStr.length() == 0) {
			return wordStr;
		}
		if (isUncountable(wordStr)) {
			return wordStr;
		}
		for (TransformationRule rule : this.singulars) {
			String result = rule.apply(wordStr);
			if (result != null) {
				return result;
			}
		}
		return wordStr;
	}

	/**
	 * Determine whether the supplied word is considered uncountable by the
	 * {@link #singularize(String) singularize} methods.
	 * 
	 * @param word
	 *            the word
	 * @return true if the plural and singular forms of the word are the same
	 */
	private boolean isUncountable(String word) {
		if (word == null) {
			return false;
		}
		String trimmedLower = word.trim().toLowerCase();
		return this.uncountables.contains(trimmedLower);
	}
}
