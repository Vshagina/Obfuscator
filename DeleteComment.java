package com.company;
import java.util.StringTokenizer;

public class DeleteComment {
    private String string;

    public DeleteComment(String string) {
        this.string = string;
    }

    public String deleteComment() {
        StringBuilder sb = new StringBuilder();
        boolean multiComment = false;
        StringTokenizer strtok = new StringTokenizer(string, "\n");
        while (strtok.hasMoreTokens()) {
            StringBuilder strb = new StringBuilder(strtok.nextToken() + "\n");
            for (int i = 0; i < strb.length(); i++) {
                if (!multiComment && strb.charAt(i) == '"' && strb.charAt(i - 1) != '\'') {
                    sb.append(strb.charAt(i));
                    i++;
                    while (i < strb.length() && !endOfString(strb, i)) {
                        sb.append(strb.charAt(i));
                        i++;
                    }
                }
                if (!multiComment && i < strb.length() - 1 && strb.charAt(i) == '/' && strb.charAt(i + 1) == '*') {
                    multiComment = true;
                    i += 2;
                }
                if (multiComment && i < strb.length() - 1 && strb.charAt(i) == '*' && strb.charAt(i + 1) == '/') {
                    i += 2;
                    multiComment = false;
                }
                if (!multiComment && i < strb.length() - 1 && strb.charAt(i) == '/' && strb.charAt(i + 1) == '/') {
                    sb.append("\n");
                    break;
                }
                if (!multiComment && i < strb.length()) {
                    sb.append(strb.charAt(i));
                }
            }
        }
        return sb.toString();
    }

    private boolean endOfString(StringBuilder str, int index) {
        if (index < str.length() && str.charAt(index) == '"') {
            if (str.charAt(index - 1) == '\\') {
                if (str.charAt(index - 2) == '\\') {
                    return true;
                }
                return false;
            }
            return true;
        }
        return false;
    }
}
