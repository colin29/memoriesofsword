package colin29.memoriesofsword.game.matchscreen;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import colin29.memoriesofsword.MyFonts;
import colin29.memoriesofsword.game.match.CardInfo;
import colin29.memoriesofsword.game.match.FollowerCard;
import colin29.memoriesofsword.game.matchscreen.graphics.HandCardGraphic;
import colin29.memoriesofsword.util.RenderUtil;

public class HandUI {

	private final MatchScreen parent;

	private final MyFonts fonts;

	public HandUI(MatchScreen parent) {
		this.parent = parent;
		fonts = parent.app.getFonts();
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

	HandCardGraphic createHandCardGraphic(CardInfo card) {

		HandCardGraphic cardGraphic = new HandCardGraphic(card);
		Table cardHeader = new Table();
		Table cardBody = new Table();

		LabelStyle smallStyle = createLabelStyle(fonts.smallFont());
		LabelStyle mediumStyle = createLabelStyle(fonts.mediumFont());
		LabelStyle largishStyle = createLabelStyle(fonts.smallFont());

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

		parent.makeClickShowInfoPanel(cardGraphic);
		return cardGraphic;
	}

	/**
	 * Creates a label style, retaining the original font color
	 */
	LabelStyle createLabelStyle(BitmapFont font) {
		return new LabelStyle(font, font.getColor());
	}

}
