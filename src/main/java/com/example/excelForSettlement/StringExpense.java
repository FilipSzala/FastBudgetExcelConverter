package com.example.excelForSettlement;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Component

public class StringExpense {
    public static ExpenseType ExpenseType (String name){
        name = name.trim().toLowerCase();
        name = replacePolishCharacters(name);
        if (name.equals("")){
            return ExpenseType.WITHOUTSPLIT;
        }
        if (name.equals("na pol")){
            return ExpenseType.SPLITINHALF;
        }
        if (name.equals("roksana")){
            return ExpenseType.SPLITINROKSANA;
        }
        if (checkRegexForFilipAmount(name)){
            return ExpenseType.FILIPSPECIFICPRICEROKSANAREMAINING;
        }
        if (checkRegexForRoksanaAmount(name)){
            return ExpenseType.ROKSANASPECIFICPRICEFILIPREMAINING;
        }
        if (checkRegexForFilipSplitMore(name)){
            return ExpenseType.FILIPSPLITMORE;
        }
        if (checkRegexForRoksanaSplitMore(name)){
            return ExpenseType.ROKSANASPLITMORE;
        }
        if (checkRegexForSpecificesPricesFilipFirst(name)){
            return ExpenseType.SPLITSPECIFICEPRICESFILIPFIRST;
        }

        if (checkRegexForSpecificesPricesRoksanaFirst(name)){
            return ExpenseType.SPLITSPECIFICEPRICESROKSANAFIRST;
        }
        if (checkRegexForDoubleSpecificesPricesRestSplitInHalfFilipFirst(name)){
            return ExpenseType.SPLITSPECIFICEPRICESFILIPFIRSTRESTSPLITINHALF;
        }
        if (checkRegexForDoubleSpecificesPricesRestSplitInHalfRoksanaFirst(name)){
            return ExpenseType.SPLITSPECIFICEPRICESROKSANAFIRSTRESTSPLITINHALF;
        }
        if (checkRegexForFilipSingleSpecificePriceRestSplitInHalf(name)){
            return ExpenseType.SPLITSPECIFICESINGLEFILIPPRICERESTSPLITINHALF;
        }
        if (checkRegexForRoksanaSingleSpecificePriceRestSplitInHalf(name)){
            return ExpenseType.SPLITSPECIFICESINGLEROKSANAPRICERESTSPLITINHALF;
        }


        return ExpenseType.OTHER;
    }
    public static Boolean checkRegexForFilipAmount (String trimmedInput2) {
        String regex = "ja \\d+( ?zl)?,? roksana reszt[ae]|ja \\d+( ?zl)?,? reszt[ae] roksana|filip \\d+( ?zl)?,? roksana reszt[ae]|filip \\d+( ?zl)?,? reszt[ae] roksana";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(trimmedInput2);
        return matcher.matches();
    }
    public static Boolean checkRegexForRoksanaAmount (String trimmedInput2) {
        String regex = "roksana \\d+( ?zl)?,? ja reszt[ae]|roksana \\d+( ?zl)?,? reszt[ae] ja|roksana \\d+( ?zl)?,? filip reszt[ae]|roksana \\d+( ?zl)?,? reszt[ae] filip";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(trimmedInput2);
        return matcher.matches();
    }
    public static Boolean checkRegexForFilipSplitMore (String trimmedInput2) {
        String regex = "ja \\d+( ?zl)? wiecej|ja \\d+( ?zl)? ?,? reszt[ae] na pol|filip \\d+( ?zl)? wiecej|filip \\d+( ?zl)? ?,? reszt[ae] na pol";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(trimmedInput2);
        return matcher.matches();
    }
    public static Boolean checkRegexForRoksanaSplitMore (String trimmedInput2) {
        String regex = "roksana \\d+( ?zl)? wiecej|roksana \\d+( ?zl)? ?,? reszt[ae] na pol";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(trimmedInput2);
        return matcher.matches();
    }
    public static Boolean checkRegexForSpecificesPricesFilipFirst (String trimmedInput2) {
        String regex = "ja \\d+( ?zl)?,? ?roksana \\d+( ?zl)?|filip \\d+( ?zl)?,? ?roksana \\d+( ?zl)?";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(trimmedInput2);
        return matcher.matches();
    }
    public static Boolean checkRegexForSpecificesPricesRoksanaFirst (String trimmedInput2) {
        String regex = "roksana \\d+( ?zl)?,? ?ja \\d+( ?zl)?|roksana \\d+( ?zl)?,? ?filip \\d+( ?zl)?";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(trimmedInput2);
        return matcher.matches();
    }
    public static Boolean checkRegexForRoksanaSingleSpecificePriceRestSplitInHalf(String trimmedInput2) {
        String regex = "roksana \\d+( ?zl)?,? ?(wiecej ?,? ?)?reszt[ae] na pol";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(trimmedInput2);
        return matcher.matches();
    }
    public static Boolean checkRegexForFilipSingleSpecificePriceRestSplitInHalf(String trimmedInput2) {
        String regex = "ja \\d+( ?zl)?,? ?(wiecej ?,? ?)?reszt[ae] na pol|filip \\d+( ?zl)?,? ?(wiecej ?,? ?)?reszt[ae] na pol";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(trimmedInput2);
        return matcher.matches();
    }
    public static Boolean checkRegexForDoubleSpecificesPricesRestSplitInHalfFilipFirst(String trimmedInput2) {
        String regex = "ja \\d+( ?zl)?,? ?roksana \\d+( ?zl)?,? ?reszt[ae] na pol|filip \\d+( ?zl)?,? ?roksana \\d+( ?zl)?,? ?reszt[ae] na pol";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(trimmedInput2);
        return matcher.matches();
    }
    public static Boolean checkRegexForDoubleSpecificesPricesRestSplitInHalfRoksanaFirst(String trimmedInput2) {
        String regex = "roksana \\d+( ?zl)?,? ?ja \\d+( ?zl)?,? ?reszt[ae] na pol|roksana \\d+( ?zl)?,? ?filip \\d+( ?zl)?,? ?reszt[ae] na pol";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(trimmedInput2);
        return matcher.matches();
    }

    public static String replacePolishCharacters(String input) {
        return input
                .replace('ą', 'a')
                .replace('ć', 'c')
                .replace('ę', 'e')
                .replace('ł', 'l')
                .replace('ń', 'n')
                .replace('ó', 'o')
                .replace('ś', 's')
                .replace('ź', 'z')
                .replace('ż', 'z');
    }

    public static double extractSingleNumber(String input) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return  Double.parseDouble(matcher.group());
        }
        throw new IllegalArgumentException("No integer found in the input string");
    }

    public static double[] extractTwoDoubles(String input) {
        Pattern pattern = Pattern.compile("\\d+(\\.\\d+)?");
        Matcher matcher = pattern.matcher(input);

        double[] numbers = new double[2];
        int count = 0;

        while (matcher.find()) {
            if (count < 2) {
                numbers[count] = Double.parseDouble(matcher.group());
                count++;
            } else {
                break;
            }
        }

        if (count != 2) {
            throw new IllegalArgumentException("The input string does not contain exactly two double numbers");
        }

        return numbers;
    }

}
