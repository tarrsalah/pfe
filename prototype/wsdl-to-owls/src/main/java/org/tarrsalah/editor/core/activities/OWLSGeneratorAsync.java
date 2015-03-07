/*
 * The MIT License
 *
 * Copyright 2014 tarrsalah.
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLOntology;
import org.mindswap.owls.vocabulary.OWLS;
import org.mindswap.wsdl.WSDLTranslator;
import org.tarrsalah.editor.core.model.Operation;
import org.tarrsalah.editor.core.model.Parameter;

/**
 *
 * @author tarrsalah
 */
public final class OWLSGeneratorAsync extends Service<File> {

    private static final Logger LOG = Logger.getLogger(OWLSGeneratorAsync.class.getName());

    private final String logicalURL;
    private final String name;
    private final String description;
    private final Operation operation;

    private OWLSGeneratorAsync(String logicalURL, String name, String description, Operation operation) {
        this.logicalURL = logicalURL;
        this.name = name;
        this.description = description;
        this.operation = operation;
    }

    public static class Builder {

        private String logicalURL;
        private String name;
        private String description;
        private Operation operation;
        private Executor executor;

        public Builder logicalURL(String logicalURL) {
            this.logicalURL = logicalURL;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder operation(Operation operation) {
            this.operation = operation;
            return this;
        }

        public Builder executor(Executor executor) {
            this.executor = executor;
            return this;
        }

        public OWLSGeneratorAsync build() {
            final OWLSGeneratorAsync generatorAsync = new OWLSGeneratorAsync(logicalURL, name, description, operation);
            generatorAsync.setExecutor(executor);
            return generatorAsync;
        }

    }

    public static Builder builder() {
        return new Builder();
    }

    private final BiConsumer<WSDLTranslator, Parameter> addParameter = (translator, parameter) -> {
        translator.addInput(parameter.getInitialParameter(),
                parameter.getWsdlName(),
                URI.create("http://www.jiji.org"),
                parameter.getXslt());
    };

    @Override
    protected Task<File> createTask() {
        return new Task<File>() {

            @Override
            protected File call() throws IOException {
                File file = File.createTempFile("_" + String.valueOf(System.currentTimeMillis()), "owls");
                file.deleteOnExit();

                final OWLOntology ontology = OWLFactory.createKB().createOntology(URI.create(logicalURL));
                OWLS.addOWLSImports(ontology);

                final WSDLTranslator translator = new WSDLTranslator(ontology,
                        operation.getOperation(), name);

                translator.setServiceName(name);
                translator.setTextDescription(description);

                operation.getInputs().forEach(parameter -> {
                    addParameter.accept(translator, parameter);
                });

                operation.getOutputs().forEach(parameter -> {
                    addParameter.accept(translator, parameter);
                });
                translator.writeOWLS(new FileOutputStream(file));
                return file;
            }
        };
    }
}
