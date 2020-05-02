package colin29.memoriesofsword.util;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;

public class PrefixingAssetManager extends AssetManager {

	public final String prefixPath;

	public PrefixingAssetManager(String prefix) {
		this.prefixPath = prefix;
	}

	// Only need this method, as all overloaded versions delegate to here
	@Override
	public synchronized <T> void load(String fileName, Class<T> type, AssetLoaderParameters<T> parameter) {
		super.load(prefixPath + fileName, type, parameter);
	}

	@Override
	public synchronized <T> T get(String fileName) {
		if (fileName.startsWith(prefixPath)) {
			return super.get(fileName);
		} else {
			return super.get(prefixPath + fileName);
		}
	}

	@Override
	public synchronized <T> T get(String fileName, Class<T> type) {
		if (fileName.startsWith(prefixPath)) {
			return super.get(fileName, type);
		} else {
			return super.get(prefixPath + fileName, type);
		}

	}

	@Override
	public synchronized boolean isLoaded(String fileName) {
		return super.isLoaded(prefixPath + fileName);
	}

	@Override
	public synchronized boolean isLoaded(String fileName, Class type) {
		return super.isLoaded(prefixPath + fileName, type);
	}

}
