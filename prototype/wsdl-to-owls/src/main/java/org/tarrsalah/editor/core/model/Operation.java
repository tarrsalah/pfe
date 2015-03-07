/*
 * The MIT License
 *
 * Copyright 2014 tarrsalah.org.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.tarrsalah.editor.core.model;

import java.util.Objects;
import javafx.collections.ObservableList;
import org.mindswap.wsdl.WSDLOperation;

/**
 * Operation.java (UTF-8) Jun 19, 2014
 *
 * Operation represent a (JavaFx) observable WSDL operation
 *
 * @author tarrsalah.org
 */
public class Operation {

    private final WSDLOperation operation;
    private ObservableList<Parameter> inputs;
    private ObservableList<Parameter> outputs;

    private Operation(WSDLOperation operation) {
        this.operation = operation;
    }

    public static Operation wrap(WSDLOperation operation) {
        return new Operation(operation);
    }

    public WSDLOperation getOperation() {
        return operation;
    }

    public synchronized ObservableList<Parameter> getInputs() {
        if (Objects.isNull(inputs)) {
            inputs = Parameter.getInputParams(operation);
        }
        return inputs;
    }

    public synchronized ObservableList<Parameter> getOutputs() {
        if (Objects.isNull(outputs)) {
            outputs = Parameter.getOutputParams(operation);
        }
        return outputs;
    }
}
