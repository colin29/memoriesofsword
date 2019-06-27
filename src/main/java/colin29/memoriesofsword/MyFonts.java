package colin29.memoriesofsword;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class MyFonts {
	BitmapFont debugFont;
	BitmapFont borderedSmallFont;
	BitmapFont borderedMediumFont;

	BitmapFont font;
	BitmapFont font_goth12;
	BitmapFont font_goth24;

	BitmapFont smallFont;
	BitmapFont moderateFont;

	private final String MS_GOTHIC_path = "fonts/MS_Gothic.ttf";
	private final String OPEN_SANS_path = "fonts/OpenSans.ttf";

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
	}

	BitmapFont generateFontWithBorder(String path, Color color, int size, Color borderColor,
			float borderWidth) {
		FreeTypeFontGenerator.setMaxTextureSize(FreeTypeFontGenerator.NO_MAXIMUM);
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(App.ASSETS_PATH + path));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = size;
		parameter.borderWidth = borderWidth;
		parameter.borderColor = borderColor;
		parameter.color = color;
		BitmapFont font = generator.generateFont(parameter);
		generator.dispose();
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
}
