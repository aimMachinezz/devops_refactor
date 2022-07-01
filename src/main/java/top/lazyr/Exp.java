package top.lazyr;

import top.lazyr.script.ConfigGen;
import top.lazyr.script.Nsga2Exp;
import top.lazyr.script.RandomExp;

/**
 * @author lazyr
 * @created 2022/2/23
 */
public class Exp {

    public static void main(String[] args) {
        exp(args[0], args[1]);
    }

    public static void exp(String action, String path) {
        switch (action) {
            case "nsga2":
                Nsga2Exp.exp(path);
                break;
            case "random":
                RandomExp.exp(path);
                break;
            case "gen":
                ConfigGen.createConfigExcel(path);
                break;
        }
    }
}
