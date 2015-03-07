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

import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.xml.namespace.QName;
import org.mindswap.owl.vocabulary.OWL;
import org.mindswap.owl.vocabulary.XSD;
import org.mindswap.utils.QNameProvider;
import org.mindswap.utils.URIUtils;
import org.mindswap.wsdl.WSDLConsts;
import org.mindswap.wsdl.WSDLOperation;
import org.mindswap.wsdl.WSDLParameter;

/**
 * Parameter.java (UTF-8)
 *
 * JavaFX Bean backed by a WSDLParameter
 *
 * Jun 9, 2014
 *
 * @author tarrsalah.org
 */
public class Parameter {

    private static final QNameProvider qnames = new QNameProvider();

    private final WSDLParameter initialParameter;
    
    private final StringProperty wsdlName = new SimpleStringProperty();
    private final StringProperty wsdlType = new SimpleStringProperty();
    private final StringProperty owlsName = new SimpleStringProperty();
    private final StringProperty owlsType = new SimpleStringProperty();
    private final StringProperty xslt = new SimpleStringProperty();

    public Parameter(WSDLParameter parameter) {
        // set the names
        this.initialParameter = parameter;

        wsdlName.setValue(URIUtils.getLocalName(parameter.getName()));

        owlsName.setValue(wsdlName.getValue());

        // set the wsdlType
        QName defaultWsdlType = parameter.getType();
        QName paramType = (defaultWsdlType == null)
                ? new QName(WSDLConsts.xsdURI, "any")
                : defaultWsdlType;

        wsdlType.setValue(qnames.shortForm(paramType.getNamespaceURI()
                + "#"
                + paramType.getLocalPart()));

        String defaultOwlsType = OWL.Thing.toString();
        if (paramType.getNamespaceURI().equals(WSDLConsts.soapEnc)
                || (paramType.getNamespaceURI().equals(WSDLConsts.xsdURI)
                && !paramType.getLocalPart().equals("any"))) {
            defaultOwlsType = XSD.ns + paramType.getLocalPart();
        }

        owlsType.setValue(qnames.shortForm(defaultOwlsType));

        xslt.setValue(" "); //None
    }

    // Properties
    public StringProperty wsdlNameProperty() {
        return wsdlName;
    }

    public StringProperty wsdlTypeProperty() {
        return wsdlType;
    }

    public StringProperty owlsNameProperty() {
        return owlsName;
    }

    public StringProperty owlsTypeProperty() {
        return owlsType;
    }

    public StringProperty xsltProperty() {
        return xslt;
    }

    // getters 
    public String getWsdlName() {
        return wsdlName.getValue();
    }

    public String getWsdlType() {
        return wsdlType.getValue();
    }

    public String getOwlsName() {
        return owlsName.getValue();
    }

    public String getOwlsType() {
        return owlsType.getValue();
    }

    public String getXslt() {
        return xslt.getValue();
    }

    // setters
    public void setWsdlName(String wsdlName) {
        this.wsdlName.setValue(wsdlName);
    }

    public void setWsdlType(String wsdlType) {
        this.wsdlType.setValue(wsdlType);
    }

    public void setOwlsName(String owlsName) {
        this.owlsName.setValue(owlsName);
    }

    public void setOwlsType(String owlsType) {
        this.owlsType.setValue(owlsType);
    }

    public void setXslt(String xslt) {
        this.xslt.setValue(xslt);
    }

    public WSDLParameter getInitialParameter() {
        return initialParameter;
    }

    @Override
    public String toString() {
        return "Parameter{" + "wsdlName=" + wsdlName.getValue()
                + ", wsdlType=" + wsdlType.getValue()
                + ", owlsName=" + owlsName.getValue()
                + ", owlsType=" + owlsType.getValue()
                + ", xslt=" + xslt.getValue() + '}';
    }

    public static ObservableList<Parameter> getInputParams(WSDLOperation operation) {
        return getParams(operation.getInputs());
    }

    public static ObservableList<Parameter> getOutputParams(WSDLOperation operation) {
        return getParams(operation.getOutputs());
    }

    private static ObservableList<Parameter> getParams(List<WSDLParameter> parameters) {
        return FXCollections.observableArrayList(parameters.stream().parallel().map(Parameter::new).collect(Collectors.toList()));
    }
}
