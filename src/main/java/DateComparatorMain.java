/*import java.time.LocalDate;
import java.time.Month;
import java.time.Period;*/

import com.triage.utils.NLPUtils;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.util.Calendar;
import java.util.Date;

public class DateComparatorMain {

	public static void main(String[] args) {
		//System.out.println("07/18".split("-")[0]);
		//System.out.println(new LocalDate(NLPUtils.parseDate("07/18")).getYear());
		//Period period = new Period(new LocalDate(NLPUtils.parseDate("01/07/18")),new LocalDate(NLPUtils.parseDate("30/09/19")), PeriodType.yearMonthDay());
		//System.out.println(period.getDays());
		//System.out.println(NLPUtils.parseHour("10:20").getTime());
		LocalDate dt = new LocalDate(new Date());
		//dt.setTime();
		Calendar cal = Calendar.getInstance();
		String[] hour = "10:20".split(":");
		cal.set(dt.getYear(), dt.getMonthOfYear()-1, dt.getDayOfMonth(),Integer.valueOf(hour[0]),Integer.valueOf(hour[1]));
		System.out.println(cal.getTime());
		System.out.println(NLPUtils.isPassedDate(cal.getTime()));
	}
}
		/*Calendar calendar = Calendar.getInstance();
		Date currentDate = new Date(System.currentTimeMillis());

		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int date = cal.get(Calendar.DATE);
		cal.clear();
		cal.set(year, month, date);
		System.out.println("Sum of x+y = " + currentDate);
		System.out.println(calendar.get(Calendar.DATE));
		System.out.println(cal.getTime());

		DateTool dt = new DateTool();
		System.out.println("DateTool:" + dt.format("dd-MM-yyyy", cal.getTime()));

		LocalDate bday = new LocalDate(1994, 2, 3);
		LocalDate bday2 = new LocalDate(1995, 2, 5);
		Period period = new Period(bday, bday2, PeriodType.yearMonthDay());
		System.out.println("Y: " + period.getYears() + ", M: " + period.getMonths() +
				", D: " + period.getDays());
	}
}

/*
		LocalDate bday = LocalDate.of(Integer.valueOf("1994"), Integer.valueOf("2"), Integer.valueOf("3"));
		Period age = Period.between(bday, LocalDate.now());
		int years = age.getYears();
		int months = age.getMonths();
		int days = age.getDays();
		System.out.println("number of years: " + years);
		System.out.println("number of months: " + months);
		System.out.println("number of days: " + days);

		TreeSet<String> tt= new TreeSet<String>(new DateComparator());
		String dt1= "11/12/2018";
		String dt2= "10/09/2010";
		String dt3= "10/09/2011";
	//	String dt2= "10/09/2010";
		tt.add(dt2);
		tt.add(dt1);
		tt.add(dt3);
		System.out.println(tt);
		Iterator<String> itr = tt.iterator();
		List<Period> periods = new ArrayList<Period>();
		String last = null;
		while (itr.hasNext()) {
			String dtt1= null;
			if (last==null)
				dtt1 = itr.next();
			else
				dtt1=last;
			if(itr.hasNext()) {
				String dtt2 = itr.next();
				periods.add(new DateUtils().difference(dtt1, dtt2));
				last = dtt2;
			}
		}
		int yearsum=0;
		int monthsum=0;
		int daysum=0;
		for (Period p: periods){
		 	yearsum+= p.getYears();
		 	monthsum+= p.getMonths();
			daysum+= p.getDays();
		}
		int averageyears = yearsum/ periods.size();
		int averagemonths = monthsum/ periods.size();
		int averagedays = daysum/ periods.size();
		System.out.println("media anni: " + averageyears);
		System.out.println("media mesi: " + averagemonths);
		System.out.println("media giorni: " + averagedays);
		//vuoi impostare un nuovo tra: Date of today + 4 anni 2 mesi e X giorni?
	}

}
*/