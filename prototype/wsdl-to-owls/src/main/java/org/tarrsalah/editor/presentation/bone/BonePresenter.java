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
package org.tarrsalah.editor.presentation.bone;

import org.tarrsalah.editor.core.activities.OwlsTemplate;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javax.inject.Inject;
import org.mindswap.wsdl.WSDLOperation;
import org.tarrsalah.editor.core.activities.OWLSGeneratorAsync;
import org.tarrsalah.editor.core.activities.OperationsAsync;
import org.tarrsalah.editor.core.model.Operation;
import org.tarrsalah.editor.core.model.Parameter;
import org.tarrsalah.editor.presentation.ViewUtils;

/**
 * FXML Controller class
 *
 * @author tarrsalah.org
 */
public class BonePresenter implements Initializable {

    private static final Logger LOG = Logger.getLogger(BonePresenter.class.getName());
    public final static ExecutorService executor = Executors.newCachedThreadPool();

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab generatorTab, browserTab;

    @FXML
    private MenuItem saveOwlsFile;

    @FXML
    private TextField wsdlURL;

    @FXML
    private Button viewOWLS;

    @FXML
    private TextField serviceName, logicalURI;

    @FXML
    private TextArea description;

    @FXML
    private ComboBox<WSDLOperation> services;

    @FXML
    private TableView<Parameter> inputs;

    @FXML
    private TableView<Parameter> outputs;

    @FXML
    private TableView namespaces;

    @FXML
    private TableColumn namespaceAbbr, namespaceURL;

    @FXML
    private Button remove, saveOwlsFileButton;

    @FXML
    private WebView browser;

    @FXML
    private ProgressIndicator progress;

    @Inject
    private OperationsAsync operationsAsync;

    @Inject
    private OwlsTemplate template;

    private final ObservableBooleanValue empty = new SimpleBooleanProperty(true);

    private final Map<WSDLOperation, Operation> currentOperationsMap= new HashMap<>();    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        viewOWLS.disableProperty().bind(services.valueProperty().isNull());
        services.disableProperty().bind(services.valueProperty().isNull());
        saveOwlsFile.disableProperty().bind(generatorTab.selectedProperty().and(empty));
        remove.disableProperty().bind(namespaces.selectionModelProperty().isNotNull());

        operationsAsync.setExecutor(executor);
        operationsAsync.wsdlURLProperty().bind(wsdlURL.textProperty());
        operationsAsync.setOnSucceeded(this::handleOperationsAsyncSuccess);
        operationsAsync.setOnFailed(this::handleOperationsAsyncFail);
        ViewUtils.bindWorkerToProgressIndicator(operationsAsync, progress);

        Stream.of(inputs, outputs).forEach(
                tableView -> {
                    tableView.getColumns().forEach((TableColumn c) -> {
                        c.setCellFactory(TextFieldTableCell.<Parameter>forTableColumn());
                    });
                }
        );

        namespaceAbbr.prefWidthProperty().bind(namespaces.widthProperty().multiply(0.25));
        namespaceURL.prefWidthProperty().bind(namespaces.widthProperty().multiply(0.75));

        services.setConverter(new StringConverter<WSDLOperation>() {
            @Override
            public String toString(WSDLOperation operation) {
                return operation.getName();
            }

            @Override
            public WSDLOperation fromString(String name) {
                return services.getItems().stream()
                        .filter(service
                                -> (service.getName() == null
                                ? name == null
                                : service.getName().equals(name)))
                        .findFirst()
                        .get();
            }
        });
    }

    private void handleOperationsAsyncSuccess(WorkerStateEvent event) {
        currentOperationsMap.clear();
        operationsAsync.getValue()
                .parallelStream()
                .forEach(operation -> {
                    currentOperationsMap.put(operation.getOperation(), operation);
                });
        final List<WSDLOperation> operations = currentOperationsMap
                .keySet()
                .parallelStream()
                .collect(Collectors.toList());

        services.setItems(FXCollections.observableArrayList(operations));
        operations.stream().findFirst().ifPresent(services::setValue);
        this.handleServices();
        LOG.log(Level.INFO,
                () -> {
                    return String.join(" ", "Fetching from",
                            operationsAsync.getWsdlURL(),
                            " succeeded");
                });
    }

    @SuppressWarnings("ThrowableResultIgnored")
    private void handleOperationsAsyncFail(WorkerStateEvent event) {
        services.getItems().clear();
        currentOperationsMap.clear();
        LOG.warning(
                () -> {
                    return "Fetcher Service task failed "
                    + operationsAsync.getException().getMessage();

                });
    }

    @FXML
    private void handleClose() {
        Platform.exit();
    }

    @FXML
    private void handleImportWsdl() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a WSDL file");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("WSDL Files", "*.wsdl"));
        File selectedFile = fileChooser.showOpenDialog(
                (Stage) generatorTab.getTabPane().getParent().getScene().getWindow());
        if (Objects.nonNull(selectedFile)) {
            wsdlURL.setText(selectedFile.toURI().toString());
            tabPane.getSelectionModel().select(generatorTab);
            handleWsdlUrl();
        }
    }

    // code that smells
    @FXML
    private void handleViewOwls() {
        final Operation operation = currentOperationsMap
                .get(services.selectionModelProperty()
                        .getValue()
                        .getSelectedItem());

        final OWLSGeneratorAsync generatorAsync = OWLSGeneratorAsync.builder()
                .name(serviceName.getText().trim())
                .description(description.getText().trim())
                .logicalURL(logicalURI.getText().trim())
                .operation(operation)
                .executor(executor)
                .build();

        ViewUtils.bindWorkerToProgressIndicator(generatorAsync, progress);        
        generatorAsync.setOnSucceeded((event -> {
            try {
                final String title = operation.getOperation().getName().concat(".owls");

                Optional<String> owls = Files.lines(generatorAsync.getValue().toPath(),
                        Charset.forName("UTF-8")).
                        reduce((String line1, String line2) -> line1 + "\n" + line2);

                browser.getEngine().loadContent(template.parse(owls.orElse("")));
                browserTab.setText(title);
                tabPane.getSelectionModel().select(browserTab);
                saveOwlsFileButton.setDisable(false);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }));
        generatorAsync.start();
    }

    @FXML
    private void handleWsdlUrl() {
        switch (operationsAsync.getState()) {
            case READY:
                operationsAsync.start();
                break;
            default:
                operationsAsync.restart();
        }
    }

    @FXML
    private void handleServices() {
        final SingleSelectionModel<WSDLOperation> currentOpeation
                = services.selectionModelProperty().getValue();

        if (currentOpeation.isEmpty()) {
            serviceName.clear();
            description.clear();
            inputs.getItems().clear();
            outputs.getItems().clear();

        } else {
            WSDLOperation operation = currentOpeation.getSelectedItem();
            serviceName.setText(Objects.requireNonNull(operation.getName(),
                    "Default name"));
            description.setText(Objects.requireNonNull(operation.getDescription(),
                    "Default description"));
            inputs.itemsProperty().bind(new SimpleObjectProperty<>(currentOperationsMap.get(operation).getInputs()));
            outputs.itemsProperty().bind(new SimpleObjectProperty<>(currentOperationsMap.get(operation).getOutputs()));
        }
        Stream.of(inputs, outputs).forEach(ViewUtils::refreshTableView);
    }

    @FXML
    private void handleSaveOWLSFile() {
        LOG.info("handleSaveOWLSFile");
    }
}
