package cn.edu.hitsz.compiler.lexer;

import cn.edu.hitsz.compiler.NotImplementedException;
import cn.edu.hitsz.compiler.symtab.SymbolTable;
import cn.edu.hitsz.compiler.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.stream.StreamSupport;

/**
 * TODO: 实验一: 实现词法分析
 * @author HITSZ, Klasnov
 * @see Token 词法单元的实现
 * @see TokenKind 词法单元类型的实现
 */

public class LexicalAnalyzer {
    private final SymbolTable symbolTable;
    private String codes;
    private int codeLen;
    private LinkedList<Token> tokens;

    public LexicalAnalyzer(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.tokens = new LinkedList<>();
    }

    /**
     * 从给予的路径中读取并加载文件内容
     *
     * @param path 路径
     */
    public void loadFile(String path) throws IOException {
        codes = Files.readString(Paths.get(path));
        codeLen = codes.length();
    }

    /**
     * 执行词法分析, 准备好用于返回的 token 列表
     * 需要维护实验一所需的符号表条目, 而得在语法分析中才能确定的符号表条目的成员可以先设置为 null
     */
    public void run() {
        // 扫描数据初始化
        int bgnPt = 0, scnPt = 0;
        char temp;
        boolean isVain;
        String str;
        // 跳过代码开头的空格、回车
        do {
            isVain = false;
            temp = codes.charAt(scnPt);
            if (isVain(temp)) {
                isVain = true;
                scnPt++;
                bgnPt++;
            }
        } while(isVain);
        // 扫描代码
        while (scnPt < codeLen) {
            temp = codes.charAt(scnPt++);
            // 标识符或关键字情况
            if (isLte(temp)) {
                do {
                    temp = codes.charAt(scnPt++);
                } while (isLte(temp) || isNum(temp));
                str = codes.substring(bgnPt, --scnPt);
                bgnPt = scnPt;
                switch (str) {
                    case "int", "return" -> tokens.add(Token.simple(str));
                    default -> tokens.add(Token.normal("id", str));
                }
                continue;
            }
            // 数字常量
            if (isNum(temp)) {
                do {
                    temp = codes.charAt(scnPt++);
                } while (isNum(temp));
                str = codes.substring(bgnPt, --scnPt);
                bgnPt = scnPt;
                tokens.add(Token.normal("IntConst", str));
                continue;
            }
            // 格式控制符号
            if (isVain(temp)) {
                bgnPt++;
                continue;
            }
            // 分界符
            if (!isLte(temp) && !isNum(temp) && !isVain(temp)) {
                str = switch (temp) {
                    case '=' -> "=";
                    case ',' -> ",";
                    case ';' -> "Semicolon";
                    case '+' -> "+";
                    case '-' -> "-";
                    case '*' -> "*";
                    case '/' -> "/";
                    case '(' -> "(";
                    case  ')' -> ")";
                    default -> "";
                };
                bgnPt++;
                if (str.compareTo("") != 0) {
                    tokens.add(Token.simple(str));
                }
            }
        }
        tokens.add(Token.eof());
    }

    /**
     * 判断一个输入的字符是否为空格或制表符
     *
     * @param c 需要判断的字符
     */
    private boolean isVain(char c) {
        return ((c == '\t') || (c == '\n') || (c == '\r'));
    }

    /**
     * 判断一个输入的字符是否为字母
     *
     * @param c 需要判断的字符
     */
    private boolean isLte(char c) {
        return ((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z'));
    }

    /**
     * 判断一个输入的字符是否为数字
     *
     * @param c 需要判断的字符
     */
    private boolean isNum(char c) {
        return ((c >= '0') && (c <= '9'));
    }

    /**
     * 获得词法分析的结果, 保证在调用了 run 方法之后调用
     *
     * @return Token 列表
     */
    public LinkedList<Token> getTokens() {
        return tokens;
    }

    public void dumpTokens(String path) {
        FileUtils.writeLines(
            path,
            StreamSupport.stream(getTokens().spliterator(), false).map(Token::toString).toList()
        );
    }


}
