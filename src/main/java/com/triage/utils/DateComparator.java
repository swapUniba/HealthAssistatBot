package com.triage.utils;

import java.util.Comparator;
import java.util.Date;

public class DateComparator implements Comparator<Date> {
        @Override
        public int compare(Date d1,Date d2) {
           // Date date1=NLPUtils.parseDate(d1);
           // Date date2=NLPUtils.parseDate(d2);
            return d1.compareTo(d2);
        }
}
