package colin29.memoriesofsword.game.matchscreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import colin29.memoriesofsword.MyFonts;
import colin29.memoriesofsword.game.match.Match;
import colin29.memoriesofsword.util.RenderUtil;
import colin29.memoriesofsword.util.template.AppWithResources;

class UIConstructor {

	private final MatchScreen parent;
	private Stage stage;

	private Skin skin;

	private MyFonts fonts;

	private final Match match;

	UIConstructor(MatchScreen parent, AppWithResources app) {
		this.parent = parent;
		this.stage = parent.getStage();

		match = parent.match;

		fonts = app.getFonts();
		skin = app.getSkin();
	}

	private int sidePanelWidth = 150;

	PlayerPartitionUIElements[] initializeUIElementsRef() {
		PlayerPartitionUIElements[] elements = new PlayerPartitionUIElements[2];
		for (int playerNumber = 1; playerNumber <= 2; playerNumber++) {
			elements[playerNumber - 1] = new PlayerPartitionUIElements(parent.getOutlineRenderer(), playerNumber);
		}
		return elements;
	}

	/**
	 * Also Initializes fields which refer to UI elements, thus regenerate/refresh calls (including those from a listener) should not be invoked
	 * before this method is called.
	 */
	void constructUI(PlayerPartitionUIElements[] elements) {

		Table root = new Table();

		constructPlayerPartition(root, elements, 2);
		constructPlayerPartition(root, elements, 1); // Player 1's area should be on the bottom

		parent.handUI.makeValidHandCardsDraggable();

		root.pack();
		root.setFillParent(true);
		stage.addActor(root);

	}

	private void constructPlayerPartition(Table root, PlayerPartitionUIElements[] playersElements, int playerNumber) {

		final boolean normalOrientation = playerNumber == 1; // normal orientation has your hand on the bottom of your area

		PlayerPartitionUIElements elements = playersElements[playerNumber - 1];

		Table playerPartition = new Table();

		root.add(playerPartition).height(Value.percentHeight(0.5f, root)).expandX().fill();
		root.row();

		playerPartition.add(constructSidePanel(elements, playerNumber)).width(sidePanelWidth).expandY().fill();
		Table mainArea = new Table();
		playerPartition.add(mainArea).expand().fill();

		TextButtonStyle bigTextButtonStyle = skin.get(TextButtonStyle.class);
		bigTextButtonStyle.font = fonts.largishFont();

		TextButton endTurnButton = new TextButton("End Turn", bigTextButtonStyle);
		endTurnButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (playerNumber == match.getActivePlayerNumber()) {
					match.nextTurn();
				}

			}
		});
		elements.endTurnButton = endTurnButton;
		parent.miscUI.updateEndTurnButtonDisabledStatus(playerNumber);
		endTurnButton.pad(20, 10, 20, 10);

		Table fieldPanel = new Table();
		fieldPanel.defaults().space(20);
		TargetableTable handPanel = new TargetableTable(match.getPlayer(playerNumber));

		if (normalOrientation) {
			mainArea.add(fieldPanel).expand().fill();
			mainArea.add(endTurnButton);
			mainArea.row();
			mainArea.add(handPanel).expandX().colspan(2).fill();
		} else {
			mainArea.add(handPanel).expandX().colspan(2).fill();
			mainArea.row();
			mainArea.add(fieldPanel).expand().fill();
			mainArea.add(endTurnButton);
		}
		handPanel.setTouchable(Touchable.enabled);
		handPanel.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				parent.userPrompter.onTargetableActorClicked(handPanel);
			}

		});

		elements.setHandPanel(handPanel);
		elements.fieldPanel = fieldPanel;

		parent.handUI.regenerateHandDisplay(playerNumber);
		parent.fieldUI.regenerateFieldDisplay(playerNumber);

		fieldPanel.setTouchable(Touchable.enabled);

	}

	private Table constructSidePanel(PlayerPartitionUIElements elements, int playerNumber) {
		Table sidePanel = new Table();
		sidePanel.defaults().expandX().left();

		Color DARK_GRAY = RenderUtil.rgb(40, 40, 40);
		Color DARK_RED = RenderUtil.rgb(128, 0, 0);

		Label nameText = new Label(match.getPlayer(playerNumber).getName(), skin);
		RenderUtil.setLabelBackgroundColor(nameText, DARK_GRAY);

		Label hpText = new Label("initial hp text", skin);
		RenderUtil.setLabelBackgroundColor(hpText, DARK_RED);

		Label aTwoDigitLabel = new Label("00", skin); // example label for sizing purposes
		int hpTextPadding = 8;
		Container<Label> hpTextWrapper = new Container<Label>(hpText);
		hpTextWrapper.size(aTwoDigitLabel.getWidth() + hpTextPadding,
				hpText.getHeight() + hpTextPadding);
		hpText.setAlignment(Align.center);

		Label playPointsText = new Label("intial pp text", skin);
		RenderUtil.setLabelBackgroundColor(playPointsText, DARK_GRAY);

		Table zoneCountPanel = new Table(); // contains counts for current graveyard, deck, and hand size
		zoneCountPanel.defaults().left().padLeft(5).padRight(5).spaceLeft(10);
		zoneCountPanel.pad(5);
		zoneCountPanel.setBackground(RenderUtil.getSolidBG(DARK_GRAY));

		Label graveyardCountText = new Label("", skin);
		Label deckCountText = new Label("", skin);
		Label handCountText = new Label("", skin);

		zoneCountPanel.add(new Label("Graveyard:", skin));
		zoneCountPanel.add(graveyardCountText).row();
		zoneCountPanel.add(new Label("Deck:", skin));
		zoneCountPanel.add(deckCountText).row();
		zoneCountPanel.add(new Label("Hand:", skin));
		zoneCountPanel.add(handCountText);

		// add ui elements to panel

		sidePanel.defaults().padLeft(5).padRight(5).spaceTop(5);

		sidePanel.add(nameText);
		sidePanel.row();
		sidePanel.add(hpTextWrapper).center();
		sidePanel.row();
		sidePanel.add(playPointsText).center();
		sidePanel.row();
		sidePanel.add(zoneCountPanel).expandX();

		// store reference to elements that need to be updated

		elements.hpText = hpText;
		elements.playPointsText = playPointsText;

		elements.graveyardCountText = graveyardCountText;
		elements.deckCountText = deckCountText;
		elements.handCountText = handCountText;

		parent.miscUI.updateHpText(playerNumber);
		parent.miscUI.updatePlayPointsText(playerNumber);
		parent.miscUI.updateZoneCountTexts(playerNumber);

		return sidePanel;
	}

}
