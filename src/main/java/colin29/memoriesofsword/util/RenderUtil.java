package colin29.memoriesofsword.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class RenderUtil {

	public static void clearGLScreen(Color clearColor) {
		Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT
				| (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
		Gdx.gl.glEnable(GL20.GL_BLEND);
	}

	/**
	 * Convenience function to make new colors from a RBG tuple
	 * 
	 * @return
	 */
	public static Color rgb(int r, int g, int b) {
		return new Color((float) r / 256, (float) g / 256, (float) b / 256, 1);
	}

	/**
	 * Workaround for there being no easy way to draw a background on a label
	 */
	public static void setLabelBackgroundColor(Label label, Color color) {
		LabelStyle style = new LabelStyle(label.getStyle());
		Pixmap labelColor = new Pixmap(1, 1, Pixmap.Format.RGB888);
		labelColor.setColor(color);
		labelColor.fill();
		style.background = new Image(new Texture(labelColor)).getDrawable();
		label.setStyle(style);
	}

	public static Drawable getSolidBG(Color color) {
		Pixmap labelColor = new Pixmap(1, 1, Pixmap.Format.RGB888);
		labelColor.setColor(color);
		labelColor.fill();
		return new Image(new Texture(labelColor)).getDrawable();
	}

}
