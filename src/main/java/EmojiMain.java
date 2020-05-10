import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.triage.utils.NLPUtils;
import com.vdurmont.emoji.EmojiParser;

public class EmojiMain {

	public static void main(String[] args) throws ParseException {
		/*String str = "Scansiona il documento. Premi :paperclip: per iniziare la scansione:";

		String answer = EmojiParser.parseToUnicode(str);
		System.out.println(answer);
		
		str = "Scansiona il documento. Premi :clippy: per iniziare la scansione: "
				+ ":stuck_out_tongue_closed_eyes:	:stuck_out_tongue: :flushed:";
		answer = EmojiParser.parseToUnicode(str);
		System.out.println(answer);*/
		
		/*String[] dates = new String[]{"18/06/1992", "16-06-1992", "16-07-1992",
										"16061992"};

        for(int i=0; i<dates.length; i++){
        	System.out.println(NLPUtils.parseDate(dates[i]));
        }*/
        
		String a = "😵	dizzy_face	😲	astonished 😟	worried	😦	frowning 😧	anguished	😈	smiling_imp";
		String b = "🔚	end	🔙	back 🔛	on	🔜	soon";

		System.out.println(EmojiParser.removeAllEmojis(a));
		System.out.println(EmojiParser.removeAllEmojis(b));
		
	}

}
