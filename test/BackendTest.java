
/**
 * Copyright 2021 SPeCS.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License. under the License.
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.analysis.JmmSemanticsResult;
import pt.up.fe.specs.util.SpecsIo;

public class BackendTest {

    /* Semantic Analysis Tests */

    @Test
    public void testFindMaximum() {
        JmmSemanticsResult result = TestUtils.analyse(SpecsIo.getResource("fixtures/public/FindMaximum.jmm"));
        assertTrue(result.getReports().isEmpty());
    }

    @Test
    public void testHelloWorld() {
        JmmSemanticsResult result = TestUtils.analyse(SpecsIo.getResource("fixtures/public/HelloWorld.jmm"));
        assertTrue(result.getReports().isEmpty());
    }

    @Test
    public void testLazysort() {
        JmmSemanticsResult result = TestUtils.analyse(SpecsIo.getResource("fixtures/public/Lazysort.jmm"));
        assertTrue(result.getReports().isEmpty());
    }

    @Test
    public void testLife() {
        JmmSemanticsResult result = TestUtils.analyse(SpecsIo.getResource("fixtures/public/Life.jmm"));
        assertTrue(result.getReports().isEmpty());
    }
    
    @Test
    public void testMonteCarloPi() {
        JmmSemanticsResult result = TestUtils.analyse(SpecsIo.getResource("fixtures/public/MonteCarloPi.jmm"));
        assertTrue(result.getReports().isEmpty());
    }

    @Test
    public void testTicTacToe() {
        JmmSemanticsResult result = TestUtils.analyse(SpecsIo.getResource("fixtures/public/TicTacToe.jmm"));
        assertTrue(result.getReports().isEmpty());
    }

   @Test
    public void testWhileAndIf() {
        JmmSemanticsResult result = TestUtils.analyse(SpecsIo.getResource("fixtures/public/WhileAndIF.jmm"));
        assertTrue(result.getReports().isEmpty());
    }

    /* Files with errors */

    @Test
    public void testArrIndexNotInt() {
        JmmSemanticsResult result = TestUtils.analyse(SpecsIo.getResource("fixtures/public/fail/semantic/arr_index_not_int.jmm"));
        assertEquals(result.getReports().size(), 1);
    }

    @Test
    public void testArrSizeNotInt() {
        JmmSemanticsResult result = TestUtils.analyse(SpecsIo.getResource("fixtures/public/fail/semantic/arr_size_not_int.jmm"));
        assertEquals(result.getReports().size(), 1);
    }
    
    @Test
    public void testBadArguments() {
        JmmSemanticsResult result = TestUtils.analyse(SpecsIo.getResource("fixtures/public/fail/semantic/badArguments.jmm"));
        assertEquals(result.getReports().size(), 2);
    }

    @Test
    public void testBinopIncomp() {
        JmmSemanticsResult result = TestUtils.analyse(SpecsIo.getResource("fixtures/public/fail/semantic/binop_incomp.jmm"));
        assertEquals(result.getReports().size(), 2);
    }

    @Test
    public void testFuncNotFound() {
        JmmSemanticsResult result = TestUtils.analyse(SpecsIo.getResource("fixtures/public/fail/semantic/funcNotFound.jmm"));
        assertEquals(result.getReports().size(), 1);
    }
    
    @Test
    public void testSimpleLength() {
        JmmSemanticsResult result = TestUtils.analyse(SpecsIo.getResource("fixtures/public/fail/semantic/simple_length.jmm"));
        assertEquals(result.getReports().size(), 2);
    }

    @Test
    public void testVarExpIncomp() {
        JmmSemanticsResult result = TestUtils.analyse(SpecsIo.getResource("fixtures/public/fail/semantic/var_exp_incomp.jmm"));
        assertEquals(result.getReports().size(), 1);
    }

    @Test
    public void testVarLitIncomp() {
        JmmSemanticsResult result = TestUtils.analyse(SpecsIo.getResource("fixtures/public/fail/semantic/var_lit_incomp.jmm"));
        assertEquals(result.getReports().size(), 1);
    }

    @Test
    public void testVarUndef() {
        JmmSemanticsResult result = TestUtils.analyse(SpecsIo.getResource("fixtures/public/fail/semantic/var_undef.jmm"));
        assertEquals(result.getReports().size(), 2);
    }

    @Test
    public void testMissType() {
        JmmSemanticsResult result = TestUtils.analyse(SpecsIo.getResource("fixtures/public/fail/semantic/extra/miss_type.jmm"));
        assertEquals(result.getReports().size(), 1);
    }

}
