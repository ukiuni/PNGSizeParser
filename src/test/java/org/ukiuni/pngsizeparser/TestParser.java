/**
PNG Size Parser

Copyright (c) 2017 ukiuni

This software is released under the MIT License.
http://opensource.org/licenses/mit-license.php
*/
package org.ukiuni.pngsizeparser;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.ukiuni.pngsizeparser.Parser.NotPNGException;
import org.ukiuni.pngsizeparser.Parser.Size;

public class TestParser {
	@Test
	public void testSample1() throws FileNotFoundException, NotPNGException, IOException {
		Size size = Parser.parse("src/test/resources/sample1.png");
		Assert.assertEquals(126, size.width);
		Assert.assertEquals(124, size.height);
	}
}
