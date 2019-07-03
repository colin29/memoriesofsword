
package colin29.memoriesofsword;

import java.io.File;
import java.io.FilenameFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.kotcrab.vis.ui.VisUI;

import colin29.memoriesofsword.game.CardRepository;
import colin29.memoriesofsword.game.match.SandBoxMatchDriver;
import colin29.memoriesofsword.util.PrefixingAssetManager;
import colin29.memoriesofsword.util.template.AppWithResources;

public class App extends Game implements AppWithResources {

	Logger logger = LoggerFactory.getLogger(this.getClass());

	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;

	private CardRepository cardRepo = new CardRepository();

	private static Skin skin;

	public static final String ASSETS_PATH = "assets/";
	public final MyFonts fonts = new MyFonts();
	AssetManager assets;

	@Override
	public Skin getSkin() {
		return skin;
	}

	@Override
	public void create() {

		fonts.init();

		VisUI.load();
		skin = VisUI.getSkin();

		assets = new PrefixingAssetManager(ASSETS_PATH);
		loadImages();
		assets.finishLoading();

		this.batch = new SpriteBatch();
		this.shapeRenderer = new ShapeRenderer();

		TestCardListings.addTestCardsToRepo(cardRepo);

		new SandBoxMatchDriver(this, cardRepo);
	}

	private void loadImages() {
		// Load all jpg files in this directory
		File dir = new File(App.ASSETS_PATH + "img/");

		File[] images = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".jpg");
			}
		});

		for (File image : images) {
			TextureParameter param = new TextureParameter();
			param.genMipMaps = true;
			assets.load("img/" + image.getName(), Texture.class, param);
		}
		logger.debug("Loaded " + images.length + " images");
	}

	@Override
	public SpriteBatch getBatch() {
		return batch;
	}

	@Override
	public ShapeRenderer getShapeRenderer() {
		return shapeRenderer;
	}

	@Override
	public AssetManager getAssets() {
		return assets;
	}

	@Override
	public MyFonts getFonts() {
		return fonts;
	}

}
