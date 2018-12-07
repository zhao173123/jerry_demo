package com.sun.java8.function;

import com.google.common.base.Predicate;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * 谓词讲解(二)
 *
 * @author jerry
 */
public class SubsumptionTest{

    @Test
    public void testPredicate(){
        StockPredicate selection = new StockPredicate("Google", "IBM", "Oracle");
        assertTrue(selection.apply("Google"));
        assertFalse(selection.apply("Apple"));
    }

    @Test
    public void testSubsumption(){
        StockPredicate standard = new StockPredicate("Google", "IBM", "Oracle");
        StockPredicate request1 = new StockPredicate("Google", "IBM");
        StockPredicate request2 = new StockPredicate("Google", "IBM", "SAP");

        assertTrue(standard.encompasses(request1));
        assertFalse(standard.encompasses(request2));
    }

    /**
     * 接受T对象并返回boolean值
     */
    public final class StockPredicate implements Predicate<String>{

        private final Set<String> tickers;

        public StockPredicate(String... tickers){
            this.tickers = new HashSet<>(Arrays.asList(tickers));
        }

        public StockPredicate(Set<String> tickers){
            this.tickers = tickers;
        }

        @Override
        public boolean apply(String ticker){
            return tickers.contains(ticker);
        }

        //encompass /ɛnˈkʌmpəs/ ： vt.围绕,包围
        public boolean encompasses(StockPredicate predicate){
            return tickers.containsAll(predicate.tickers);
        }

    }
}


