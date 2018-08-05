package application;

import java.io.IOException;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXScrollPane;
import com.jfoenix.controls.JFXTextField;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import newServer.datagram.ChatMessage;

public class LobbyController {

	@FXML
	private JFXScrollPane lobbyPane;

	@FXML
	private JFXScrollPane chatPane;

	@FXML
	private JFXTextField msgText;

	@FXML
	private FontAwesomeIconView uploadBtn;

	@FXML
	private MaterialDesignIconView sendBtn;

	@FXML
	private JFXListView<User> lobbyList;

	@FXML
	private JFXListView<ChatMessage> chatList;

	public void initialize() {
		System.out.println("test");
		for (int i = 0; i < 10; i++) {
			ChatMessage c = new ChatMessage("hi", Integer.toString(i));
			chatList.getItems().add(c);
		}
		chatList.setSelectionModel(new NoSelectionModel<ChatMessage>());
		chatList.setCellFactory(new Callback<ListView<ChatMessage>, ListCell<ChatMessage>>() {

			@Override
			public ListCell<ChatMessage> call(ListView<ChatMessage> param) {
				return new ChatMessageCell();
			}

		});

	}

	@FXML
	void sendBtnAction(ActionEvent event) {

	}

	@FXML
	void sendMessage(ActionEvent event) {

	}

	@FXML
	void uploadBtnAction(ActionEvent event) {

	}

	static class ChatMessageCell extends ListCell<ChatMessage> {
		AnchorPane anchorPane;
		@Override
		protected void updateItem(ChatMessage item, boolean empty) {
			super.updateItem(item, empty);

			if (empty || item == null) {
				setText(null);
				setGraphic(null);
			} else {
				try {
					anchorPane = FXMLLoader.load(Main_test.class.getResource("/application/reportTemplate.fxml"));
					Label name = (Label) anchorPane.getChildren().get(0);
					Label text = (Label) anchorPane.getChildren().get(1);
					Label time = (Label) anchorPane.getChildren().get(2);

					if (item.getSender().equals("self")) {
						name.setText("You");
					} else {
						name.setText(item.getSender());
					}
					text.setText(item.getTextMsg());
					System.out.println(item.getTimeStamp());
					time.setText(item.getTimeStamp());
					setGraphic(anchorPane);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
