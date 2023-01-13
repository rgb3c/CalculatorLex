package com.rgb3c.calculatorLex;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rgb3c.calculatorLex.Logic.*;

public class Logic {

    public static HashMap<String, Function> functionMap;
    static String result;
    static Map<LexemeType, ActionLexeme> actionMap = new HashMap<>();
    static boolean unary = false;
    static BigInteger cellValue;

    public Logic(String input) {
        functionMap = getFunctionMap();
        String expressionText = input;
        List<Lexeme> lexemes = lexAnalyze(expressionText);
        LexemeBuffer lexemeBuffer = new LexemeBuffer(lexemes);
        result = String.valueOf(expr(lexemeBuffer));
    }

    protected static void actionMapFilling() {
        actionMap.put(LexemeType.NAME, new NameAction());
        actionMap.put(LexemeType.OP_MINUS, new MinusAction());
        actionMap.put(LexemeType.NUMBER, new NumberAction());
        actionMap.put(LexemeType.LEFT_BRACKET, new LeftBracketAction());
        actionMap.put(LexemeType.OP_PLUS, new PlusAction());
        actionMap.put(LexemeType.OP_MUL, new MulAction());
        actionMap.put(LexemeType.OP_DIV, new DivAction());
    }

    public String getResult() {
        return result;
    }

    public enum LexemeType {
        LEFT_BRACKET, RIGHT_BRACKET,
        OP_PLUS, OP_MINUS, OP_MUL, OP_DIV,
        NUMBER, NAME, COMMA,
        EOF;
    }

    public interface Function {
        BigInteger apply(List<BigInteger> args);
    }

    public static HashMap<String, Function> getFunctionMap() {
        HashMap<String, Function> functionTable = new HashMap<>();
        functionTable.put("min", args -> {
            if (args.isEmpty()) {
                throw new RuntimeException("No arguments for function min");
            } else {
                BigInteger min = args.get(0);
                for (BigInteger val: args) {
                    if (val.compareTo(min) < 0) {
                        min = val;
                    }
                }
                return min;
            }
        });
        functionTable.put("pow", args -> {
            if (args.size() != 2) {
                throw new RuntimeException("Wrong arguments count for function pow: " + args.size());
            }
//            return Math.pow(args.get(0), args.get(1));
            return new BigInteger(String.valueOf((int)Math.pow(args.get(0).doubleValue(), args.get(1).doubleValue())));
        });
        functionTable.put("rand", args -> {
            if (!args.isEmpty()) {
                throw new RuntimeException("Wrong arguments count for function rand");
            }
            return new BigInteger(String.valueOf((int)(Math.random() * 256f)));
        });
        functionTable.put("avg", args -> {
            BigInteger sum = new BigInteger(String.valueOf(0));
            for (int i = 0; i < args.size(); i++) {
                sum = sum.add(args.get(i));
            }
            return sum.divide(BigInteger.valueOf(args.size()));
        });
        return functionTable;
    }

    public static class LexemeBuffer {
        private int pos;

        public List<Lexeme> lexemes;

        public LexemeBuffer(List<Lexeme> lexemes) {
            this.lexemes = lexemes;
        }

        public Lexeme next() {
            return lexemes.get(pos++);
        }

        public Lexeme get() {
            return lexemes.get(pos - 1);
        }

        public void back() {
            pos--;
        }

        public int getPos() {
            return pos;
        }
    }

    public static class Lexeme {
        LexemeType type;
        String value;

        public Lexeme(LexemeType type, String value) {
            this.type = type;
            this.value = value;
        }

        public Lexeme(LexemeType type, Character value) {
            this.type = type;
            this.value = value.toString();
        }

        @Override
        public String toString() {
            return "Lexeme{" +
                    "type=" + type +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public static List<Lexeme> lexAnalyze (String expText) {

        Map<Character,LexemeType> lexemeMap = new HashMap<>();
        lexemeMap.put('(', LexemeType.LEFT_BRACKET);
        lexemeMap.put(')', LexemeType.RIGHT_BRACKET);
        lexemeMap.put('+', LexemeType.OP_PLUS);
        lexemeMap.put('-', LexemeType.OP_MINUS);
        lexemeMap.put('*', LexemeType.OP_MUL);
        lexemeMap.put('/', LexemeType.OP_DIV);
        lexemeMap.put(',', LexemeType.COMMA);

        ArrayList<Lexeme> lexemes = new ArrayList<>();
        int pos = 0;
        while (pos < expText.length()) {
            char c = expText.charAt(pos);

            if (lexemeMap.containsKey(c)) {
                lexemes.add(new Lexeme(lexemeMap.get(c),c));
                pos++;
            }

            if (c <= '9' && c >= '0') {
                StringBuilder sb = new StringBuilder();
                do {
                    sb.append(c);
                    pos++;
                    if (pos >= expText.length()) {
                        break;
                    }
                    c = expText.charAt(pos);
                } while (c <= '9' && c >= '0');
                lexemes.add(new Lexeme(LexemeType.NUMBER, sb.toString()));
            } else {
                if (c != ' ') {
                    if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                        StringBuilder sb = new StringBuilder();
                        do {
                            sb.append(c);
                            pos++;
                            if (pos >= expText.length()) {
                                break;
                            }
                            c = expText.charAt(pos);
                        } while ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));

                        if (functionMap.containsKey(sb.toString())) {
                            lexemes.add(new Lexeme(LexemeType.NAME, sb.toString()));
                        } else {
                            throw new RuntimeException("Unexpected character: " + c);
                        }
                    }

                } else {
                    pos++;
                }
            }
        }

        lexemes.add(new Lexeme(LexemeType.EOF, ""));
        return lexemes;
    }

    public static BigInteger expr (LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        if (lexeme.type == LexemeType.EOF){
            return BigInteger.valueOf(0);
        } else {
            lexemes.back();
            return plusminus(lexemes);
        }
    }

    public static BigInteger plusminus(LexemeBuffer lexemes) {
        unary = false;
        BigInteger value = multdiv(lexemes);
        Lexeme lexeme = lexemes.next();
        while (lexeme.type == LexemeType.OP_PLUS || lexeme.type == LexemeType.OP_MINUS) {
            value = value.add(actionMap.get(lexeme.type).lexAction(lexemes));
            lexeme = lexemes.next();
        }
        lexemes.back();
        return value;
    }

    public static BigInteger multdiv(LexemeBuffer lexemes) {
        cellValue = new BigInteger(String.valueOf(factor(lexemes)));
        Lexeme lexeme = lexemes.next();
        while (lexeme.type == LexemeType.OP_MUL || lexeme.type == LexemeType.OP_DIV) {
            actionMap.get(lexeme.type).lexAction(lexemes);
            lexeme = lexemes.next();
        }
        lexemes.back();
        return cellValue;
    }

    public static BigInteger factor (LexemeBuffer lexemes) {
        unary = true;
        Lexeme lexeme = lexemes.next();
        if (lexeme == null) {
            throw new RuntimeException("Unexpected token: " + lexeme.value + lexemes.getPos());
        }
        return actionMap.get(lexeme.type).lexAction(lexemes);
    }


    public static BigInteger func(LexemeBuffer lexemeBuffer) {
        String name = lexemeBuffer.next().value;
        Lexeme lexeme = lexemeBuffer.next();

        if (lexeme.type != LexemeType.LEFT_BRACKET) {
            throw new RuntimeException("Wrong functional call synrax at " + lexeme.value);
        }
        ArrayList<BigInteger> args = new ArrayList<>();

        lexeme = lexemeBuffer.next();
        if (lexeme.type != LexemeType.RIGHT_BRACKET) {
            lexemeBuffer.back();
            do {
                args.add(expr(lexemeBuffer));
                lexeme = lexemeBuffer.next();

                if (lexeme.type != LexemeType.COMMA && lexeme.type != LexemeType.RIGHT_BRACKET) {
                    throw new RuntimeException("Wrong functional call synrax at " + lexeme.value);
                }

            } while (lexeme.type == LexemeType.COMMA);
        }
        return functionMap.get(name).apply(args);
    }
}

interface ActionLexeme {
    public BigInteger lexAction(LexemeBuffer lexemes);
}

class NameAction implements ActionLexeme {
    public BigInteger lexAction(LexemeBuffer lexemes) {
        lexemes.back();
        return func(lexemes);
    }
}

class NumberAction implements ActionLexeme {
    public BigInteger lexAction(LexemeBuffer lexemes) {
        Logic.Lexeme lexeme = lexemes.get();
        return new BigInteger(String.valueOf(Integer.parseInt(lexeme.value)));
    }
}

class LeftBracketAction implements ActionLexeme {
    public BigInteger lexAction(LexemeBuffer lexemes) {
        Logic.Lexeme lexeme = lexemes.get();
        BigInteger value = Logic.expr(lexemes);
        lexeme = lexemes.next();
        if (lexeme.type != Logic.LexemeType.RIGHT_BRACKET) {
            throw new RuntimeException("Unexpected token: " + lexeme.value + lexemes.getPos());
        }
        return value;
    }
}

class MinusAction implements ActionLexeme {
    public BigInteger lexAction(LexemeBuffer lexemes) {
        if (unary) {
            BigInteger value = factor(lexemes);
            return value.negate();
        } else {
            return multdiv(lexemes).negate();
        }
    }
}

class PlusAction implements ActionLexeme {
    public BigInteger lexAction(LexemeBuffer lexemes) {
        return multdiv(lexemes);
    }
}

class MulAction implements ActionLexeme {
    public BigInteger lexAction(LexemeBuffer lexemes) {
        return cellValue = cellValue.multiply(factor(lexemes));
    }
}

class DivAction implements ActionLexeme {
    public BigInteger lexAction(LexemeBuffer lexemes) {
        return cellValue = cellValue.divide(factor(lexemes));
    }
}

