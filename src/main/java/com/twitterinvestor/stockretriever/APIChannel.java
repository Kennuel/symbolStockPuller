package com.twitterinvestor.stockretriever;

import org.patriques.AlphaVantageConnector;
import org.patriques.TimeSeries;
import org.patriques.output.timeseries.Daily;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.Interval;

import java.io.IOException;
import java.util.Calendar;

@RestController
public class APIChannel {

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Daily> getStuff(@RequestParam String product) {

        String apiKey = "C3C6MW3JC1SIOE6W";
        int timeout = 3000;

        AlphaVantageConnector apiConnector = new AlphaVantageConnector(apiKey, timeout);
        TimeSeries stockTimeSeries = new TimeSeries(apiConnector);
        return ResponseEntity.ok(stockTimeSeries.daily(product));
    }

    @GetMapping(path = "orange", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Stock> getStuffTwo(
            @RequestParam String product,
            @RequestParam int day,
            @RequestParam int month,
            @RequestParam int year) throws IOException {


        Calendar from = Calendar.getInstance();
        from.set(year,month,day,0,0,0);

        Calendar to = Calendar.getInstance();
        to.set(year,month,day,23,0,0);
        to.add(Calendar.DAY_OF_YEAR, 3);

        if(isWeekend(from)) {
            from = setFridayBeforeWeekend(from);
            to = setMondayAfterWeekend(to);
        } else if(isWeekend(to)) {
            to = setMondayAfterWeekend(to);
        }

        Stock google = YahooFinance.get(product, from, to, Interval.DAILY);


        return ResponseEntity.ok(google);
    }

    private Calendar setFridayBeforeWeekend(Calendar cal) {

        if (isSaturday(cal)) {
            cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        }
        if (isSunday(cal)) {
            cal.add(Calendar.DATE, -2);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        }

        return cal;
    }

    private Calendar setMondayAfterWeekend(Calendar cal) {
        if (isSaturday(cal)) {
            cal.add(Calendar.DATE, 2);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        }

        if (isSunday(cal)) {
            cal.add(Calendar.DATE, 2);
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        }

        return cal;
    }

    private boolean isWeekend(Calendar cal) {
        return  isSaturday(cal) || isSunday(cal) ;
    }

    private boolean isSunday(Calendar cal) {
        return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    private boolean isSaturday(Calendar cal) {
        return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
    }
}
