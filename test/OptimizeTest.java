
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

import org.junit.Test;

import pt.up.fe.comp.TestUtils;
import pt.up.fe.comp.jmm.ollir.OllirResult;
import pt.up.fe.specs.util.SpecsIo;

public class OptimizeTest {

    /* OLLIR Tests */

    @Test
    public void testHelloWorld() {
        OllirResult result = TestUtils.optimize(SpecsIo.getResource("fixtures/public/HelloWorld.jmm"));
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testSimple() {
        OllirResult result = TestUtils.optimize(SpecsIo.getResource("fixtures/public/Simple.jmm"));
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testOperatorPrecedence() {
        OllirResult result = TestUtils.optimize(SpecsIo.getResource("fixtures/public/OperatorPrecedence.jmm"));
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testFac() {
        OllirResult result = TestUtils.optimize(SpecsIo.getResource("fixtures/public/Fac.jmm"));
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testFindMaximum() {
        OllirResult result = TestUtils.optimize(SpecsIo.getResource("fixtures/public/FindMaximum.jmm"));
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testLazySort() {
        OllirResult result = TestUtils.optimize(SpecsIo.getResource("fixtures/public/Lazysort.jmm"));
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testLife() {
        OllirResult result = TestUtils.optimize(SpecsIo.getResource("fixtures/public/Life.jmm"));
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testMonteCarloPi() {
        OllirResult result = TestUtils.optimize(SpecsIo.getResource("fixtures/public/MonteCarloPi.jmm"));
        TestUtils.noErrors(result.getReports());
    }
    
    @Test
    public void testMyClass() {
        OllirResult result = TestUtils.optimize(SpecsIo.getResource("fixtures/public/MyClass.jmm"));
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testTicTacToe() {
        OllirResult result = TestUtils.optimize(SpecsIo.getResource("fixtures/public/TicTacToe.jmm"));
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testWhileAndIf() {
        OllirResult result = TestUtils.optimize(SpecsIo.getResource("fixtures/public/WhileAndIF.jmm"));
        TestUtils.noErrors(result.getReports());
    }

    @Test
    public void testMyClass2() {
        OllirResult result = TestUtils.optimize(SpecsIo.getResource("fixtures/public/ollir/myclass2.jmm"));
        TestUtils.noErrors(result.getReports());
    }
}
