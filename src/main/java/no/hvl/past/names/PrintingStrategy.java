package no.hvl.past.names;

import org.xml.sax.ext.DefaultHandler2;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;

/**
 * The purpose of the names-library is to enable unique identification of graph elements.
 * This identification is intended to be independent of a concrete String representation.
 * However to interact with the outside world, one often needs a way to transform a name into its String
 * representation, which may not be unique.
 */
public interface PrintingStrategy {



    public class DefaultPrintingStrategy implements PrintingStrategy {

        private final boolean ignorePrefix;
        private final boolean putParanthesis;
        private final boolean ignoreDecorators;
        private String prefixDelimiter = "/"; // like unix paths
        private String sequentialCompositionSymbol = "."; // like OO notation
        private String coproductSymbol = "⊔"; // default
        private String typedBySymbol = "::"; // like in haskell
        private String emptySymbol = ""; // like "nothing to show"
        private String inverseSymbol = "^-1"; // default
        private String iteratedSymbol = "*"; // default
        private String extendsSymbol = "<:"; // like in Scala Generics
        private String pairLeftParan = "("; // default
        private String pairSeparator = ","; // default
        private String pairRightParan = ")"; // default
        private String elementOfSymbol = "@";
        private String applicationArgumentLeftParan = "<";
        private String applicationArgumentRightParan = ">";
        private String pullbackSymbol = "PB";
        private String productSymbol = " x ";
        private String complementSymbol = "~";
        private String optionalSymbol = "?";
        private String mandatorySymbol = "!";
        private String stringDelimiter = "\"";
        private String trueSymbol = "true";
        private String falseSymbol = "false";
        private String absoluteSymbol = "|";


        public DefaultPrintingStrategy(boolean ignorePrefix, boolean putParenthesis, boolean ignoreDecorators) {
            this.ignorePrefix = ignorePrefix;
            this.putParanthesis = putParenthesis;
            this.ignoreDecorators = ignoreDecorators;
        }

        @Override
        public String identifier(Identifier id) {
            return id.toString();
        }

        @Override
        public String variable(Variable var) {
            return var.getVariableName();
        }

        @Override
        public String stringValue(String value) {
            return stringDelimiter + value + stringDelimiter;
        }

        @Override
        public String integerValue(BigInteger value) {
            return value.toString();
        }

        @Override
        public String floatValue(double value) {
            return BigDecimal.valueOf(value).toEngineeringString();
        }

        @Override
        public String trueValue() {
            return trueSymbol;
        }

        @Override
        public String falseValue() {
            return falseSymbol;
        }

        @Override
        public String handlePrefix(String prefix, String prefixed) {
            if (ignorePrefix) {
                return prefixed;
            } else {
                return prefix + prefixDelimiter + prefixed;
            }
        }

        public String empty() {
            return emptySymbol;
        }

        public String sequentialComposition(String fst, String snd) {
            return fst + sequentialCompositionSymbol + snd;
        }

        public String coproduct(String fst, String snd) {
            return (putParanthesis ? pairLeftParan : "") + fst + coproductSymbol + snd + (putParanthesis ? pairRightParan : "");
        }

        public String pullback(String applicant, String target) {
            return pullbackSymbol + pairLeftParan + applicant + pairSeparator + target + pairRightParan;
        }

        public String merge(Collection<String> transformedNames) {
            if (transformedNames.isEmpty()) {
                return empty();
            }
            StringBuilder result = new StringBuilder();
            result.append(coproductSymbol);
            if (putParanthesis) {
                result.append(pairLeftParan);
            }
            Iterator<String> iterator = transformedNames.iterator();
            while (iterator.hasNext()) {
                result.append(iterator.next());
                if (iterator.hasNext()) {
                    result.append(pairSeparator);
                }
            }
            if (putParanthesis) {
                result.append(pairRightParan);
            }
            return result.toString();
        }

        public String typedBy(String element, String type) {
            return element + typedBySymbol + type;
        }

        public String inverse(String element) {
            if (ignoreDecorators) {
                return element;
            }
            return element + inverseSymbol;
        }

        public String iterated(String element) {

            if (ignoreDecorators) {
                return element;
            }
            return element + iteratedSymbol;
        }

        @Override
        public String global(String nested) {
            if (ignoreDecorators) {
                return nested;
            }
            return nested + "+";
        }

        public String absolute(String element) {
            if (ignoreDecorators) {
                return element;
            }
            return absoluteSymbol + element + absoluteSymbol;
        }

        @Override
        public String copied(String nested) {
            if (ignoreDecorators) {
                return nested;
            }
            return nested + "'";
        }

        @Override
        public String substituted(String first, String second) {
            return first + "|" + second;
        }

        public String extens(String firstTransformed, String secondTransformed) {
            return firstTransformed + extendsSymbol + secondTransformed;
        }

        public String appliedTo(String firstTransformed, String secondTransformed) {
            return firstTransformed + applicationArgumentLeftParan + secondTransformed + applicationArgumentRightParan;
        }

        public String pair(String first, String second) {
            return pairLeftParan + first + pairSeparator + second + pairRightParan;
        }

        @Override
        public String downType(String first, String second) {
            return first;
        }

        @Override
        public String projection(String first, String second) {
            return "p_" + second + "(" + first + ")";
        }

        public String injection(String first, String second) {
            return "in_" + second + "(" + first + ")";
        }

        @Override
        public String preimage(String first, String second) {
            return first + "^-1(" + second + ")";
        }


        public String elementOf(String first, String second) {
            return first + elementOfSymbol + second;
        }

        public String times(String first, String second) {
            return (putParanthesis ? pairLeftParan : "") + first + productSymbol + second + (putParanthesis ? pairRightParan : "");
        }

        public String childOf(String child, String parent) {
            return parent + "|_" + child;
        }


        @Override
        public String augmentedWith(String first, String second) {
            return first + "+" + second;
        }

        ;

        public  String complement(String nested) {
            if (ignoreDecorators) {
                return nested;
            }
            return nested + complementSymbol;
        }

        public String optional(String nested) {
            if (ignoreDecorators) {
                return nested;
            }
            return nested + optionalSymbol;
        }

        public String mandatory(String nested) {
            if (ignoreDecorators) {
                return nested;
            }
            return nested + mandatorySymbol;
        }

        public void setPrefixDelimiter(String prefixDelimiter) {
            this.prefixDelimiter = prefixDelimiter;
        }

        public void setSequentialCompositionSymbol(String sequentialCompositionSymbol) {
            this.sequentialCompositionSymbol = sequentialCompositionSymbol;
        }

        public void setCoproductSymbol(String coproductSymbol) {
            this.coproductSymbol = coproductSymbol;
        }

        public void setTypedBySymbol(String typedBySymbol) {
            this.typedBySymbol = typedBySymbol;
        }

        public void setEmptySymbol(String emptySymbol) {
            this.emptySymbol = emptySymbol;
        }

        public void setInverseSymbol(String inverseSymbol) {
            this.inverseSymbol = inverseSymbol;
        }

        public void setIteratedSymbol(String iteratedSymbol) {
            this.iteratedSymbol = iteratedSymbol;
        }

        public void setExtendsSymbol(String extendsSymbol) {
            this.extendsSymbol = extendsSymbol;
        }

        public void setPairLeftParan(String pairLeftParan) {
            this.pairLeftParan = pairLeftParan;
        }

        public void setPairSeparator(String pairSeparator) {
            this.pairSeparator = pairSeparator;
        }

        public void setPairRightParan(String pairRightParan) {
            this.pairRightParan = pairRightParan;
        }

        public void setElementOfSymbol(String elementOfSymbol) {
            this.elementOfSymbol = elementOfSymbol;
        }

        public void setApplicationArgumentLeftParan(String applicationArgumentLeftParan) {
            this.applicationArgumentLeftParan = applicationArgumentLeftParan;
        }

        public void setApplicationArgumentRightParan(String applicationArgumentRightParan) {
            this.applicationArgumentRightParan = applicationArgumentRightParan;
        }

        public void setPullbackSymbol(String pullbackSymbol) {
            this.pullbackSymbol = pullbackSymbol;
        }

        public void setProductSymbol(String productSymbol) {
            this.productSymbol = productSymbol;
        }

        public void setComplementSymbol(String complementSymbol) {
            this.complementSymbol = complementSymbol;
        }

        public void setOptionalSymbol(String optionalSymbol) {
            this.optionalSymbol = optionalSymbol;
        }

        public void setMandatorySymbol(String mandatorySymbol) {
            this.mandatorySymbol = mandatorySymbol;
        }

        public void setStringDelimiter(String stringDelimiter) {
            this.stringDelimiter = stringDelimiter;
        }

        public void setTrueSymbol(String trueSymbol) {
            this.trueSymbol = trueSymbol;
        }

        public void setFalseSymbol(String falseSymbol) {
            this.falseSymbol = falseSymbol;
        }

        @Override
        public String indexed(String print, long index) {
            return print + "_" + index;
        }
    }

    class LatexPrintingStrategy extends DefaultPrintingStrategy {

        private final boolean mathematicalOrder;


        public LatexPrintingStrategy(boolean ignorePrefix, boolean putParanthesis, boolean mathematicalOrder) {
            super(ignorePrefix, putParanthesis, false);
            this.mathematicalOrder = mathematicalOrder;
            if (mathematicalOrder) {
                this.setSequentialCompositionSymbol("\\circ");
            } else {
                this.setSequentialCompositionSymbol("\\fatsemi");
            }
            this.setPrefixDelimiter(".");
            this.setCoproductSymbol("\\sqcup");
            this.setProductSymbol("\\times");
            this.setElementOfSymbol("\\in");
            this.setEmptySymbol("\\emptyset");
            this.setTypedBySymbol(":");
            this.setApplicationArgumentLeftParan("\\langle");
            this.setApplicationArgumentRightParan("\\rangle");
            this.setTrueSymbol("\\top");
            this.setTrueSymbol("\\bot");
            this.setExtendsSymbol("\\subseteq");
        }

        @Override
        public String sequentialComposition(String fst, String snd) {
            if (mathematicalOrder) {
                return super.sequentialComposition(snd, fst);
            } else {
                return super.sequentialComposition(fst, snd);
            }
        }

        @Override
        public String iterated(String element) {
            return "{" + element + "}^*";
        }

        @Override
        public String inverse(String element) {
            return "{" + element + "}^{-1}";
        }

        @Override
        public String mandatory(String nested) {
            return "{" + nested + "}^{+}";
        }

        @Override
        public String optional(String nested) {
            return "{" + nested + "}^{?}";
        }
    }

    PrintingStrategy IGNORE_PREFIX = new DefaultPrintingStrategy(true, false, false);
    PrintingStrategy DETAILED = new DefaultPrintingStrategy(false, true, false);

    String identifier(Identifier id);

    String variable(Variable var);

    String stringValue(String value);

    String integerValue(BigInteger value);

    String handlePrefix(String prefix, String prefixed);

    String floatValue(double value);

    String trueValue();

    String falseValue();

    String empty();

     String sequentialComposition(String fst, String snd);

     String coproduct(String fst, String snd);

     String pullback(String applicant, String target);

     String merge(Collection<String> transformedNames);

     String typedBy(String element, String type);

     String inverse(String element);

     String iterated(String element);

    String global(String nested);

    String extens(String firstTransformed, String secondTransformed);

      String appliedTo(String firstTransformed, String secondTransformed);

     String pair(String first, String second);

    String downType(String first, String second);

    String projection(String first, String second);

    String injection(String first, String second);

    String preimage(String first, String second);

    String elementOf(String first, String second);

     String times(String first, String second);

    String augmentedWith(String first, String second);

    String childOf(String child, String parent);

    String complement(String nested);

    String optional(String nested);

    String mandatory(String nested);

    String indexed(String print, long index);

    String absolute(String nested);

    String copied(String nested);

    String substituted(String first, String second);


}
