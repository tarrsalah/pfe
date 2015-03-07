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
 * LIABILITY, WHETHER IN AN ACTION OF CONTRoACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.tarrsalah.editor;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.tarrsalah.editor.presentation.bone.BonePresenter;
import org.tarrsalah.editor.presentation.bone.BoneView;

/**
 * App.java (UTF-8)
 *
 * May 31, 2014
 *
 * @author tarrsalah.org
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(new BoneView().getView());
        stage.setScene(scene);
        stage.setTitle("WSDL to OWLS converter");
        stage.setMinWidth(1000);
        stage.setWidth(1000);
        stage.setHeight(1000);
        stage.getIcons().setAll(
                new Image(getResourceLoc("icons/flower-seed-icon-16.png")),
                new Image(getResourceLoc("icons/flower-seed-icon-32.png")),
                new Image(getResourceLoc("icons/flower-seed-icon-64.png")),
                new Image(getResourceLoc("icons/flower-seed-icon-128.png")),
                new Image(getResourceLoc("icons/flower-seed-icon-256.png")),
                new Image(getResourceLoc("icons/flower-seed-icon-512.png"))
        );
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        BonePresenter.executor.shutdownNow();
    }

    private String getResourceLoc(String name) {
        return getClass().getClassLoader().getResource(name).toExternalForm();
    }
}
