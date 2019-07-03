package colin29.memoriesofsword.util.template;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import colin29.memoriesofsword.MyFonts;

/**
 * Just a filtered interface for use by screens, who only need to access an GDX app for the resources it holds
 *
 */
public interface AppWithResources {
	public SpriteBatch getBatch();

	public ShapeRenderer getShapeRenderer();

	public AssetManager getAssets();

	public MyFonts getFonts();

	public Skin getSkin();
}
