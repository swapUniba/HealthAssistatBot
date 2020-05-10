import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.triage.utils.ArrayListUtils;

public class ArrayListUtilstTest {
	
	ArrayList<String> array;
	
	@Before
	public void setUp() throws Exception {
		array = new ArrayList<String>();
		array.add("A01ds ");
		array.add(" 012 ");
		array.add("abcd");
		array.add("245%&$");
		array.add(" 012 ");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTransformArraysToString() {
		assertEquals("A01ds 012 abcd 245%&$ 012", ArrayListUtils.transformArraysToString(array));
	}

	@Test
	public void testCountDistinct() {
		assertEquals(4, ArrayListUtils.countDistinct(array));
	}

}
