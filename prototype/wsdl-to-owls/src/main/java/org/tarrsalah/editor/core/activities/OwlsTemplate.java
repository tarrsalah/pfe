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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;

public class OwlsTemplate {

    private static final Logger LOG = Logger.getLogger(OwlsTemplate.class.getName());
    private final String JS_FILE = "file:///home/tarrsalah/src/github.com/tarrsalah/wsdl2owlsFX/src/main/resources/prettify.js";
    private final String CSS_FILE = "file:///home/tarrsalah/src/github.com/tarrsalah/wsdl2owlsFX/src/main/resources/prettify.css";
    
    public volatile File current;
    private final String template
            =String.join("",                 
            "<!doctype html>",                                 
             "<html>",
             "<head>",
             "<style>",
             "${css}",
             "</style>",
             "<script>",
             "${js}",
             "</script>",
             "</head>",
             "<body onload=\"prettyPrint()\">",
             "<pre class=\"prettyprint lang-xml\">",
             "<code>",
             "${content}",
             "</code>",
             "</pre>",
             "</body>",
             "</html>");

    
    
    @FXML
    public void handleSaveFileContent() {
        LOG.info(this.current.getName());
    }

    // TO be Improved
    public String parse(String owls) {
        try {
            return template.replace("${content}", owls.replace("<", "&lt;")
                    .replace(">", "&gt;"))
                    .replace("${js}",
                            Files.lines(Paths.get(URI.create(JS_FILE)
                                    ))
                            .reduce((line1, line2) -> line1 + "\n" + line2).orElse(""))
                    .replace("${css}",
                            Files.lines(Paths.get(URI.create(CSS_FILE)
                                    ))
                            .reduce((line1, line2) -> line1 + "\n" + line2).orElse(""));
        } catch (IOException ex) {
            Logger.getLogger(OwlsTemplate.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }   
}
