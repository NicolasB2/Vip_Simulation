package com.simulationFramework.GUI.controller;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import com.jfoenix.controls.JFXTimePicker;
import com.simulationFramework.SystemState.SITMFactory.SITMCalendar;
import com.simulationFramework.SystemState.SITMFactory.SITMPlanVersion;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Data;

@Data
public class NewProController {

	private Stage stage;

	private GUIController guiController;

	private String nameProject;

	private BorderPane containerView;

	private AnchorPane formView;

	private AnchorPane csvDataView;

	private AnchorPane variablesView;

	private AnchorPane clockView;

	private AnchorPane planversionsView;

	private AnchorPane dateView;

	private AnchorPane systemVariablesView;

	@FXML
	private Button butCancel;

	@FXML
	private Button butBack;

	@FXML
	private Button butNext;

	@FXML
	private Button butFinish;

	@FXML
	private Button butOpenAssign;

	@FXML
	private Button butAssignAll;
	// ---------Form View---------

	@FXML
	private TextField txtName;

	@FXML
	private CheckBox chCSV;

	@FXML
	private CheckBox chOracle;
	// ---------Data View CSV---------
	@FXML
	private TextField txtDataResourcePath;

	@FXML
	private Button butSelectDataResourcePath;

	@FXML
	private TextField txtSeparator;

	// ----------Data View Oracle---------

	@FXML
	private DatePicker dtStartDate;

	@FXML
	private DatePicker dtEndingDate;

	@FXML
	private JFXTimePicker tpHourStartDate;

	@FXML
	private JFXTimePicker tpHourEndDate;

	@FXML
	private ListView<CheckBox> lvPlanversionIds;

	// ---------------------------------
	@FXML
	private MenuButton nameBusID;

	@FXML
	private MenuButton nameLineID;

	@FXML
	private MenuButton nameGPSx;

	@FXML
	private MenuButton nameGPSy;

	@FXML
	private MenuButton clock;

	private ObservableList<String> listColumName;

	@FXML
	private ListView<String> lvColumnValue;

	@FXML
	private ListView<HeaderPosition> lvColumnName;

	@FXML
	private TextField tfNameUser;

	private HashMap<String, Integer> hashPosition;

	public void initiaize() {

	}

	// Buttons Methods -----------------------------------------

	@FXML
	void butNextAction(ActionEvent event) throws IOException {

		if (containerView.getCenter() == formView) {

			butBack.setDisable(false);

			if (txtName.getText().equals("")) {
				validateFieldEmpty();
			} else {
				if (chCSV.isSelected()) {
					containerView.setCenter(csvDataView);
				} else if (chOracle.isSelected()) {
					containerView.setCenter(planversionsView);
					loadPlanVersionIds(guiController.getPlanversion());

				}
			}

		} else if (containerView.getCenter() == csvDataView) {

			if (txtDataResourcePath.getText().equals("") || txtSeparator.getText().equals("")) {
				validateFieldEmpty();
			} else {
				guiController.loadDataSource(txtDataResourcePath.getText(), txtSeparator.getText());
				containerView.setCenter(planversionsView);

				ArrayList<SITMPlanVersion> planversions = new ArrayList<>();
				for (SITMPlanVersion i : guiController.getPlanversion()) {
					planversions.add(i);
				}

				Collections.sort(planversions, new Comparator<SITMPlanVersion>() {
					public int compare(SITMPlanVersion o1, SITMPlanVersion o2) {
						return o2.getActivationDate().compareTo(o1.getActivationDate());
					}
				});

				loadPlanVersionIds(planversions);
			}

		} else if (containerView.getCenter() == planversionsView) {

			ObservableList<CheckBox> selectedVariables = lvPlanversionIds.getItems();
			long planVersioId = -1;

			for (CheckBox i : selectedVariables) {
				if (i.isSelected()) {
					planVersioId = Long.parseLong(i.getText().split(" ")[1]);
				}
			}
			guiController.getSimController().setPlanVersionID(planVersioId);
			ArrayList<SITMCalendar> calendar = guiController.getSimController().getDateByPlanVersion(planVersioId);

			SITMCalendar initialDate = calendar.get(0);
			SITMCalendar lastDate = calendar.get(calendar.size() - 1);

			dtStartDate.setValue(convertToLocalDateViaSqlDate(initialDate.getOperationDay()));
			dtEndingDate.setValue(convertToLocalDateViaSqlDate(lastDate.getOperationDay()));
			containerView.setCenter(dateView);

		} else if (containerView.getCenter() == dateView) {

			if (tpHourStartDate.getValue() == null || tpHourEndDate.getValue() == null || dtStartDate.getValue() == null
					|| dtEndingDate.getValue() == null) {
				validateFieldEmpty();
			} else {

				String starHour = tpHourStartDate.getValue() + ":00";
				String endingHour = tpHourEndDate.getValue() + ":00";

				String startDate = dtStartDate.getValue().toString();
				String endingDate = dtEndingDate.getValue().toString();

				String startFormat = startDate + " " + starHour;
				String endingFormat = endingDate + " " + endingHour;

				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				try {
					Date initialDate = new Date(dateFormat.parse(startFormat).getTime());
					Date lastDate = new Date(dateFormat.parse(endingFormat).getTime());
					guiController.getSimController().setDates(initialDate, lastDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}

				lvColumnValue.setItems(guiController.getHeaders());
				containerView.setCenter(variablesView);

			}

		} else if (containerView.getCenter() == variablesView) {

			containerView.setCenter(systemVariablesView);
			butNext.setDisable(true);
			butFinish.setDisable(false);

			for (int i = 0; i < lvColumnName.getItems().size(); i++) {

				MenuItem item1 = new MenuItem(lvColumnName.getItems().get(i).getNameHeader());
				MenuItem item2 = new MenuItem(lvColumnName.getItems().get(i).getNameHeader());
				MenuItem item3 = new MenuItem(lvColumnName.getItems().get(i).getNameHeader());
				MenuItem item4 = new MenuItem(lvColumnName.getItems().get(i).getNameHeader());
				MenuItem item5 = new MenuItem(lvColumnName.getItems().get(i).getNameHeader());

				nameBusID.getItems().add(item1);
				nameLineID.getItems().add(item2);
				nameGPSx.getItems().add(item3);
				nameGPSy.getItems().add(item4);
				clock.getItems().add(item5);

				item1.setOnAction(e -> {
					nameBusID.setText(item1.getText());
				});

				item2.setOnAction(e -> {
					nameLineID.setText(item2.getText());
				});

				item3.setOnAction(e -> {
					nameGPSx.setText(item3.getText());
				});

				item4.setOnAction(e -> {
					nameGPSy.setText(item4.getText());
				});

				item5.setOnAction(e -> {
					clock.setText(item5.getText());
				});

			}

		}

	}

	@FXML
	void butBackAction(ActionEvent event) throws IOException {

		if (containerView.getCenter() == csvDataView || containerView.getCenter() == planversionsView) {

			butBack.setDisable(true);
			containerView.setCenter(formView);

		} else if (containerView.getCenter() == variablesView) {

			butFinish.setDisable(true);

			containerView.setCenter(dateView);

		} else if (containerView.getCenter() == dateView) {

			containerView.setCenter(planversionsView);

		} else if (containerView.getCenter() == systemVariablesView) {

			butNext.setDisable(false);
			containerView.setCenter(variablesView);

		}

	}

	@FXML
	void butSelectDataResourcePathAction(ActionEvent event) {

		FileChooser locationChooser = new FileChooser();
		locationChooser.setTitle("Select Location");
		File file = locationChooser.showOpenDialog(stage);

		if (file != null) {
			txtDataResourcePath.setText(file.getAbsolutePath());
		}

	}

	@FXML
	void butFinishAction(ActionEvent event) throws IOException {

		if (nameBusID.getText().equals("") || nameLineID.getText().equals("") || nameGPSx.getText().equals("")
				|| nameGPSy.getText().equals("") || clock.getText().equals("")) {

			validateFieldEmpty();

		} else {

			String[] pAttributes = { txtName.getText() };
			HashMap<String, Integer> positionDirectory = new HashMap<>();
			for (HeaderPosition hp : lvColumnName.getItems()) {
				positionDirectory.put(hp.getNameHeader(), hp.getPositionHeader());

			}

			int tClock = positionDirectory.get(clock.getText());
			int tBus = positionDirectory.get(nameBusID.getText());
			int tLine = positionDirectory.get(nameLineID.getText());
			int tgpsx = positionDirectory.get(nameGPSx.getText());
			int tgpsy = positionDirectory.get(nameGPSy.getText());

			guiController.getSimController().setColumnNumberForSimulationVariables(tClock, tgpsx, tgpsy, tBus, tLine);

			guiController.getSimController().setHeaders(positionDirectory);
			guiController.finishNewProject(pAttributes, positionDirectory);
			stage.close();

		}
	}

	@FXML
	void butCancelAction(ActionEvent event) {

		stage.close();

	}

	// Assign Name
	@FXML
	void butAssignNameColumn(ActionEvent event) throws IOException {

		if (lvColumnValue.getSelectionModel().getSelectedItem() == null) {

			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("");
			alert.setHeaderText(null);
			alert.setContentText("Please select a item");
			alert.showAndWait();

		} else if (tfNameUser.getText().equals("")) {

			validateFieldEmpty();

		} else {

			String newName = tfNameUser.getText();

			HeaderPosition headerPosition = new HeaderPosition(newName,
					lvColumnValue.getSelectionModel().getSelectedIndex());
			lvColumnName.getItems().add(headerPosition);

			tfNameUser.setText("");

		}

	}

	@FXML
	void butAssignAll(ActionEvent event) {

		HeaderPosition headerPosition = null;
		for (int i = 0; i < lvColumnValue.getItems().size(); i++) {
			headerPosition = new HeaderPosition(lvColumnValue.getItems().get(i), i);
			lvColumnName.getItems().add(headerPosition);
		}		

	}

	@FXML
	void onClickCheckCSV(ActionEvent event) {
		chOracle.setSelected(false);
	}

	@FXML
	void onClickOracleCheck(ActionEvent event) {
		chCSV.setSelected(false);
	}

	// -------------------------------------------------------------------

	public void start() throws IOException {
		containerView = (BorderPane) stage.getScene().getRoot();
		butBack.setDisable(true);
		butFinish.setDisable(true);

		FXMLLoader fxmlLoader = new FXMLLoader();
		NewProController newProController = null;

		fxmlLoader = GUIController.loadFXML("NewProView-Form");
		formView = fxmlLoader.load();
		newProController = fxmlLoader.getController();
		txtName = newProController.getTxtName();
		chCSV = newProController.getChCSV();
		chOracle = newProController.getChOracle();

		fxmlLoader = GUIController.loadFXML("NewProView-Data");
		csvDataView = fxmlLoader.load();
		newProController = fxmlLoader.getController();
		txtDataResourcePath = newProController.getTxtDataResourcePath();
		txtSeparator = newProController.getTxtSeparator();

		fxmlLoader = GUIController.loadFXML("NewProView-Variables");
		variablesView = fxmlLoader.load();
		newProController = fxmlLoader.getController();
		lvColumnName = newProController.getLvColumnName();
		lvColumnValue = newProController.getLvColumnValue();
		tfNameUser = newProController.getTfNameUser();
		butOpenAssign = newProController.getButOpenAssign();

		fxmlLoader = GUIController.loadFXML("NewProView-Clock");
		clockView = fxmlLoader.load();
		newProController = fxmlLoader.getController();

		fxmlLoader = GUIController.loadFXML("NewProView-Date");
		dateView = fxmlLoader.load();
		newProController = fxmlLoader.getController();
		dtStartDate = newProController.getDtStartDate();
		dtEndingDate = newProController.getDtEndingDate();
		tpHourStartDate = newProController.getTpHourStartDate();
		tpHourEndDate = newProController.getTpHourEndDate();
		tpHourStartDate.set24HourView(true);
		tpHourEndDate.set24HourView(true);

		fxmlLoader = GUIController.loadFXML("NewProView-Planversions");
		planversionsView = fxmlLoader.load();
		newProController = fxmlLoader.getController();
		lvPlanversionIds = newProController.getLvPlanversionIds();

		fxmlLoader = GUIController.loadFXML("NewProView-Variables-System");
		systemVariablesView = fxmlLoader.load();
		newProController = fxmlLoader.getController();
		nameBusID = newProController.getNameBusID();
		nameLineID = newProController.getNameLineID();
		nameGPSx = newProController.getNameGPSx();
		nameGPSy = newProController.getNameGPSy();
		clock = newProController.getClock();

		containerView.setCenter(formView);
	}

	public void loadPlanVersionIds(Iterable<SITMPlanVersion> planversionIds) {

		for (SITMPlanVersion plan : planversionIds) {

			ArrayList<SITMCalendar> calendar = guiController.getSimController()
					.getDateByPlanVersion(plan.getPlanVersionID());

			SITMCalendar initialDate = calendar.get(0);
			SITMCalendar lastDate = calendar.get(calendar.size() - 1);

			CheckBox check = new CheckBox();
			check.setUserData(plan);
			check.setText("ID: " + plan.getPlanVersionID() + " Dates: [" + initialDate.getOperationDay() + " - "
					+ lastDate.getOperationDay() + "]");
			lvPlanversionIds.getItems().add(check);

		}
	}

	private void validateFieldEmpty() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Empty Fields");
		alert.setHeaderText(null);
		alert.setContentText("The field cannot be empty");
		alert.showAndWait();
	}

	public LocalDate convertToLocalDateViaSqlDate(Date dateToConvert) {
		return new java.sql.Date(dateToConvert.getTime()).toLocalDate();
	}
}
