package cn.edu.hitsz.compiler.parser;

import cn.edu.hitsz.compiler.lexer.Token;
import cn.edu.hitsz.compiler.parser.table.NonTerminal;


/**
 * 词法分析程序中状态栈的单元，可以同时存储终结符和非终结符
 * @author Klasnov
 */
public class Character {
    private final Token token;
    private final NonTerminal nonTrm;

    public Character(Token token) {
        this.token = token;
        this.nonTrm = null;
    }

    public Character(NonTerminal nonTrm) {
        this.token = null;
        this.nonTrm = nonTrm;
    }

    public Token getTkn() {
        return token;
    }

    public NonTerminal getNonTrm() {
        return nonTrm;
    }

    /**
     * 判断当前内容是否为终结符
     * @return 如果是终结符则返回True，否则返回False
     */
    public boolean isTkn() {
        return this.token != null;
    }

    /**
     * 判断当前内容是否为非终结符
     * @return 如果是非终结符则返回True，否则返回False
     */
    public boolean isNonTrm() {
        return this.nonTrm != null;
    }
}
