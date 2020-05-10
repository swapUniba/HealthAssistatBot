import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.triage.rest.enummodels.ItalianProvince;

public class ItalianProvinceTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetProvince() {
		//Test abbreviazioni province
		assertEquals("BA", ItalianProvince.getProvinceExact("BA"));
		assertEquals("CZ", ItalianProvince.getProvinceExact("cz"));
		assertEquals("AV", ItalianProvince.getProvinceExact("aV"));
		assertEquals("AT", ItalianProvince.getProvinceExact("At"));
		assertEquals("VB", ItalianProvince.getProvinceExact("VB"));
		assertEquals("VV", ItalianProvince.getProvinceExact("VV"));
		
		//Test nome provincia no errori
		assertEquals("BA", ItalianProvince.getProvinceFuzzy("bari"));
		assertEquals("CZ", ItalianProvince.getProvinceFuzzy("CATANZARO"));
		assertEquals("AV", ItalianProvince.getProvinceFuzzy("aveLLiNo"));
		assertEquals("VV", ItalianProvince.getProvinceFuzzy("Vibo Valentia"));
		
		//Test nome provincia parziali
		assertEquals("VV", ItalianProvince.getProvinceFuzzy("Valentia"));
		assertEquals("VV", ItalianProvince.getProvinceFuzzy("Vibo"));
		assertEquals("VS", ItalianProvince.getProvinceFuzzy("Campidano"));
		
		//Test nome provincia con errori
		//assertEquals("BA", ItalianProvince.getProvince("bri")); #Error on this. Return BR
		assertEquals("MB", ItalianProvince.getProvinceFuzzy("monz"));
		assertEquals("CZ", ItalianProvince.getProvinceFuzzy("CATNZARO"));
		assertEquals("AV", ItalianProvince.getProvinceFuzzy("avLLiNo"));
		
		//Test nessun risultato
		assertEquals(null, ItalianProvince.getProvinceFuzzy("sadsac"));
		assertEquals(null, ItalianProvince.getProvinceFuzzy("ascvpk"));
	}

}
