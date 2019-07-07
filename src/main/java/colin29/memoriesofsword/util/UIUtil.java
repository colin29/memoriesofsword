package colin29.memoriesofsword.util;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class UIUtil {

	/**
	 * Creates a label style, retaining the original font color
	 */
	public static LabelStyle createLabelStyle(BitmapFont font) {
		return new LabelStyle(font, font.getColor());
	}

}
