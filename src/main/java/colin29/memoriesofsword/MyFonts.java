package colin29.memoriesofsword;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import colin29.memoriesofsword.util.RenderUtil;

public class MyFonts {
	BitmapFont debugFont;
	BitmapFont borderedSmallFont;
	BitmapFont borderedMediumFont;

	BitmapFont font;
	BitmapFont font_goth12;
	BitmapFont font_goth24;

	BitmapFont smallFont;
	BitmapFont moderateFont;

	BitmapFont largeThickFont;
	BitmapFont borderedLargeRedThickFont;
	BitmapFont borderedLargeGreenThickFont;
	BitmapFont thinBorderedLargeWhiteThickFont;

	private final String MS_GOTHIC_path = "fonts/MS_Gothic.ttf";
	private final String OPEN_SANS_path = "fonts/OpenSans.ttf";
	private final String ALTE_HAAS_path = "fonts/alte-haas-grotesk.bold.ttf";

	final String ASSETS_PATH = App.ASSETS_PATH;

	BitmapFont largishFont;

	MyFonts() {
	}

	public void init() {

		borderedSmallFont = generateFontWithBorder(OPEN_SANS_path, Color.WHITE, 12, Color.BLACK, 1f);
		borderedMediumFont = generateFontWithBorder(OPEN_SANS_path, Color.WHITE, 16, Color.BLACK, 1.5f);

		font = new BitmapFont(); // Default Arial font.
		font_goth12 = generateFont("fonts/MS_Gothic.ttf", Color.WHITE, 12);
		font_goth24 = generateFont("fonts/MS_Gothic.ttf", Color.WHITE, 24);

		smallFont = generateFont(OPEN_SANS_path, Color.WHITE, 12);
		moderateFont = generateFont(OPEN_SANS_path, Color.WHITE, 15);
		largishFont = generateFont(OPEN_SANS_path, Color.WHITE, 18);

		largeThickFont = generateFont(ALTE_HAAS_path, Color.WHITE, 25);

		Color MODERATE_RED = RenderUtil.rgb(240, 0, 0);
		Color MODERATE_GREEN = RenderUtil.rgb(115, 230, 0);

		thinBorderedLargeWhiteThickFont = generateFontWithBorder(ALTE_HAAS_path, Color.WHITE, 25, Color.BLACK, 0.6f);
		borderedLargeRedThickFont = generateFontWithBorder(ALTE_HAAS_path, MODERATE_RED, 25, Color.WHITE, 0.9f);
		borderedLargeGreenThickFont = generateFontWithBorder(ALTE_HAAS_path, MODERATE_GREEN, 25, Color.BLACK, 0.9f);

	}

	/**
	 * Note for borderered text, changing font color now changes the border color, not the original
	 */
	BitmapFont generateFontWithBorder(String path, Color color, int size, Color borderColor,
			float borderWidth) {
		FreeTypeFontGenerator.setMaxTextureSize(FreeTypeFontGenerator.NO_MAXIMUM);
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(ASSETS_PATH + path));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = size;
		parameter.borderWidth = borderWidth;
		parameter.borderColor = borderColor;
		parameter.color = color;
		BitmapFont font = generator.generateFont(parameter);
		generator.dispose();

		font.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);

		return font;
	}

	BitmapFont generateFont(String path, Color color, int size) {

		FreeTypeFontGenerator.setMaxTextureSize(FreeTypeFontGenerator.NO_MAXIMUM);
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(App.ASSETS_PATH + path));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = size;
		// HoloUI.addJapaneseCharacters(parameter);
		parameter.color = color;
		BitmapFont font = generator.generateFont(parameter);
		generator.dispose();
		return font;
	}

	public BitmapFont borderedMediumFont() {
		return borderedMediumFont;
	}

	public BitmapFont borderedSmallFont() {
		return borderedSmallFont;
	}

	public BitmapFont smallFont() {
		return smallFont;
	}

	public BitmapFont mediumFont() {
		return moderateFont;
	}

	public BitmapFont largishFont() {
		return largishFont;
	}

	public BitmapFont permanentStatsBorderedText() {
		return thinBorderedLargeWhiteThickFont;
	}

	public BitmapFont damagedFollowerDefText() {
		return borderedLargeRedThickFont;
	}

	public BitmapFont buffedFollowerDefText() {
		return borderedLargeGreenThickFont;
	}

}
