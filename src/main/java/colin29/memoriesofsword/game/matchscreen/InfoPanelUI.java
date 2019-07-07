package colin29.memoriesofsword.game.matchscreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import colin29.memoriesofsword.game.match.CardInfo;
import colin29.memoriesofsword.game.match.FollowerCard;
import colin29.memoriesofsword.game.match.Permanent;
import colin29.memoriesofsword.game.matchscreen.MatchScreen.PromptContext;
import colin29.memoriesofsword.game.matchscreen.graphics.HandCardGraphic;
import colin29.memoriesofsword.game.matchscreen.graphics.PermanentGraphic;
import colin29.memoriesofsword.util.RenderUtil;
import colin29.memoriesofsword.util.UIUtil;

public class InfoPanelUI {

	static class InfoTable extends Table {
		public final InfoPanelUI.InfoTableElements elements = new InfoPanelUI.InfoTableElements();
	}

	static class InfoTableElements {
		Label origEffectsText;
		Table appliedEffectsPanel;
	}

	private final MatchScreen parent;
	Table infoPanel; // shows detailed information about a clicked permanent

	public InfoPanelUI(MatchScreen parent) {
		this.parent = parent;
	}

	void createAndDisplayInfoPanel(PermanentGraphic graphic) {
		removeInfoPanel();
		infoPanel = createInfoPanel(graphic.getPermanent());
	}

	void createAndDisplayInfoPanel(HandCardGraphic graphic) {
		removeInfoPanel();
		infoPanel = createInfoPanel(graphic.getParentCard());
	}

	private Table createInfoPanel(Permanent<?> permanent) {
		InfoPanelUI.InfoTable infoTable = createInfoTableSkeleton(permanent.getParentCard());
		infoTable.elements.origEffectsText.setText(permanent.generateOrigEffectsText());
		return infoTable;
	}

	private Table createInfoPanel(CardInfo card) {
		InfoPanelUI.InfoTable infoTable = createInfoTableSkeleton(card);
		infoTable.elements.origEffectsText.setText(card.generateOrigEffectsText());
		return infoTable;
	}

	/**
	 * Creates the shape of the info table and fills in all the areas in common (Selected target can be Card or Permanent)
	 * 
	 * @param card:
	 *            If the selected target is a card, that card. If the selected targeted is a permanent, its parent card.
	 * 
	 * @return References to table elements so that callers can fill in the rest
	 */
	private InfoTable createInfoTableSkeleton(final CardInfo card) {

		int infoPanelWidth = 300;

		InfoPanelUI.InfoTable rootTemp = new InfoPanelUI.InfoTable();

		rootTemp.setFillParent(true);
		parent.stage.addActor(rootTemp);

		Table info = new Table();
		info.setBackground(RenderUtil.getSolidBG(Color.DARK_GRAY));

		LabelStyle largishStyle = UIUtil.createLabelStyle(parent.fonts.largishFont());
		Label nameText = new Label(card.getName(), largishStyle);

		Table statsRow = new Table();
		statsRow.defaults().uniform().fill().spaceRight(10);
		statsRow.left();

		int statLabelWidth = 15;

		Label costText = parent.handUI.parent.createColoredLabel(String.valueOf(card.getCost()), largishStyle, parent.FOREST, Align.center);

		// generateOrigEffectsText()

		statsRow.defaults().width(statLabelWidth);

		statsRow.add(costText);

		if (card instanceof FollowerCard) {
			FollowerCard cardFol = (FollowerCard) card;
			Label atkText = parent.handUI.parent.createColoredLabel(String.valueOf(cardFol.getAtk()), largishStyle, parent.DARK_BLUE, Align.center);
			Label defText = parent.handUI.parent.createColoredLabel(String.valueOf(cardFol.getDef()), largishStyle, parent.DARK_RED, Align.center);

			statsRow.add(atkText);
			statsRow.add(defText);
		}

		statsRow.row();

		info.defaults().space(5);
		info.pad(10).left();
		info.defaults().left();

		info.add(nameText).row();
		info.add(statsRow).row();

		Label cardText = parent.handUI.parent.createColoredLabel("", largishStyle, Color.BLACK, Align.left);

		cardText.setWrap(true);
		info.add(cardText).expandX().fillX();

		Table effectsPanel = new Table(); // technically only for applied effects
		effectsPanel.setBackground(RenderUtil.getSolidBG(Color.DARK_GRAY));

		Label sampleEffectText = parent.handUI.parent.createColoredLabel("{Applied effects show up here}", largishStyle, Color.BLACK, Align.left);

		effectsPanel.pad(10).left();
		effectsPanel.defaults().space(5).expandX().fillX();
		effectsPanel.add(sampleEffectText).row();

		rootTemp.top().left().pad(10);
		rootTemp.defaults().space(10).left().uniformX().fillX();
		rootTemp.add(info).top().width(infoPanelWidth).left().row();
		rootTemp.add(effectsPanel);

		rootTemp.elements.origEffectsText = cardText;
		rootTemp.elements.appliedEffectsPanel = effectsPanel;

		return rootTemp;
	}

	void makeClickShowInfoPanel(final MatchScreen matchScreen, PermanentGraphic graphic) {
		graphic.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (matchScreen.promptContext == PromptContext.IDLE) {
					createAndDisplayInfoPanel(graphic);
				}
			}
		});
	}

	void makeClickShowInfoPanel(final MatchScreen matchScreen, HandCardGraphic graphic) {
		graphic.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (matchScreen.promptContext == PromptContext.IDLE) {
					createAndDisplayInfoPanel(graphic);
				}
			}
		});
	}

	/**
	 * OK if info panel is already null
	 * 
	 */
	void removeInfoPanel() {
		if (infoPanel != null) {
			infoPanel.remove();
			infoPanel = null;
		}
	}

}
