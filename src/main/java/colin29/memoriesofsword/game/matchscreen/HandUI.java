package colin29.memoriesofsword.game.matchscreen;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import colin29.memoriesofsword.MyFonts;
import colin29.memoriesofsword.game.match.Card;
import colin29.memoriesofsword.game.match.CardInfo;
import colin29.memoriesofsword.game.match.FollowerCard;
import colin29.memoriesofsword.game.match.Player;
import colin29.memoriesofsword.game.matchscreen.graphics.HandCardGraphic;
import colin29.memoriesofsword.util.RenderUtil;
import colin29.memoriesofsword.util.UIUtil;

class HandUI {

	final MatchScreen parent;

	private final MyFonts fonts;

	private DragAndDrop dragAndDrop = new DragAndDrop();

	public HandUI(MatchScreen parent) {
		this.parent = parent;
		fonts = parent.app.getFonts();
	}

	HandCardGraphic createHandCardGraphic(CardInfo card) {

		HandCardGraphic cardGraphic = new HandCardGraphic(card);
		Table cardHeader = new Table();
		Table cardBody = new Table();

		LabelStyle smallStyle = UIUtil.createLabelStyle(fonts.smallFont());
		LabelStyle mediumStyle = UIUtil.createLabelStyle(fonts.mediumFont());
		LabelStyle largishStyle = UIUtil.createLabelStyle(fonts.smallFont());

		// Set the background card art
		Texture img = parent.assets.get("img/image01.jpg", Texture.class);
		TextureRegionDrawable imgDrawable = new TextureRegionDrawable(new TextureRegion(img));
		cardBody.setBackground(imgDrawable);

		// make the cost symbol, etc.

		Label costText = new Label(String.valueOf(card.getCost()), largishStyle);
		costText.setAlignment(Align.top);

		Table costPanel = new Table();
		costPanel.add(costText);
		costPanel.setBackground(RenderUtil.getSolidBG(Color.FOREST));

		Label nameText = new Label(card.getName(), smallStyle);
		nameText.setEllipsis(true);
		nameText.setEllipsis("..");

		RenderUtil.setLabelBackgroundColor(nameText, Color.GRAY);

		cardBody.top().left();

		cardBody.add(cardHeader).expandX().fillX().row();

		if (card instanceof FollowerCard) {

			Color DARK_BLUE = RenderUtil.rgb(51, 51, 204);
			Color DARK_RED = RenderUtil.rgb(128, 0, 0);

			FollowerCard cardFol = (FollowerCard) card;
			Label atkText = new Label(String.valueOf(cardFol.getAtk()), mediumStyle);
			RenderUtil.setLabelBackgroundColor(atkText, DARK_BLUE);
			Label defText = new Label(String.valueOf(cardFol.getDef()), mediumStyle);
			RenderUtil.setLabelBackgroundColor(defText, DARK_RED);

			Table cardAtkDefColumn = new Table();
			cardAtkDefColumn.defaults().left();
			cardAtkDefColumn.add(atkText).row();
			cardAtkDefColumn.add(defText).row();

			cardBody.add(cardAtkDefColumn).left();
		}

		cardHeader.defaults().top();

		final int costPanelWidth = 15;
		cardHeader.add(costPanel).width(costPanelWidth).fill();
		cardHeader.add(nameText).width(parent.cardGraphicWidth - costPanelWidth);

		cardGraphic.add(cardBody).height(parent.cardGraphicHeight);

		parent.infoUI.makeClickShowInfoPanel(cardGraphic);
		return cardGraphic;
	}

	void regenerateHandDisplay(int playerNumber) {
		PlayerPartitionUIElements elements = parent.getUIElements(playerNumber);

		elements.listOfHandGraphics.clear();

		Table handPanel = elements.getHandPanel();
		handPanel.clearChildren();

		List<CardInfo> p1CardsInHand = parent.match.getPlayer(playerNumber).getHand();
		for (CardInfo card : p1CardsInHand) {
			HandCardGraphic cardGraphic = createHandCardGraphic(card);
			cardGraphic.setTouchable(Touchable.enabled);
			handPanel.add(cardGraphic).width(parent.cardGraphicWidth);

			elements.listOfHandGraphics.add(cardGraphic);
		}
	}

	void makeValidHandCardsDraggable() {
		// Currently, cards in hand can be dragged on their owner's turn
		// and the player has enough pp to play them
		dragAndDrop.clear();

		for (int playerNumber = 1; playerNumber <= 2; playerNumber++) {
			List<HandCardGraphic> graphics = parent.getUIElements(playerNumber).listOfHandGraphics;

			Player activePlayer = parent.match.getActivePlayer();

			for (HandCardGraphic graphic : graphics) {
				CardInfo card = graphic.getParentCard();
				if (activePlayer == card.getOwner() &&
						activePlayer.getPlayPoints() >= card.getCost()) {
					parent.handUI.makeDraggable(parent, graphic);
					parent.outlineRenderer.startDrawingMyOutline(graphic);
				} else {
					parent.outlineRenderer.stopDrawingMyOutline(graphic);
				}
			}
		}

		parent.handUI.addFieldsAsDragTargets();
	}

	void disableValidHandCardsDraggable() {
		dragAndDrop.clear();
		for (int playerNumber = 1; playerNumber <= 2; playerNumber++) {
			List<HandCardGraphic> graphics = parent.getUIElements(playerNumber).listOfHandGraphics;
			for (HandCardGraphic graphic : graphics) {
				parent.outlineRenderer.stopDrawingMyOutline(graphic);
			}
		}

	}

	private void addFieldsAsDragTargets() {
		for (int i = 1; i <= 2; i++) {
			final int playerNumber = i;
			Table fieldPanel = parent.getUIElements(playerNumber).fieldPanel;
			dragAndDrop.addTarget(new Target(fieldPanel) {

				Drawable coloredBG = RenderUtil.getSolidBG(Color.DARK_GRAY);

				private boolean isValidTarget(Payload payload) {
					Card card = (Card) payload.getObject();
					return parent.match.getPlayer(playerNumber) == card.getOwner(); // can only drop cards onto their owner's field
				}

				@Override
				public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
					if (isValidTarget(payload)) {

						fieldPanel.background(coloredBG);
						return true;
					}

					return false;
				}

				/** Called when the payload is no longer over the target, whether because the touch was moved or a drop occurred. */
				@Override
				public void reset(Source source, Payload payload) {
					fieldPanel.background((Drawable) null);
				}

				@Override
				public void drop(Source source, Payload payload, float x, float y, int pointer) {
					if (isValidTarget(payload)) {
						Card card = (Card) payload.getObject();
						card.getOwner().playCard(card);
					}

				}
			});
		}
	}

	private void makeDraggable(final MatchScreen matchScreen, HandCardGraphic cardGraphic) { // cards are cast by dragging to the field
		dragAndDrop.addSource(new Source(cardGraphic) {

			@Override
			public Payload dragStart(InputEvent event, float x, float y, int pointer) {
				Payload payload = new Payload();

				payload.setObject(cardGraphic.getParentCard());

				// Make a seperate temporary graphic based on the same card
				Container<HandCardGraphic> dragGraphic = new Container<HandCardGraphic>(
						createHandCardGraphic(cardGraphic.getParentCard()))
								.width(matchScreen.cardGraphicWidth);

				payload.setDragActor(dragGraphic);
				payload.setInvalidDragActor(dragGraphic);
				payload.setValidDragActor(dragGraphic);
				return payload;
			}
		});
	}

}
