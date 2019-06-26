package colin29.memoriesofsword.util.template;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Just a filtered interface for use by screens, who only need to access an GDX app for the resources it holds
 *
 */
public interface AppWithResources {
	public SpriteBatch getBatch();

	public ShapeRenderer getShapeRenderer();

	public AssetManager getAssets();
}
