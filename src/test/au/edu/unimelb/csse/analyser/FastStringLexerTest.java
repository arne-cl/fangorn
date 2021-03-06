/*******************************************************************************
 * Copyright 2011 The fangorn project
 * 
 *        Author: Sumukh Ghodke
 * 
 *        Licensed to the Apache Software Foundation (ASF) under one
 *        or more contributor license agreements.  See the NOTICE file
 *        distributed with this work for additional information
 *        regarding copyright ownership.  The ASF licenses this file
 *        to you under the Apache License, Version 2.0 (the
 *        "License"); you may not use this file except in compliance
 *        with the License.  You may obtain a copy of the License at
 * 
 *          http://www.apache.org/licenses/LICENSE-2.0
 * 
 *        Unless required by applicable law or agreed to in writing,
 *        software distributed under the License is distributed on an
 *        "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *        KIND, either express or implied.  See the License for the
 *        specific language governing permissions and limitations
 *        under the License.
 ******************************************************************************/
package au.edu.unimelb.csse.analyser;

import junit.framework.TestCase;

public class FastStringLexerTest extends TestCase {
	public void testFindsTokens() throws Exception {
		FastStringLexer tok = new FastStringLexer();
		int[] positions = tok.tokens("(AA(BBB(CC DDD)(EE FF))(GG HHH))");
		assertEquals(42, positions.length);

		assertPosition(0, 1, positions, 0); // (
		assertPosition(1, 3, positions, 2); // AA
		assertPosition(3, 4, positions, 4); // (
		assertPosition(4, 7, positions, 6); // BBB
		assertPosition(7, 8, positions, 8); // (
		assertPosition(8, 10, positions, 10); // CC
		assertPosition(10, 11, positions, 12); //  
		assertPosition(11, 14, positions, 14); // DDD
		assertPosition(14, 15, positions, 16); // )
		assertPosition(15, 16, positions, 18); // (
		assertPosition(16, 18, positions, 20); // EE
		assertPosition(18, 19, positions, 22); //  
		assertPosition(19, 21, positions, 24); // FF
		assertPosition(21, 22, positions, 26); // )
		assertPosition(22, 23, positions, 28); // )
		assertPosition(23, 24, positions, 30); // (
		assertPosition(24, 26, positions, 32); // GG
		assertPosition(26, 27, positions, 34); //  
		assertPosition(27, 30, positions, 36); // HHH
		assertPosition(30, 31, positions, 38); // )
		assertPosition(31, 32, positions, 40); // )
	}
	
	public void testWhitespaceTokens() throws Exception {
		FastStringLexer tok = new FastStringLexer();
		int[] positions = tok.tokens(" (  AA  ( BBB ( CC  DDD ) ) )");
		assertEquals(40, positions.length);

		assertPosition(0, 1, positions, 0); // 
		assertPosition(1, 2, positions, 2); // (
		assertPosition(2, 4, positions, 4); //   
		assertPosition(4, 6, positions, 6); // AA
		assertPosition(6, 8, positions, 8); //   
		assertPosition(8, 9, positions, 10); // (
		assertPosition(9, 10, positions, 12); //  
		assertPosition(10, 13, positions, 14); // BBB
		assertPosition(13, 14, positions, 16); //  
		assertPosition(14, 15, positions, 18); // (
		assertPosition(15, 16, positions, 20); //  
		assertPosition(16, 18, positions, 22); // CC
		assertPosition(18, 20, positions, 24); //   
		assertPosition(20, 23, positions, 26); // DDD
		assertPosition(23, 24, positions, 28); //  
		assertPosition(24, 25, positions, 30); // )
		assertPosition(25, 26, positions, 32); //  
		assertPosition(26, 27, positions, 34); // )
		assertPosition(27, 28, positions, 36); //  
		assertPosition(28, 29, positions, 38); // )

	}
	
	public void testSingleTuple() throws Exception {
		FastStringLexer tok = new FastStringLexer();
		int[] positions = tok.tokens("(CC DD)");
		assertEquals(10, positions.length);
		assertPosition(0, 1, positions, 0); // (
		assertPosition(1, 3, positions, 2); // CC
		assertPosition(3, 4, positions, 4); //   
		assertPosition(4, 6, positions, 6); // DD
		assertPosition(6, 7, positions, 8); // )
	}

	public void testIncrementIsExecutedEvenIfExceptionIsThrown()
			throws Exception {
		int[] a = new int[1];
		int i = 0;
		try {
			a[i++] = 16;
			assertEquals(16, a[0]);
			a[i++] = 32;
		} catch (ArrayIndexOutOfBoundsException e) {
			assertEquals(2, i);
		}
	}

	private void assertPosition(int expectedStart, int expectedEnd,
			int[] actual, int pos) {
		assertEquals(expectedStart, actual[pos]);
		assertEquals(expectedEnd, actual[pos + 1]);
	}
}
