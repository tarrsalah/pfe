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
package org.tarrsalah.editor.core.activities;

import org.tarrsalah.editor.core.model.Operation;
import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.mindswap.wsdl.WSDLService;

/**
 * OperationsAsync.java (UTF-8) Jun 5, 2014
 *
 * @author tarrsalah.org
 */
public  class OperationsAsync extends Service<List<Operation>> {

    private final StringProperty wsdlURL = new SimpleStringProperty(this, "wsdlURL");

    public String getWsdlURL() {
        return wsdlURL.getValue();
    }

    public void setWsdlURL(String wsdlURL) {
        this.wsdlURL.setValue(wsdlURL);
    }

    public StringProperty wsdlURLProperty() {
        return wsdlURL;
    }

    @Override
    protected Task<List<Operation>> createTask() {
        return new Task<List<Operation>>() {
            @Override
            protected List<Operation> call() throws Exception {
                return WSDLService.createService(wsdlURL.getValue()).getOperations()
                        .stream()
                        .map(Operation::wrap)
                        .collect(Collectors.toList());
            }
        };
    }
}
