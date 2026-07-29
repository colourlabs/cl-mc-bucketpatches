package net.colourlabs.bucketpatches.patches.imageonmap;

public class PluralEval {
    private final String input;
    private int pos;

    public PluralEval(String input) {
        this.input = input;
        this.pos = 0;
    }

    public static Integer evaluate(String script, int n) {
        String expr = script.replaceAll("\\bn\\b", Integer.toString(n));
        PluralEval parser = new PluralEval(expr);
        Object result = parser.parseExpression();
        if (result instanceof Boolean) return (Boolean) result ? 1 : 0;
        if (result instanceof Number) return ((Number) result).intValue();
        return Integer.valueOf(result.toString());
    }

    private Object parseExpression() {
        return parseTernary();
    }

    private Object parseTernary() {
        Object cond = parseLogical();
        skipWhitespace();
        if (pos < input.length() && input.charAt(pos) == '?') {
            pos++;
            Object trueVal = parseExpression();
            skipWhitespace();
            expect(':');
            Object falseVal = parseExpression();
            return bool(cond) ? trueVal : falseVal;
        }
        return cond;
    }

    private Object parseLogical() {
        Object left = parseComparison();
        while (true) {
            skipWhitespace();
            if (match("&&")) {
                Object right = parseComparison();
                left = bool(left) && bool(right);
            } else if (match("||")) {
                Object right = parseComparison();
                left = bool(left) || bool(right);
            } else {
                break;
            }
        }
        return left;
    }

    private Object parseComparison() {
        Object left = parseAdditive();
        while (true) {
            skipWhitespace();
            if (match("==")) {
                left = toNum(left) == toNum(parseAdditive());
            } else if (match("!=")) {
                left = toNum(left) != toNum(parseAdditive());
            } else if (match(">=")) {
                left = toNum(left) >= toNum(parseAdditive());
            } else if (match("<=")) {
                left = toNum(left) <= toNum(parseAdditive());
            } else if (match(">")) {
                left = toNum(left) > toNum(parseAdditive());
            } else if (match("<")) {
                left = toNum(left) < toNum(parseAdditive());
            } else {
                break;
            }
        }
        return left;
    }

    private Object parseAdditive() {
        Object left = parseMultiplicative();
        while (true) {
            skipWhitespace();
            if (match("+")) {
                left = toNum(left) + toNum(parseMultiplicative());
            } else if (match("-")) {
                left = toNum(left) - toNum(parseMultiplicative());
            } else {
                break;
            }
        }
        return left;
    }

    private Object parseMultiplicative() {
        Object left = parseUnary();
        while (true) {
            skipWhitespace();
            if (match("*")) {
                left = toNum(left) * toNum(parseUnary());
            } else if (match("/")) {
                left = toNum(left) / toNum(parseUnary());
            } else if (match("%")) {
                left = toNum(left) % toNum(parseUnary());
            } else {
                break;
            }
        }
        return left;
    }

    private Object parseUnary() {
        skipWhitespace();
        if (match("!")) {
            return !bool(parseUnary());
        }
        if (match("-")) {
            return -toNum(parseUnary());
        }
        return parsePrimary();
    }

    private Object parsePrimary() {
        skipWhitespace();
        if (pos < input.length() && input.charAt(pos) == '(') {
            pos++;
            Object val = parseExpression();
            skipWhitespace();
            expect(')');
            return val;
        }
        if (pos < input.length() && (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.')) {
            return parseNumber();
        }
        if (pos < input.length() && Character.isJavaIdentifierStart(input.charAt(pos))) {
            return parseIdentifier();
        }
        return 0;
    }

    private Number parseNumber() {
        int start = pos;
        while (pos < input.length() && (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.')) pos++;
        String numStr = input.substring(start, pos);
        if (numStr.contains(".")) return Double.parseDouble(numStr);
        return Integer.parseInt(numStr);
    }

    private String parseIdentifier() {
        int start = pos;
        while (pos < input.length() && Character.isJavaIdentifierPart(input.charAt(pos))) pos++;
        return input.substring(start, pos);
    }

    private void skipWhitespace() {
        while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) pos++;
    }

    private boolean match(String s) {
        if (input.startsWith(s, pos)) {
            pos += s.length();
            return true;
        }
        return false;
    }

    private void expect(char c) {
        skipWhitespace();
        if (pos >= input.length() || input.charAt(pos) != c)
            throw new RuntimeException("Expected '" + c + "' at position " + pos);
        pos++;
    }

    private static boolean bool(Object o) {
        if (o instanceof Boolean) return (Boolean) o;
        if (o instanceof Number) return ((Number) o).intValue() != 0;
        return o != null;
    }

    private static int toNum(Object o) {
        if (o instanceof Number) return ((Number) o).intValue();
        return o instanceof Boolean && (Boolean) o ? 1 : 0;
    }
}
