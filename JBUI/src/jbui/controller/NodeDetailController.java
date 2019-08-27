package jbui.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import jbui.model.IdSystemNode;

public class NodeDetailController // NO_UCD
{
	private static int LEFT_MSG_INFO_COLUMN_INDEX = 0;

	// Workarounds observed GridPane dynamic row height reduction inability
	private static double MSG_IMAGE_TO_TEXT_HEIGHT_FIT_RATIO = 2.2;

	private static int MSGS_GROUPS_ID_COLUMN_INDEX = 4;

	private static int RIGHT_MSG_INFO_COLUMN_INDEX = 2;

	@FXML
	private GridPane mMsgFlowGridPane;

	@FXML
	private ScrollPane mMsgFlowScrollPane;

	@FXML
	private StackPane mMsgFlowStackPane;

	void init(IdSystemNode modelNode)
	{
		Background background = new Background(Arrays.asList(), null);
		mMsgFlowStackPane.setBackground(background);
		LinkedList<GridPane> msgInfoGridPaneList = new LinkedList<>();
		int msgInfoChanSide = LEFT_MSG_INFO_COLUMN_INDEX;
		boolean isEvenMsgGrup = true;
		IdSystemNode.MsgElement prevMsgElem = null;

		do
		{
			int curSeqFirstMsgIndex = msgInfoGridPaneList.size();

			// Build the message sequences that interact with the communication channel
			for (IdSystemNode.MsgElement msgElem : modelNode.mMsgElemSequences)
			{
				GridPane gridPane = new GridPane();
				Label signatureLabel = new Label(msgElem.mSignature);
				signatureLabel.getStyleClass().add("signatureLabel");
				GridPane.setHalignment(signatureLabel, HPos.CENTER);
				gridPane.add(signatureLabel, 1, 0);
				Label msgLabel = new Label(msgElem.mMsg);
				msgLabel.getStyleClass().add("msgLabel");
				GridPane.setValignment(msgLabel, VPos.CENTER);
				gridPane.add(msgLabel, 1, 1);
				ImageView image;

				// Check if we must use default sides for the message info box.
				// When the previous message has same signature than current, it means both
				// belong to the same principal, so we keep the side for good.
				if (prevMsgElem != null && !msgElem.mSignature.equals(prevMsgElem.mSignature))
				{
					if (msgInfoChanSide == LEFT_MSG_INFO_COLUMN_INDEX)
					{
						msgInfoChanSide = RIGHT_MSG_INFO_COLUMN_INDEX;
					}
					else
					{
						msgInfoChanSide = LEFT_MSG_INFO_COLUMN_INDEX;
					}
				}

				if (msgInfoChanSide == LEFT_MSG_INFO_COLUMN_INDEX)
				{
					gridPane.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
				}

				if (msgElem.mIsSend)
				{
					image = new ImageView("/jbui/resource/send_arrow.png");

					if (msgInfoChanSide == RIGHT_MSG_INFO_COLUMN_INDEX)
					{
						image.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
					}
				}
				else
				{
					image = new ImageView("/jbui/resource/recv_arrow.png");

					if (msgInfoChanSide == LEFT_MSG_INFO_COLUMN_INDEX)
					{
						image.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
					}
				}

				gridPane.add(image, 0, 1);
				image.setPreserveRatio(true);
				image.setFitHeight(msgLabel.getFont().getSize() * MSG_IMAGE_TO_TEXT_HEIGHT_FIT_RATIO);
				mMsgFlowGridPane.add(gridPane, msgInfoChanSide, msgInfoGridPaneList.size());
				msgInfoGridPaneList.add(gridPane);
				gridPane.getStyleClass().add("msgInfoGridPane");
				prevMsgElem = msgElem;
			}

			if (curSeqFirstMsgIndex > 0)
			{
				Color color = isEvenMsgGrup ? Color.WHITE : Color.LIGHTGRAY;

				msgInfoGridPaneList.get(curSeqFirstMsgIndex).localToParentTransformProperty()
						.addListener((observable, oldTransform, newTransform) ->
						{
							double startY = newTransform.getTy();

							if (startY > 0)
							{
								Insets insets = new Insets(startY, 0, 0, 0);
								BackgroundFill bgFill = new BackgroundFill(color, null, insets);
								List<BackgroundFill> bgFills = new ArrayList<>(
										mMsgFlowStackPane.getBackground().getFills());
								bgFills.add(bgFill);
								Background newBackground = new Background(bgFills, null);
								mMsgFlowStackPane.setBackground(newBackground);
							}
						});
			}

			// Build the related node ID indicator
			Label label = new Label(modelNode.unparseIdUnspaced());
			label.getStyleClass().add("msgGroupIdLabel");
			VBox vBox = new VBox(label);
			vBox.setAlignment(Pos.CENTER);
			mMsgFlowGridPane.add(vBox, MSGS_GROUPS_ID_COLUMN_INDEX, curSeqFirstMsgIndex, 1,
					msgInfoGridPaneList.size() - curSeqFirstMsgIndex);

			isEvenMsgGrup = !isEvenMsgGrup;
		}
		while ((modelNode = modelNode.getParent()) != null);
	}

	@FXML
	private void initialize()
	{
		ControllerUtil.setPanningHandScrollPaneCursors(mMsgFlowScrollPane);
	}
}