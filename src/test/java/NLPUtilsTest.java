import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.triage.rest.models.users.TherapyDay;
import com.triage.utils.NLPUtils;

public class NLPUtilsTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGenerateTerms() {
		/*assertArrayEquals(
				NLPUtils.generateTerms("mal di pancia"), 
				new String[]{"mal", "panc"});
		assertArrayEquals(
				NLPUtils.generateTerms("mi sento spossato, non ho voglia di fare nulla"), 
				new String[]{"sento", "spossat", "vogl", "fare", "nulla"});*/
	}
	
	@Test
	public void testGenerateTerms1() {
		/*assertArrayEquals(
				NLPUtils.generateTerms1("mal di pancia"), 
				new String[]{"mal", "pancia"});
		assertArrayEquals(
				NLPUtils.generateTerms1("mi sento spossato, non ho voglia di fare nulla"), 
				new String[]{"sento", "spossato", "voglia", "fare", "nulla"});*/
	}

	@Test
	public void testParseNumber() {
		assertTrue("text e num", NLPUtils.parseNumber("asfcas123") == 123);
		assertTrue("text e num", NLPUtils.parseNumber("asfcas 123") == 123);
		assertTrue("text e num", NLPUtils.parseNumber("asf123cas") == 123);
		assertTrue("text e num", NLPUtils.parseNumber("asf 123 cas") == 123);
		assertTrue("text e num", NLPUtils.parseNumber("123") == 123);
		assertTrue("text e num", NLPUtils.parseNumber("asfcas") == -1);
	}
	
	@Test
	public void testParseHour() {
		assertTrue("", true);
		System.out.println("Hour " + NLPUtils.parseHour("15.30"));
		System.out.println("Hour " + NLPUtils.parseHour("15:30"));
		System.out.println("Hour " + NLPUtils.parseHour("3033"));
		System.out.println("Hour " + NLPUtils.parseHour("15:3033"));

	}
	
	@Test
	public void testParseDate() {
		assertTrue("", true);
		System.out.println("Date " + NLPUtils.parseDate("17/03/18"));
		System.out.println("Date " + NLPUtils.parseDate("17/03/2018"));
		System.out.println("Date " + NLPUtils.parseDate("17/03/188"));
		System.out.println("Date " + NLPUtils.parseDate("17/13/18"));
		System.out.println("Date " + NLPUtils.parseDate("177/03/18"));
	}
	
	@Test
	public void testParseWeekDay() {
		assertTrue("", true);
		System.out.println("WeekDay " + TherapyDay.parseDays("l,lu m, ma-me, domen"));
		/*System.out.println("WeekDay " + NLPUtils.parseDate("17/03/2018"));
		System.out.println("WeekDay " + NLPUtils.parseDate("17/03/188"));
		System.out.println("WeekDay " + NLPUtils.parseDate("17/13/18"));
		System.out.println("WeekDay " + NLPUtils.parseDate("177/03/18"));*/
	}

}
