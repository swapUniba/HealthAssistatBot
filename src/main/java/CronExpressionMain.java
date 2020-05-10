import java.text.ParseException;
import java.util.Locale;

import net.redhogs.cronparser.CronExpressionDescriptor;
import net.redhogs.cronparser.Options;
import org.quartz.CronExpression;

public class CronExpressionMain {

	public static void main(String[] args) throws ParseException {
		Options opt = new Options();
		opt.setTwentyFourHourTime(true);
		//String res = CronExpressionDescriptor.getDescription("0 17 * * 2,1,4,6", opt, Locale.ITALIAN);
		boolean result = CronExpression.isValidExpression("0 00 11 16 06 ? 2019");
		System.out.println(result);
		String res = CronExpressionDescriptor.getDescription("0 " + 0 + " " + 11 + " " + 16+ " "+ 6 + " ? " + 2019);
		System.out.println(res);
	}

}
