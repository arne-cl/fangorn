package au.edu.unimelb.csse.join;

import java.io.IOException;

import org.apache.lucene.index.DocsAndPositionsEnum;
import org.apache.lucene.index.IndexReader;
import org.junit.Before;

import au.edu.unimelb.csse.Operator;

public class LookaheadTermEarlyMRRJoinTest extends PairJoinTestCase {
	JoinBuilder join;
	Operator[] lookaheadOps = new Operator[] { Operator.DESCENDANT,
			Operator.ANCESTOR, Operator.FOLLOWING, Operator.PRECEDING };

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		join = LookaheadTermEarlyMRRJoin.JOIN_BUILDER;
	}

	public void testFollowingOpWithSimpleFollows() throws Exception {
		int[][] expectedResults = new int[][] {
				new int[] { 1, 2, 2, 4, 2, 3, 2, 6 },
				new int[] { 1, 2, 2, 4, 2, 3, 2, 6 }, new int[] { 1, 2, 2, 4 },
				new int[] { 2, 3, 2, 6 } };
		int[] expectedNumComparisons = new int[] { 3, 3, 2, 1 };
		String sent = "(SS(PP(PP WW))(NN(FF WW))(ZZ(FF WW)))";
		String term1 = "PP";
		String term2 = "FF";
		Operator op = Operator.FOLLOWING;

		assertJoinWithLookaheads(sent, term1, op, term2, 8, expectedResults,
				expectedNumComparisons);

		assertTermEarlyJoin(sent, term1, op, term2, 8,
				new int[] { 1, 2, 2, 4 }, 1);

		assertRegularJoin(sent, term1, op, term2, 8, new int[] { 1, 2, 2, 4, 2,
				3, 2, 6 }, 2);
	}

	public void testFollowingOpWithOnePrecedingIgnored() throws Exception {
		int[][] expectedResults = new int[][] {
				new int[] { 2, 3, 2, 6, 3, 4, 2, 8 },
				new int[] { 2, 3, 2, 6, 3, 4, 2, 8 }, new int[] { 2, 3, 2, 6 },
				new int[] { 3, 4, 2, 8 } };
		int[] expectedNumComparisons = new int[] { 4, 6, 3, 1 };

		String sent = "(SS(AA(FF WW))(PP(PP WW))(NN(FF WW))(ZZ(FF WW)))";
		String term1 = "PP";
		String term2 = "FF";
		Operator op = Operator.FOLLOWING;

		assertJoinWithLookaheads(sent, term1, op, term2, 8, expectedResults,
				expectedNumComparisons);

		assertTermEarlyJoin(sent, term1, op, term2, 8,
				new int[] { 2, 3, 2, 6 }, 2);

		assertRegularJoin(sent, term1, op, term2, 8, new int[] { 2, 3, 2, 6, 3,
				4, 2, 8 }, 3);
	}

	public void testFollowingOpWithNoneFollowing() throws Exception {
		String sent = "(SS(AA(FF WW))(PP(PP WW))(NN(JJ WW))(ZZ(JJ WW)))";
		String term1 = "PP";
		String term2 = "FF";
		Operator op = Operator.FOLLOWING;
	
		assertJoinWithLookaheads(sent, term1, op, term2, 8, new int[][] {new int[] {}, new int[] {}, new int[] {}, new int[] {}},
				new int[] {1, 2, 1, 2});

		assertTermEarlyJoin(sent, term1, op, term2, 8, new int[] {}, 1);

		assertRegularJoin(sent, term1, op, term2, 8, new int[] {}, 1);
	}

	public void testPrecedingOpSimple() throws Exception {
		String sent = "(SS(PP(PP WW))(NN(FF WW))(ZZ(FF WW)))";
		String term1 = "FF";
		String term2 = "PP";
		Operator op = Operator.PRECEDING;
		int[][] expectedResults = new int[][] { new int[] { 0, 1, 1, 7 },
				new int[] { 0, 1, 2, 2 }, new int[] { 0, 1, 2, 2 },
				new int[] { 0, 1, 2, 2 } };
		int[] expectedNumComparisons = new int[] { 2, 2, 3, 1 };

		assertJoinWithLookaheads(sent, term1, op, term2, 8, expectedResults,
				expectedNumComparisons);

		assertTermEarlyJoin(sent, term1, op, term2, 8,
				new int[] { 0, 1, 1, 7 }, 1);

		assertRegularJoin(sent, term1, op, term2, 8, new int[] { 0, 1, 1, 7, 0,
				1, 2, 2 }, 2);
	}

	public void testPrecedingOpWithNonePreceding() throws Exception {
		String sent = "(SS(NN(FF WW))(ZZ(FF WW))(PP(PP WW)))";
		String term1 = "FF";
		String term2 = "PP";
		Operator op = Operator.PRECEDING;
		for (Operator nextOp : lookaheadOps) {
			IndexReader r = setupIndexWithDocs(sent);
			DocsAndPositionsEnum posEnum = initPrevGetNext(r, 8, 0, term1,
					term2);
			lookaheadJoinAndAssertOutput(0, 2, join, prev, op, nextOp, posEnum,
					0);
		}

		assertTermEarlyJoin(sent, term1, op, term2, 8, new int[] {}, 2);

		assertRegularJoin(sent, term1, op, term2, 8, new int[] {}, 2);
	}

	public void testPrecedingOpWithOneIgnored() throws Exception {
		String sent = "(SS(PP(PP WW)(FF WW))(ZZ(FF WW))(PP WW))";
		String term1 = "FF";
		String term2 = "PP";
		int[][] expectedResults = new int[][] { new int[] { 0, 2, 1, 7 },
				new int[] { 0, 1, 2, 3 }, new int[] { 0, 1, 2, 3 },
				new int[] { 0, 1, 2, 3 } };
		int[] expectedNumComparisons = new int[] { 5, 3, 5, 2 };
		Operator op = Operator.PRECEDING;

		assertJoinWithLookaheads(sent, term1, op, term2, 8, expectedResults,
				expectedNumComparisons);

		assertTermEarlyJoin(sent, term1, op, term2, 8,
				new int[] { 0, 2, 1, 7 }, 2);

		assertRegularJoin(sent, term1, op, term2, 8, new int[] { 0, 2, 1, 7, 0,
				1, 2, 3 }, 4);
	}

	public void testDescendantOpWithMixedPositions() throws Exception {
		String sent = "(SS(AA(PP WW)(AA(DD WW)))(ZZ(DD ww))(AA(FF WW))(AA(DD(AA WW))))";
		String term1 = "AA";
		String term2 = "DD";
		int[][] expectedResults = new int[][] {
				new int[] { 1, 2, 3, 3, 4, 5, 2, 11 },
				new int[] { 1, 2, 3, 3, 4, 5, 2, 11 },
				new int[] { 1, 2, 3, 3 }, new int[] { 4, 5, 2, 11 } };
		int[] expectedNumComparisons = new int[] { 8, 9, 2, 2 };
		Operator op = Operator.DESCENDANT;

		assertJoinWithLookaheads(sent, term1, op, term2, 20, expectedResults,
				expectedNumComparisons);

		assertTermEarlyJoin(sent, term1, op, term2, 20,
				new int[] { 1, 2, 3, 3 }, 1);

		assertRegularJoin(sent, term1, op, term2, 20, new int[] { 1, 2, 3, 3,
				4, 5, 2, 11 }, 6);
	}

	public void testChildOpWithMixedPositions() throws Exception {
		String sent = "(SS(AA(PP WW)(AA(CC WW))(CC WW))"
				+ "(AA(PP WW)(AA(DD(CC WW)))(CC WW))(ZZ(CC WW))"
				+ "(AA(FF WW))(AA(CC(AA(DD(CC WW))))))";
		String term1 = "AA";
		String term2 = "CC";
		int[][] expectedResults = new int[][] {
				new int[] { 1, 2, 3, 3, 2, 3, 2, 5, 5, 6, 2, 11, 8, 9, 2, 20 },
				new int[] { 1, 2, 3, 3, 2, 3, 2, 5, 5, 6, 2, 11, 8, 9, 2, 20 },
				new int[] { 1, 2, 3, 3 }, new int[] { 8, 9, 2, 20 } };
		int[] expectedNumComparisons = new int[] { 20, 22, 3, 3 };
		Operator op = Operator.CHILD;

		assertJoinWithLookaheads(sent, term1, op, term2, 28, expectedResults,
				expectedNumComparisons);

		assertTermEarlyJoin(sent, term1, op, term2, 28,
				new int[] { 1, 2, 3, 3 }, 2);

		assertRegularJoin(sent, term1, op, term2, 28, new int[] { 1, 2, 3, 3,
				2, 3, 2, 5, 5, 6, 2, 11, 8, 9, 2, 20 }, 16);
	}

	/**
	 * 
	 * String sent; String term1; String term2; int[][] expectedResults; int[]
	 * expectedNumComparisons;
	 * 
	 * @throws Exception
	 */
	public void testAncestorOpWithNestedDescendants() throws Exception {
		String sent = "(SS(AA(PP WW)(AA(DD WW)))(ZZ(DD ww))(AA(FF WW))(AA(DD(AA WW))))";
		String term1 = "DD";
		String term2 = "AA";
		int[][] expectedResults = new int[][] {
				new int[] { 0, 2, 1, 12, 4, 5, 1, 12 },
				new int[] { 1, 2, 2, 4, 4, 5, 1, 12 },
				new int[] { 1, 2, 2, 4 }, new int[] { 4, 5, 1, 12 } };
		int[] expectedNumComparisons = new int[] { 9, 10, 4, 2 };
		Operator op = Operator.ANCESTOR;

		assertJoinWithLookaheads(sent, term1, op, term2, 12, expectedResults,
				expectedNumComparisons);

		assertTermEarlyJoin(sent, term1, op, term2, 12,
				new int[] { 0, 2, 1, 12 }, 1);

		assertRegularJoin(sent, term1, op, term2, 12, new int[] { 0, 2, 1, 12,
				1, 2, 2, 4, 4, 5, 1, 12 }, 7);
	}

	public void testChildOp() throws Exception {
		String sent = "(SS(CC WW)(PP(CC WW)(JJ WW))(PP(QQ(CC WW))(PP(CC WW)(ZZ WW))))";
		String term1 = "PP";
		String term2 = "CC";
		int[][] expectedResults = new int[][] {
				new int[] { 1, 2, 2, 4, 4, 5, 3, 9 },
				new int[] { 1, 2, 2, 4, 4, 5, 3, 9 }, new int[] { 1, 2, 2, 4 },
				new int[] { 4, 5, 3, 9 } };
		int[] expectedNumComparisons = new int[] { 9, 9, 3, 1 };
		Operator op = Operator.CHILD;

		assertJoinWithLookaheads(sent, term1, op, term2, 12, expectedResults,
				expectedNumComparisons);

		assertTermEarlyJoin(sent, term1, op, term2, 12,
				new int[] { 1, 2, 2, 4 }, 2);

		assertRegularJoin(sent, term1, op, term2, 12, new int[] { 1, 2, 2, 4,
				4, 5, 3, 9 }, 7);
	}

	public void testParentOp() throws Exception {
		String sent = "(SS(CC WW)(PP(CC WW)(JJ WW))(PP(QQ(CC WW))(PP(CC WW)(ZZ WW))))";
		String term1 = "CC";
		String term2 = "PP";
		int[][] expectedResults = new int[][] {
				new int[] { 1, 3, 1, 11, 4, 6, 2, 10 },
				new int[] { 1, 3, 1, 11, 4, 6, 2, 10 },
				new int[] { 1, 3, 1, 11 }, new int[] { 4, 6, 2, 10 } };
		int[] expectedNumComparisons = new int[] { 9, 6, 3, 1 };
		Operator op = Operator.PARENT;

		assertJoinWithLookaheads(sent, term1, op, term2, 16, expectedResults,
				expectedNumComparisons);

		assertTermEarlyJoin(sent, term1, op, term2, 16,
				new int[] { 1, 3, 1, 11 }, 2);

		assertRegularJoin(sent, term1, op, term2, 16, new int[] { 1, 3, 1, 11,
				4, 6, 2, 10 }, 7);
	}

	public void testFixChildBug() throws Exception {
		String sent = "(S1 (S (PP (IN In) (NP (NP (NNP September)) (, ,) (NP (CD 2008)))) (, ,) (NP (DT the) (NN organization)) (VP (VBD marked) (NP (NP (NP (DT the) (JJ 5th) ('' ') (NN anniversary) ('' ')) (PP (IN of) (NP (NP (NP (DT the) (NNP RIAA) (POS 's)) (NN litigation) (NN campaign)) (PP (IN by) (S (VP (VBG publishing) (NP (DT a) (ADJP (RB highly) (JJ critical)) (, ,) (JJ detailed) (NN report))))) (, ,) (VP (VBN entitled) ('' ') (NP (NNP RIAA)) (PP (IN v.) (NP (NNP The) (NNP People))))))) (: :) (NP (NP (CD Five) (NNS Years)) (RB Later) (POS '))) (, ,) (S (VP (VBG concluding) (SBAR (IN that) (S (NP (DT the) (NN campaign)) (VP (AUX was) (NP (DT a) (NN failure)))))))) (. .)))";
		String term1 = "NP";
		String term2 = "VP";
		int[][] expectedResults = new int[][] { new int[] { 28, 34, 7, 52 },
				new int[] { 28, 34, 7, 52 }, new int[] { 28, 34, 7, 52 },
				new int[] { 28, 34, 7, 52 } };
		int[] expectedNumComparisons = new int[] { 32, 47, 17, 41 };
		Operator op = Operator.CHILD;

		assertJoinWithLookaheads(sent, term1, op, term2, 68, expectedResults,
				expectedNumComparisons);

		assertTermEarlyJoin(sent, term1, op, term2, 68, new int[] { 28, 34, 7,
				52 }, 16);

		assertRegularJoin(sent, term1, op, term2, 68,
				new int[] { 28, 34, 7, 52 }, 30);
	}

	public void testFixParentBug() throws Exception {
		String sent = "(S1 (S (PP (IN In) (NP (NP (NNP September)) (, ,) (NP (CD 2008)))) (, ,) (NP (DT the) (NN organization)) (VP (VBD marked) (NP (NP (NP (DT the) (JJ 5th) ('' ') (NN anniversary) ('' ')) (PP (IN of) (NP (NP (NP (DT the) (NNP RIAA) (POS 's)) (NN litigation) (NN campaign)) (PP (IN by) (S (VP (VBG publishing) (NP (DT a) (ADJP (RB highly) (JJ critical)) (, ,) (JJ detailed) (NN report))))) (, ,) (VP (VBN entitled) ('' ') (NP (NNP RIAA)) (PP (IN v.) (NP (NNP The) (NNP People))))))) (: :) (NP (NP (CD Five) (NNS Years)) (RB Later) (POS '))) (, ,) (S (VP (VBG concluding) (SBAR (IN that) (S (NP (DT the) (NN campaign)) (VP (AUX was) (NP (DT a) (NN failure)))))))) (. .)))";
		String term1 = "VP";
		String term2 = "NP";
		int[][] expectedResults = new int[][] { new int[] { 14, 34, 6, 53 },
				new int[] { 14, 34, 6, 53 }, new int[] { 14, 34, 6, 53 },
				new int[] { 14, 34, 6, 53 } };
		int[] expectedNumComparisons = new int[] { 30, 64, 27, 32 };
		Operator op = Operator.PARENT;

		assertJoinWithLookaheads(sent, term1, op, term2, 20, expectedResults,
				expectedNumComparisons);

		assertTermEarlyJoin(sent, term1, op, term2, 20, new int[] { 14, 34, 6,
				53 }, 17);

		assertRegularJoin(sent, term1, op, term2, 20,
				new int[] { 14, 34, 6, 53 }, 26);
	}

	public void testFollowingSibling() throws Exception {
		String sent = "(N(P(P(P(P(N A)(P B))(G C)(N D)(P N)(P E)))(N F)))";
		String term1 = "P";
		String term2 = "N";
		int[][] expectedResults = new int[][] {
				new int[] { 3, 4, 4, 8, 6, 7, 2, 11 },
				new int[] { 3, 4, 4, 8, 6, 7, 2, 11 },
				new int[] { 3, 4, 4, 8 }, new int[] { 6, 7, 2, 11 } };
		int[] expectedNumComparisons = new int[] { 21, 23, 11, 11 };
		Operator op = Operator.FOLLOWING_SIBLING;

		assertJoinWithLookaheads(sent, term1, op, term2, 28, expectedResults,
				expectedNumComparisons);

		assertTermEarlyJoin(sent, term1, op, term2, 28,
				new int[] { 3, 4, 4, 8 }, 10);

		assertRegularJoin(sent, term1, op, term2, 28, new int[] { 3, 4, 4, 8,
				6, 7, 2, 11 }, 23);
	}

	public void testImmediateFollowingSibling() throws Exception {
		String sent = "(N(P(P(P(P(N A)(P B))(P C)(N D)(P N)(P E)))(N F)))";
		String term1 = "P";
		String term2 = "N";
		int[][] expectedResults = new int[][] {
				new int[] { 3, 4, 4, 8, 6, 7, 2, 11 },
				new int[] { 3, 4, 4, 8, 6, 7, 2, 11 },
				new int[] { 3, 4, 4, 8 }, new int[] { 6, 7, 2, 11 } };
		int[] expectedNumComparisons = new int[] { 24, 24, 13, 13 };
		Operator op = Operator.IMMEDIATE_FOLLOWING_SIBLING;

		assertJoinWithLookaheads(sent, term1, op, term2, 32, expectedResults,
				expectedNumComparisons);

		assertTermEarlyJoin(sent, term1, op, term2, 32,
				new int[] { 3, 4, 4, 8 }, 12);

		assertRegularJoin(sent, term1, op, term2, 32, new int[] { 3, 4, 4, 8,
				6, 7, 2, 11 }, 24);
	}

	public void testPrecedingSibling() throws Exception {
		String sent = "(N(P(P(P(P(N A)(P B))(P C)(N D)(P N)(P E)))(N F)))";
		String term1 = "N";
		String term2 = "P";
		int[][] expectedResults = new int[][] { new int[] { 0, 6, 2, 11 },
				new int[] { 0, 2, 4, 8, 2, 3, 4, 8 }, new int[] { 0, 2, 4, 8 },
				new int[] { 2, 3, 4, 8 } };
		int[] expectedNumComparisons = new int[] { 22, 19, 34, 7 };
		Operator op = Operator.PRECEDING_SIBLING;

		assertJoinWithLookaheads(sent, term1, op, term2, 20, expectedResults,
				expectedNumComparisons);

		assertTermEarlyJoin(sent, term1, op, term2, 20,
				new int[] { 0, 6, 2, 11 }, 16);

		assertRegularJoin(sent, term1, op, term2, 20, new int[] { 0, 6, 2, 11,
				0, 2, 4, 8, 2, 3, 4, 8 }, 39);
	}

	public void testImmediatePrecedingSibling() throws Exception {
		String sent = "(N(P(P(P(P(N A)(P B))(P C)(N D)(P N)(P E)))(N F)))";
		String term1 = "N";
		String term2 = "P";
		int[][] expectedResults = new int[][] { new int[] { 0, 6, 2, 11 },
				new int[] { 2, 3, 4, 8 }, new int[] { 2, 3, 4, 8 },
				new int[] { 2, 3, 4, 8 } };
		int[] expectedNumComparisons = new int[] { 22, 20, 37, 7 };
		Operator op = Operator.IMMEDIATE_PRECEDING_SIBLING;

		assertJoinWithLookaheads(sent, term1, op, term2, 20, expectedResults,
				expectedNumComparisons);

		assertTermEarlyJoin(sent, term1, op, term2, 20,
				new int[] { 0, 6, 2, 11 }, 16);

		assertRegularJoin(sent, term1, op, term2, 20, new int[] { 0, 6, 2, 11,
				2, 3, 4, 8 }, 40);
	}

	public void testImmediateFollowing() throws Exception {
		String sent = "(N(P(P(P(P(N A)(P B))(P C)(N D)(P N)(P E)))(N F)))";
		String term1 = "P";
		String term2 = "N";
		int[][] expectedResults = new int[][] {
				new int[] { 3, 4, 4, 8, 6, 7, 2, 11 },
				new int[] { 3, 4, 4, 8, 6, 7, 2, 11 },
				new int[] { 3, 4, 4, 8 }, new int[] { 6, 7, 2, 11 } };
		int[] expectedNumComparisons = new int[] { 24, 21, 13, 1 };
		Operator op = Operator.IMMEDIATE_FOLLOWING;

		assertJoinWithLookaheads(sent, term1, op, term2, 32, expectedResults,
				expectedNumComparisons);

		assertTermEarlyJoin(sent, term1, op, term2, 32,
				new int[] { 3, 4, 4, 8 }, 12);

		assertRegularJoin(sent, term1, op, term2, 32, new int[] { 3, 4, 4, 8,
				6, 7, 2, 11 }, 14);
	}

	public void testImmediatePreceding() throws Exception {
		String sent = "(N(P(P(P(P(N A)(P B))(P C)(N D)(P N)(P E)))(N F)))";
		String term1 = "N";
		String term2 = "P";
		int[][] expectedResults = new int[][] { new int[] { 0, 6, 2, 11 },
				new int[] { 2, 3, 4, 8, 5, 6, 4, 8 }, new int[] { 2, 3, 4, 8 },
				new int[] { 5, 6, 4, 8 } };
		int[] expectedNumComparisons = new int[] { 15, 21, 27, 1 };
		Operator op = Operator.IMMEDIATE_PRECEDING;

		assertJoinWithLookaheads(sent, term1, op, term2, 20, expectedResults,
				expectedNumComparisons);

		assertTermEarlyJoin(sent, term1, op, term2, 20,
				new int[] { 0, 6, 2, 11 }, 9);

		assertRegularJoin(sent, term1, op, term2, 20, new int[] { 0, 6, 2, 11,
				0, 6, 3, 9, 2, 3, 4, 8, 5, 6, 4, 8 }, 23);
	}

	private void assertJoinWithLookaheads(String sent, String term1,
			Operator op, String term2, int expectedTerm1Num,
			int[][] expectedResults, int[] expectedNumComparisons)
			throws IOException {
		for (int i = 0; i < lookaheadOps.length; i++) {
			Operator nextOp = lookaheadOps[i];
			IndexReader r = setupIndexWithDocs(sent);
			DocsAndPositionsEnum posEnum = initPrevGetNext(r, expectedTerm1Num,
					0, term1, term2);
			lookaheadJoinAndAssertOutput(expectedResults[i].length,
					expectedNumComparisons[i], join, prev, op, nextOp, posEnum,
					i);
			int expectedOffset = expectedResults[i].length - 4;
			assertPositions(expectedResults[i], expectedOffset < 0 ? 0
					: expectedOffset, bufferResult);
		}
	}

	private void assertTermEarlyJoin(String sent, String term1, Operator op,
			String term2, int expectedTerm1Num, int[] expectedResults,
			int expectedNumComparisons) throws IOException {
		IndexReader r = setupIndexWithDocs(sent);
		DocsAndPositionsEnum posEnum = initPrevGetNext(r, expectedTerm1Num, 0,
				term1, term2);
		termEarlyJoinAndAssertOutput(expectedResults.length,
				expectedNumComparisons, join, prev, op, posEnum);
		int expectedOffset = expectedResults.length - 4;
		assertPositions(expectedResults, expectedOffset < 0 ? 0
				: expectedOffset, bufferResult);
	}

	private void assertRegularJoin(String sent, String term1, Operator op,
			String term2, int expectedTerm1Num, int[] expectedResults,
			int expectedNumComparisons) throws IOException {
		IndexReader r = setupIndexWithDocs(sent);
		DocsAndPositionsEnum posEnum = initPrevGetNext(r, expectedTerm1Num, 0,
				term1, term2);
		joinAndAssertOutput(expectedResults.length, expectedNumComparisons,
				join, prev, op, posEnum);
		int expectedOffset = expectedResults.length - 4;
		assertPositions(expectedResults, expectedOffset < 0 ? 0
				: expectedOffset, bufferResult);
	}

}
