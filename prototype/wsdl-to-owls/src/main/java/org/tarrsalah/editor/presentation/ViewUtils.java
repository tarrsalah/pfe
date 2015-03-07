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
package org.tarrsalah.editor.presentation;

import javafx.concurrent.Worker;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;

/**
 *
 * @author tarrsalah.org
 */
public class ViewUtils {

    // refreshTableView the mother fucker ! BUG 
    static public void refreshTableView(TableView<?> table) {
        table.getColumns().get(0).setVisible(false);
        table.getColumns().get(0).setVisible(true);

    }

    static public void bindWorkerToProgressIndicator(Worker worker,
            ProgressIndicator indicator) {
        worker.runningProperty().addListener(
                (o, oldV, newV) -> {
                    if (newV) {
                        indicator.setPrefHeight(20);
                        indicator.setPrefWidth(20);
                    } else {
                        indicator.setPrefHeight(0);
                        indicator.setPrefWidth(0);
                    }
                }
        );
    }
}
